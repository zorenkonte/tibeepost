import { useCallback, useEffect, useMemo, useState } from 'react'
import { normalizeHost, type Device, type DeviceStatus } from '../lib/devices'
import { readStored, writeStored } from '../lib/storage'
import { checkHealth } from '../lib/tvApi'

const DEVICES_KEY = 'tibeepost.devices'
const SELECTED_KEY = 'tibeepost.selectedDevices'
const POLL_MS = 5000

export function useDevices() {
  const [devices, setDevices] = useState<Device[]>(() => readStored<Device[]>(DEVICES_KEY, []))
  const [selectedIds, setSelectedIds] = useState<string[]>(() => readStored<string[]>(SELECTED_KEY, []))
  const [statuses, setStatuses] = useState<Record<string, DeviceStatus>>({})

  useEffect(() => writeStored(DEVICES_KEY, devices), [devices])
  useEffect(() => writeStored(SELECTED_KEY, selectedIds), [selectedIds])

  useEffect(() => {
    let cancelled = false
    const poll = async () => {
      await Promise.all(
        devices.map(async (device) => {
          try {
            const health = await checkHealth(device)
            if (!cancelled) {
              setStatuses((current) => ({
                ...current,
                [device.id]: { reachability: 'online', version: health.version, checkedAt: Date.now() },
              }))
            }
          } catch {
            if (!cancelled) {
              setStatuses((current) => ({ ...current, [device.id]: { reachability: 'offline', checkedAt: Date.now() } }))
            }
          }
        }),
      )
    }
    void poll()
    const timer = setInterval(() => void poll(), POLL_MS)
    return () => {
      cancelled = true
      clearInterval(timer)
    }
  }, [devices])

  const upsert = useCallback((device: Device) => {
    const normalized = { ...device, host: normalizeHost(device.host), name: device.name.trim(), token: device.token.trim() }
    setDevices((current) => {
      const index = current.findIndex((d) => d.id === normalized.id)
      if (index === -1) return [...current, normalized]
      return current.map((d) => (d.id === normalized.id ? normalized : d))
    })
    setSelectedIds((current) => (current.includes(normalized.id) ? current : [...current, normalized.id]))
  }, [])

  const remove = useCallback((id: string) => {
    setDevices((current) => current.filter((d) => d.id !== id))
    setSelectedIds((current) => current.filter((selected) => selected !== id))
    setStatuses((current) => {
      const next = { ...current }
      delete next[id]
      return next
    })
  }, [])

  const toggleSelected = useCallback((id: string, selected: boolean) => {
    setSelectedIds((current) => (selected ? [...new Set([...current, id])] : current.filter((s) => s !== id)))
  }, [])

  const selectedDevices = useMemo(
    () => devices.filter((device) => selectedIds.includes(device.id)),
    [devices, selectedIds],
  )

  return { devices, statuses, selectedIds, selectedDevices, upsert, remove, toggleSelected }
}

export type DevicesStore = ReturnType<typeof useDevices>
