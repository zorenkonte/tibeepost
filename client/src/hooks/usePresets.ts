import { useCallback, useEffect, useState } from 'react'
import { newId } from '../lib/ids'
import type { NotificationPayload } from '../lib/payload'
import { BUILT_IN_PRESETS, type Preset } from '../lib/presets'
import { readStored, writeStored } from '../lib/storage'

const PRESETS_KEY = 'tibeepost.presets'

export function usePresets() {
  const [custom, setCustom] = useState<Preset[]>(() => readStored<Preset[]>(PRESETS_KEY, []))

  useEffect(() => writeStored(PRESETS_KEY, custom), [custom])

  const save = useCallback((name: string, payload: NotificationPayload) => {
    const trimmed = name.trim()
    if (!trimmed) return
    setCustom((current) => {
      const existing = current.find((p) => p.name.toLowerCase() === trimmed.toLowerCase())
      const preset: Preset = { id: existing?.id ?? newId(), name: trimmed, builtIn: false, payload }
      return existing ? current.map((p) => (p.id === preset.id ? preset : p)) : [...current, preset]
    })
  }, [])

  const remove = useCallback((id: string) => setCustom((current) => current.filter((p) => p.id !== id)), [])

  return { presets: [...BUILT_IN_PRESETS, ...custom], save, remove }
}

export type PresetsStore = ReturnType<typeof usePresets>
