import { Button, Select, SurfaceCut, useToast } from '@cladd-ui/react'
import { useState } from 'react'
import type { DevicesStore } from '../hooks/useDevices'
import { copyText } from '../lib/clipboard'
import { curlForClear, curlForNotify } from '../lib/curl'
import { toWire, type NotificationPayload } from '../lib/payload'

interface CurlExportProps {
  payload: NotificationPayload
  devices: DevicesStore
}

const PLACEHOLDER_DEVICE = '__placeholder__'

export function CurlExport({ payload, devices }: CurlExportProps) {
  const toast = useToast()
  const [deviceId, setDeviceId] = useState<string>(devices.selectedDevices[0]?.id ?? PLACEHOLDER_DEVICE)
  const device = devices.devices.find((d) => d.id === deviceId)
  const options = [PLACEHOLDER_DEVICE, ...devices.devices.map((d) => d.id)]
  const label = (id: string) => (id === PLACEHOLDER_DEVICE ? 'TV_IP placeholder' : devices.devices.find((d) => d.id === id)?.name ?? id)
  const notifyCommand = curlForNotify(toWire(payload), device)
  const clearCommand = payload.id.trim() ? curlForClear(payload.id.trim(), device) : null

  const copy = async (text: string, what: string) => {
    if (await copyText(text)) {
      toast({ title: `${what} copied`, color: 'green', timeout: 2500 })
    } else {
      toast({ title: 'Clipboard blocked', text: 'Select the text and copy it manually.', color: 'red' })
    }
  }

  return (
    <div className="flex flex-col gap-3">
      <div className="flex flex-wrap items-center gap-2">
        <span className="text-cladd-xs font-medium text-cladd-fg-soft">Target for the command</span>
        <Select
          size="sm"
          options={options}
          value={deviceId}
          onChange={setDeviceId}
          renderOption={({ value }) => label(value)}
          popoverPosition="bottom-start"
        >
          {label(deviceId)}
        </Select>
        <span className="flex-1" />
        <Button size="sm" onClick={() => void copy(notifyCommand, 'curl command')}>
          Copy as curl
        </Button>
        <Button size="sm" color="neutral" onClick={() => void copy(JSON.stringify(toWire(payload), null, 2), 'JSON')}>
          Copy JSON
        </Button>
      </div>
      <SurfaceCut className="p-3">
        <pre className="overflow-x-auto whitespace-pre font-mono text-cladd-2xs leading-relaxed text-cladd-fg-soft">
          {notifyCommand}
        </pre>
      </SurfaceCut>
      {clearCommand && (
        <SurfaceCut className="p-3">
          <pre className="overflow-x-auto whitespace-pre font-mono text-cladd-2xs leading-relaxed text-cladd-fg-soft">{clearCommand}</pre>
        </SurfaceCut>
      )}
    </div>
  )
}
