import { useCallback, useState } from 'react'
import type { DeviceOutcome } from './useSender'
import { newId } from '../lib/ids'
import type { NotificationPayload } from '../lib/payload'

export interface HistoryEntry {
  id: string
  sentAt: number
  payload: NotificationPayload
  outcomes: DeviceOutcome[]
}

const MAX_ENTRIES = 50

export function useHistory() {
  const [entries, setEntries] = useState<HistoryEntry[]>([])

  const record = useCallback((payload: NotificationPayload, outcomes: DeviceOutcome[]) => {
    setEntries((current) =>
      [{ id: newId(), sentAt: Date.now(), payload, outcomes }, ...current].slice(0, MAX_ENTRIES),
    )
  }, [])

  const clear = useCallback(() => setEntries([]), [])

  return { entries, record, clear }
}

export type HistoryStore = ReturnType<typeof useHistory>
