import { Button, Chip, SectionTitle, SurfaceCut } from '@cladd-ui/react'
import type { Sender } from '../hooks/useSender'
import { validatePayload, type NotificationPayload } from '../lib/payload'

interface SendPanelProps {
  payload: NotificationPayload
  targetCount: number
  sender: Sender
}

export function SendPanel({ payload, targetCount, sender }: SendPanelProps) {
  const validation = validatePayload(payload)
  const cardId = payload.id.trim()

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center justify-between gap-3">
        <SectionTitle>Send</SectionTitle>
        <span className="text-cladd-xs text-cladd-fg-soft">
          {targetCount === 0
            ? 'Select at least one device in the Devices tab'
            : `${targetCount} device${targetCount === 1 ? '' : 's'} selected`}
        </span>
      </div>
      <div className="flex flex-wrap gap-2">
        <Button
          color="green"
          loading={sender.busy}
          disabled={sender.busy || targetCount === 0 || validation !== null}
          onClick={() => void sender.send(payload)}
        >
          Send notification
        </Button>
        <Button
          color="neutral"
          disabled={sender.busy || targetCount === 0 || cardId === ''}
          onClick={() => void sender.clear(cardId)}
        >
          Clear card by ID
        </Button>
      </div>
      {validation && <span className="text-cladd-xs text-cladd-red">{validation}</span>}
      {sender.outcomes.length > 0 && (
        <SurfaceCut className="p-2">
          <div className="flex flex-col gap-1">
            {sender.outcomes.map((outcome) => (
              <div key={outcome.deviceId} className="flex items-center gap-2 text-cladd-xs">
                <Chip size="xs" color={outcome.ok ? 'green' : 'red'}>
                  {outcome.ok ? 'OK' : 'Failed'}
                </Chip>
                <span className="font-medium">{outcome.deviceName}</span>
                <span className="truncate text-cladd-fg-soft">{outcome.detail}</span>
              </div>
            ))}
          </div>
        </SurfaceCut>
      )}
    </div>
  )
}
