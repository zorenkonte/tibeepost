import { DEFAULT_PAYLOAD, type NotificationPayload } from './payload'

export interface Preset {
  id: string
  name: string
  builtIn: boolean
  payload: NotificationPayload
}

export const BUILT_IN_PRESETS: Preset[] = [
  {
    id: 'builtin-alert',
    name: 'Loud red alert',
    builtIn: true,
    payload: {
      ...DEFAULT_PAYLOAD,
      title: 'Attention',
      message: 'Something needs you right now.',
      duration: 20,
      position: 'center',
      widthPercent: 70,
      background: '#B91C1C',
      textColor: '#FFFFFF',
      accent: '#FDE047',
      dim: 0.9,
      sound: 'default',
      speak: true,
    },
  },
  {
    id: 'builtin-info',
    name: 'Quiet info card',
    builtIn: true,
    payload: {
      ...DEFAULT_PAYLOAD,
      title: 'FYI',
      message: 'A small update, nothing urgent.',
      duration: 8,
      position: 'top-right',
      widthPercent: 35,
      background: '#FFFFFF',
      textColor: '#111111',
      accent: '',
      dim: 0,
      sound: 'none',
      speak: false,
    },
  },
]
