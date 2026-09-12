import { Button, Chip, Input, Popover, PopoverRoot, PopoverTrigger, useDialog } from '@cladd-ui/react'
import { useState } from 'react'
import type { PresetsStore } from '../hooks/usePresets'
import type { NotificationPayload } from '../lib/payload'

interface PresetsBarProps {
  store: PresetsStore
  current: NotificationPayload
  onApply: (payload: NotificationPayload) => void
}

export function PresetsBar({ store, current, onApply }: PresetsBarProps) {
  const dialog = useDialog()
  const [name, setName] = useState('')
  const [open, setOpen] = useState(false)

  const savePreset = () => {
    store.save(name, current)
    setName('')
    setOpen(false)
  }

  return (
    <div className="flex flex-wrap items-center gap-2">
      <span className="text-cladd-xs font-medium text-cladd-fg-soft">Presets</span>
      {store.presets.map((preset) => (
        <Chip
          key={preset.id}
          as="button"
          size="sm"
          color={preset.builtIn ? 'brand' : 'neutral'}
          onClick={() => onApply({ ...preset.payload })}
          onContextMenu={(event: React.MouseEvent) => {
            if (preset.builtIn) return
            event.preventDefault()
            dialog.confirm({
              title: `Delete preset "${preset.name}"?`,
              confirmButtonText: 'Delete',
              confirmButtonColor: 'red',
              onConfirm: () => store.remove(preset.id),
            })
          }}
        >
          {preset.name}
        </Chip>
      ))}
      <PopoverRoot open={open} onOpenChange={setOpen}>
        <PopoverTrigger>
          <Button size="sm" color="neutral">
            Save as preset
          </Button>
        </PopoverTrigger>
        <Popover position="bottom-start" className="w-72" contentClassName="flex flex-col gap-2 p-3">
          <Input
            value={name}
            onChange={setName}
            placeholder="Preset name"
            autoFocus
            onKeyDown={(event) => {
              if (event.key === 'Enter') savePreset()
            }}
          />
          <div className="flex gap-2">
            <Button size="sm" disabled={!name.trim()} onClick={savePreset}>
              Save
            </Button>
            <Button size="sm" color="neutral" onClick={() => setOpen(false)}>
              Cancel
            </Button>
          </div>
          <span className="text-cladd-2xs text-cladd-fg-softer">Right-click a custom preset to delete it.</span>
        </Popover>
      </PopoverRoot>
    </div>
  )
}
