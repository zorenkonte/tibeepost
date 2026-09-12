import { Button, Chip, SectionTitle, SurfaceCut, useToast } from '@cladd-ui/react'
import { useState } from 'react'
import type { DevicesStore } from '../hooks/useDevices'
import type { Device } from '../lib/devices'
import { toWire, validatePayload, type NotificationPayload } from '../lib/payload'
import { clearNotification, sendNotification } from '../lib/tvApi'

export interface DeviceOutcome {
  deviceId: string
  deviceName: string
  ok: boolean
  detail: string
}

export interface SendOutcome {
  payload: NotificationPayload
  outcomes: DeviceOutcome[]
}

interface SendPanelProps {
  payload: NotificationPayload
  devices: DevicesStore
  onSent?: (outcome: SendOutcome) => void
}

async function deliver(device: Device, payload: NotificationPayload): Promise<DeviceOutcome> {
  try {
    const result = await sendNotification(device, toWire(payload))
    return { deviceId: device.id, deviceName: device.name, ok: true, detail: `${result.result} (id ${result.id})` }
  } catch (error) {
    return { deviceId: device.id, deviceName: device.name, ok: false, detail: error instanceof Error ? error.message : String(error) }
  }
}

async function clear(device: Device, id: string): Promise<DeviceOutcome> {
  try {
    const result = await clearNotification(device, id)
    return { deviceId: device.id, deviceName: device.name, ok: true, detail: result.result }
  } catch (error) {
    return { deviceId: device.id, deviceName: device.name, ok: false, detail: error instanceof Error ? error.message : String(error) }
  }
}

export function SendPanel({ payload, devices, onSent }: SendPanelProps) {
  const toast = useToast()
  const [busy, setBusy] = useState(false)
  const [outcomes, setOutcomes] = useState<DeviceOutcome[]>([])
  const targets = devices.selectedDevices
  const validation = validatePayload(payload)
  const cardId = payload.id.trim()

  const run = async (action: (device: Device) => Promise<DeviceOutcome>, label: string, record: boolean) => {
    setBusy(true)
    const results = await Promise.all(targets.map(action))
    setOutcomes(results)
    setBusy(false)
    const failed = results.filter((r) => !r.ok).length
    toast({
      title: failed === 0 ? `${label} to ${results.length} device${results.length === 1 ? '' : 's'}` : `${failed} of ${results.length} failed`,
      text: failed === 0 ? undefined : results.filter((r) => !r.ok).map((r) => `${r.deviceName}: ${r.detail}`).join('\n'),
      color: failed === 0 ? 'green' : 'red',
    })
    if (record) onSent?.({ payload, outcomes: results })
  }

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center justify-between gap-3">
        <SectionTitle>Send</SectionTitle>
        <span className="text-cladd-xs text-cladd-fg-soft">
          {targets.length === 0 ? 'Select at least one device in the Devices tab' : `${targets.length} device${targets.length === 1 ? '' : 's'} selected`}
        </span>
      </div>
      <div className="flex flex-wrap gap-2">
        <Button
          color="green"
          loading={busy}
          disabled={busy || targets.length === 0 || validation !== null}
          onClick={() => void run((device) => deliver(device, payload), 'Sent', true)}
        >
          Send notification
        </Button>
        <Button
          color="neutral"
          disabled={busy || targets.length === 0 || cardId === ''}
          onClick={() => void run((device) => clear(device, cardId), 'Cleared', false)}
        >
          Clear card by ID
        </Button>
      </div>
      {validation && <span className="text-cladd-xs text-cladd-red">{validation}</span>}
      {outcomes.length > 0 && (
        <SurfaceCut className="p-2">
          <div className="flex flex-col gap-1">
            {outcomes.map((outcome) => (
              <div key={outcome.deviceId} className="flex items-center gap-2 text-cladd-xs">
                <Chip size="xs" color={outcome.ok ? 'green' : 'red'}>
                  {outcome.ok ? 'OK' : 'Failed'}
                </Chip>
                <span className="font-medium">{outcome.deviceName}</span>
                <span className="truncate text-cladd-fg-soft">{outcome.detail}</span>
              </div>
            ))}
          </div>
        </SurfaceCut>
      )}
    </div>
  )
}
