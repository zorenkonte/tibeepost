import { ColorPicker, Input, NumberField, Select, Slider, Switch, Textarea } from '@cladd-ui/react'
import { useState } from 'react'
import {
  POSITIONS,
  SOUND_MODES,
  soundMode,
  type NotificationPayload,
  type Position,
  type SoundMode,
} from '../lib/payload'
import { Field } from './Field'

const POSITION_LABEL: Record<Position, string> = {
  center: 'Center',
  'top-left': 'Top left',
  'top-right': 'Top right',
  'bottom-left': 'Bottom left',
  'bottom-right': 'Bottom right',
}

const SOUND_LABEL: Record<SoundMode, string> = {
  default: 'Default chime',
  none: 'Silent',
  url: 'Custom URL',
}

interface ComposeFormProps {
  value: NotificationPayload
  onChange: (next: NotificationPayload) => void
}

export function ComposeForm({ value, onChange }: ComposeFormProps) {
  const [lastAccent, setLastAccent] = useState(value.accent || '#FF1744')
  const [lastSoundUrl, setLastSoundUrl] = useState(soundMode(value.sound) === 'url' ? value.sound : '')
  const set = <K extends keyof NotificationPayload>(key: K, next: NotificationPayload[K]) =>
    onChange({ ...value, [key]: next })
  const mode = soundMode(value.sound)

  const changeSoundMode = (next: SoundMode) => {
    if (next === 'url') set('sound', lastSoundUrl)
    else set('sound', next)
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="grid gap-3 sm:grid-cols-2">
        <Field label="Title">
          <Input value={value.title} onChange={(v) => set('title', v)} placeholder="Order #1042" clearButton />
        </Field>
        <Field label="ID" hint="Reusing an ID replaces the card in place">
          <Input value={value.id} onChange={(v) => set('id', v)} placeholder="auto-generated when empty" clearButton />
        </Field>
      </div>

      <Field label="Message" hint="The only required field">
        <Textarea value={value.message} onChange={(v) => set('message', v)} placeholder="New order received" />
      </Field>

      <div className="grid gap-3 sm:grid-cols-2">
        <Field label="Image URL">
          <Input value={value.image} onChange={(v) => set('image', v)} placeholder="https://…/banner.png" clearButton />
        </Field>
        <Field label="Icon URL">
          <Input value={value.icon} onChange={(v) => set('icon', v)} placeholder="https://…/icon.png" clearButton />
        </Field>
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <Field label="Position">
          <Select
            options={[...POSITIONS]}
            value={value.position}
            onChange={(v) => set('position', v)}
            renderOption={({ value: option }) => POSITION_LABEL[option]}
            popoverPosition="bottom-start"
          >
            {POSITION_LABEL[value.position]}
          </Select>
        </Field>
        <Field label="Duration (seconds)">
          <NumberField value={value.duration} min={1} max={3600} onChange={(v) => set('duration', v)} />
        </Field>
        <Field label="Persistent" hint="Stays until cleared by ID">
          <div className="flex h-cladd-md items-center">
            <Switch checked={value.persistent} onChange={(checked) => set('persistent', checked)} />
          </div>
        </Field>
      </div>

      <div className="grid gap-3 sm:grid-cols-2">
        <Field label={`Card width · ${value.widthPercent}% of screen`}>
          <Slider min={10} max={100} step={5} value={value.widthPercent} onChange={(v) => set('widthPercent', v)} />
        </Field>
        <Field label={`Dim behind card · ${Math.round(value.dim * 100)}%`}>
          <Slider min={0} max={1} step={0.05} value={value.dim} onChange={(v) => set('dim', v)} />
        </Field>
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <Field label="Background">
          <ColorPicker alpha={false} value={value.background} onChange={(c) => set('background', c.hex.toUpperCase())} />
        </Field>
        <Field label="Text color">
          <ColorPicker alpha={false} value={value.textColor} onChange={(c) => set('textColor', c.hex.toUpperCase())} />
        </Field>
        <Field label="Accent stripe">
          <div className="flex items-center gap-2">
            <Switch
              checked={value.accent !== ''}
              onChange={(checked) => set('accent', checked ? lastAccent : '')}
            />
            <ColorPicker
              alpha={false}
              disabled={value.accent === ''}
              value={value.accent || lastAccent}
              onChange={(c) => {
                setLastAccent(c.hex.toUpperCase())
                set('accent', c.hex.toUpperCase())
              }}
              className="flex-1"
            />
          </div>
        </Field>
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <Field label="Sound">
          <Select
            options={[...SOUND_MODES]}
            value={mode}
            onChange={changeSoundMode}
            renderOption={({ value: option }) => SOUND_LABEL[option]}
            popoverPosition="bottom-start"
          >
            {SOUND_LABEL[mode]}
          </Select>
        </Field>
        <Field label="Sound URL" className={mode === 'url' ? '' : 'opacity-40'}>
          <Input
            value={mode === 'url' ? value.sound : ''}
            disabled={mode !== 'url'}
            onChange={(v) => {
              setLastSoundUrl(v)
              set('sound', v)
            }}
            placeholder="https://…/ding.mp3"
            clearButton
          />
        </Field>
        <Field label="Read aloud" hint="Speaks title and message after the sound">
          <div className="flex h-cladd-md items-center">
            <Switch checked={value.speak} onChange={(checked) => set('speak', checked)} />
          </div>
        </Field>
      </div>
    </div>
  )
}
