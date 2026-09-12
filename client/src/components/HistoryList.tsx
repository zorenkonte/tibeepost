import { Button, Chip, SectionTitle, Surface } from '@cladd-ui/react'
import type { HistoryStore } from '../hooks/useHistory'
import type { NotificationPayload } from '../lib/payload'

interface HistoryListProps {
  store: HistoryStore
  onResend: (payload: NotificationPayload) => void
  onLoad: (payload: NotificationPayload) => void
  sending: boolean
}

function formatTime(timestamp: number): string {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

export function HistoryList({ store, onResend, onLoad, sending }: HistoryListProps) {
  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <SectionTitle>History</SectionTitle>
        {store.entries.length > 0 && (
          <Button size="sm" color="neutral" onClick={store.clear}>
            Clear history
          </Button>
        )}
      </div>
      {store.entries.length === 0 && (
        <p className="text-cladd-fg-soft">Nothing sent in this session yet. Sends are kept until you reload the page.</p>
      )}
      <div className="flex flex-col gap-2">
        {store.entries.map((entry) => {
          const failed = entry.outcomes.filter((o) => !o.ok).length
          return (
            <Surface key={entry.id} className="p-3">
              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-2">
                  <span className="text-cladd-xs text-cladd-fg-soft">{formatTime(entry.sentAt)}</span>
                  <span className="min-w-0 flex-1 truncate font-medium">
                    {entry.payload.title.trim() ? `${entry.payload.title.trim()} · ` : ''}
                    {entry.payload.message.trim()}
                  </span>
                  <Chip size="xs" color={failed === 0 ? 'green' : 'red'}>
                    {failed === 0 ? `${entry.outcomes.length} ok` : `${failed}/${entry.outcomes.length} failed`}
                  </Chip>
                </div>
                <div className="flex flex-wrap gap-x-3 gap-y-1 text-cladd-2xs text-cladd-fg-softer">
                  {entry.outcomes.map((o) => (
                    <span key={o.deviceId}>
                      {o.deviceName}: {o.detail}
                    </span>
                  ))}
                </div>
                <div className="flex gap-2">
                  <Button size="sm" disabled={sending} onClick={() => onResend(entry.payload)}>
                    Re-send
                  </Button>
                  <Button size="sm" color="neutral" onClick={() => onLoad(entry.payload)}>
                    Load into composer
                  </Button>
                </div>
              </div>
            </Surface>
          )
        })}
      </div>
    </div>
  )
}
