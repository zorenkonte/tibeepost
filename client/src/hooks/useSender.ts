import { useToast } from '@cladd-ui/react'
import { useCallback, useState } from 'react'
import type { Device } from '../lib/devices'
import { toWire, type NotificationPayload } from '../lib/payload'
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

function describe(error: unknown): string {
  return error instanceof Error ? error.message : String(error)
}

async function deliver(device: Device, payload: NotificationPayload): Promise<DeviceOutcome> {
  try {
    const result = await sendNotification(device, toWire(payload))
    return { deviceId: device.id, deviceName: device.name, ok: true, detail: `${result.result} (id ${result.id})` }
  } catch (error) {
    return { deviceId: device.id, deviceName: device.name, ok: false, detail: describe(error) }
  }
}

async function dismiss(device: Device, id: string): Promise<DeviceOutcome> {
  try {
    const result = await clearNotification(device, id)
    return { deviceId: device.id, deviceName: device.name, ok: true, detail: result.result }
  } catch (error) {
    return { deviceId: device.id, deviceName: device.name, ok: false, detail: describe(error) }
  }
}

export function useSender(targets: Device[], onSent?: (outcome: SendOutcome) => void) {
  const toast = useToast()
  const [busy, setBusy] = useState(false)
  const [outcomes, setOutcomes] = useState<DeviceOutcome[]>([])

  const run = useCallback(
    async (action: (device: Device) => Promise<DeviceOutcome>, label: string, recorded: NotificationPayload | null) => {
      if (targets.length === 0) {
        toast({ title: 'No devices selected', text: 'Pick at least one TV in the Devices tab.', color: 'red' })
        return
      }
      setBusy(true)
      const results = await Promise.all(targets.map(action))
      setOutcomes(results)
      setBusy(false)
      const failures = results.filter((r) => !r.ok)
      toast({
        title:
          failures.length === 0
            ? `${label} to ${results.length} device${results.length === 1 ? '' : 's'}`
            : `${failures.length} of ${results.length} failed`,
        text: failures.length === 0 ? undefined : failures.map((r) => `${r.deviceName}: ${r.detail}`).join('\n'),
        color: failures.length === 0 ? 'green' : 'red',
      })
      if (recorded) onSent?.({ payload: recorded, outcomes: results })
    },
    [targets, toast, onSent],
  )

  const send = useCallback(
    (payload: NotificationPayload) => run((device) => deliver(device, payload), 'Sent', payload),
    [run],
  )

  const clear = useCallback((id: string) => run((device) => dismiss(device, id), 'Cleared', null), [run])

  return { busy, outcomes, send, clear }
}

export type Sender = ReturnType<typeof useSender>
