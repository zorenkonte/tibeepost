import { Button, Checkbox, Chip, Input, SectionTitle, Surface, SurfaceCut, useDialog } from '@cladd-ui/react'
import { useState } from 'react'
import type { DevicesStore } from '../hooks/useDevices'
import { newDeviceId, normalizeHost, type Device, type Reachability } from '../lib/devices'

const REACHABILITY_LABEL: Record<Reachability, { text: string; color: 'green' | 'red' | 'neutral' }> = {
  online: { text: 'Online', color: 'green' },
  offline: { text: 'Offline', color: 'red' },
  unknown: { text: 'Checking', color: 'neutral' },
}

interface DeviceFormProps {
  initial: Device
  onSave: (device: Device) => void
  onCancel: () => void
}

function DeviceForm({ initial, onSave, onCancel }: DeviceFormProps) {
  const [name, setName] = useState(initial.name)
  const [host, setHost] = useState(initial.host)
  const [token, setToken] = useState(initial.token)
  const hostValid = normalizeHost(host).length > 0

  return (
    <SurfaceCut className="p-3">
      <div className="flex flex-col gap-3">
        <Input value={name} onChange={setName} placeholder="Nickname, e.g. Living room" clearButton />
        <Input
          value={host}
          onChange={setHost}
          placeholder="IP or host, e.g. 192.168.1.50"
          infoMessage="Port 8090 is added when omitted"
          valid={host.length === 0 || hostValid}
          clearButton
        />
        <Input
          value={token}
          onChange={setToken}
          placeholder="Bearer token (optional)"
          infoMessage="Only needed when the TV has a token set"
          clearButton
        />
        <div className="flex gap-2">
          <Button
            disabled={!hostValid}
            onClick={() => onSave({ id: initial.id, name: name.trim() || normalizeHost(host), host, token })}
          >
            Save device
          </Button>
          <Button color="neutral" onClick={onCancel}>
            Cancel
          </Button>
        </div>
      </div>
    </SurfaceCut>
  )
}

export function DevicesPanel({ store }: { store: DevicesStore }) {
  const dialog = useDialog()
  const [editing, setEditing] = useState<Device | null>(null)

  const startAdd = () => setEditing({ id: newDeviceId(), name: '', host: '', token: '' })

  const confirmRemove = (device: Device) =>
    dialog.confirm({
      title: `Remove ${device.name}?`,
      text: 'The TV keeps running; only this browser forgets it.',
      confirmButtonText: 'Remove',
      confirmButtonColor: 'red',
      onConfirm: () => store.remove(device.id),
    })

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <SectionTitle>Devices</SectionTitle>
        <Button onClick={startAdd} disabled={editing !== null}>
          Add TV
        </Button>
      </div>

      {editing && (
        <DeviceForm
          initial={editing}
          onSave={(device) => {
            store.upsert(device)
            setEditing(null)
          }}
          onCancel={() => setEditing(null)}
        />
      )}

      {store.devices.length === 0 && !editing && (
        <p className="text-cladd-fg-soft">
          No TVs yet. Add one by IP address. The TibeePost settings screen on the TV shows its address in large type.
        </p>
      )}

      <div className="flex flex-col gap-2">
        {store.devices.map((device) => {
          const status = store.statuses[device.id]
          const reachability = status?.reachability ?? 'unknown'
          const label = REACHABILITY_LABEL[reachability]
          return (
            <Surface key={device.id} className="p-3">
              <div className="flex items-center gap-3">
                <Checkbox
                  checked={store.selectedIds.includes(device.id)}
                  onChange={(checked) => store.toggleSelected(device.id, checked)}
                />
                <div className="flex min-w-0 flex-1 flex-col">
                  <span className="truncate font-medium">{device.name}</span>
                  <span className="truncate text-cladd-xs text-cladd-fg-soft">
                    {device.host}
                    {device.token ? ' · token set' : ''}
                    {status?.version ? ` · v${status.version}` : ''}
                  </span>
                </div>
                <Chip color={label.color} size="sm">
                  {label.text}
                </Chip>
                <Button size="sm" color="neutral" onClick={() => setEditing(device)}>
                  Edit
                </Button>
                <Button size="sm" color="red" variant="transparent" outline={false} onClick={() => confirmRemove(device)}>
                  Remove
                </Button>
              </div>
            </Surface>
          )
        })}
      </div>
    </div>
  )
}
