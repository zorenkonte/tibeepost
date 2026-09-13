export const POSITIONS = ['center', 'top-left', 'top-right', 'bottom-left', 'bottom-right'] as const
export type Position = (typeof POSITIONS)[number]

export const SOUND_MODES = ['default', 'none', 'url'] as const
export type SoundMode = (typeof SOUND_MODES)[number]

export interface NotificationPayload {
  id: string
  title: string
  message: string
  image: string
  icon: string
  duration: number
  persistent: boolean
  position: Position
  widthPercent: number
  background: string
  textColor: string
  accent: string
  dim: number
  sound: string
  speak: boolean
}

export const DEFAULT_PAYLOAD: NotificationPayload = {
  id: '',
  title: '',
  message: '',
  image: '',
  icon: '',
  duration: 15,
  persistent: false,
  position: 'center',
  widthPercent: 60,
  background: '#FFFFFF',
  textColor: '#111111',
  accent: '',
  dim: 0,
  sound: 'default',
  speak: false,
}

export type WirePayload = Record<string, string | number | boolean>

export function soundMode(sound: string): SoundMode {
  if (sound === 'none' || sound === 'default') return sound
  return 'url'
}

export function toWire(payload: NotificationPayload): WirePayload {
  const wire: WirePayload = {}
  const text = (key: keyof NotificationPayload) => {
    const value = String(payload[key]).trim()
    if (value) wire[key] = value
  }
  text('id')
  text('title')
  wire.message = payload.message.trim()
  text('image')
  text('icon')
  wire.duration = payload.duration
  wire.persistent = payload.persistent
  wire.position = payload.position
  wire.widthPercent = payload.widthPercent
  wire.background = payload.background
  wire.textColor = payload.textColor
  wire.accent = payload.accent.trim() || 'none'
  wire.dim = Number(payload.dim.toFixed(2))
  wire.sound = payload.sound.trim() || 'none'
  wire.speak = payload.speak
  return wire
}

export function validatePayload(payload: NotificationPayload): string | null {
  if (!payload.message.trim()) return 'Message is required'
  if (soundMode(payload.sound) === 'url' && !/^https?:\/\//i.test(payload.sound.trim())) {
    return 'Sound URL must start with http:// or https://'
  }
  for (const key of ['image', 'icon'] as const) {
    const value = payload[key].trim()
    if (value && !/^https?:\/\//i.test(value)) return `${key === 'image' ? 'Image' : 'Icon'} URL must start with http:// or https://`
  }
  return null
}
