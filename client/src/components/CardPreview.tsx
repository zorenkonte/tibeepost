import { useState, type CSSProperties } from 'react'
import type { NotificationPayload, Position } from '../lib/payload'

const TV_WIDTH_PX = 1920
const DENSITY = 2

function tv(dp: number): string {
  return `${((dp * DENSITY) / TV_WIDTH_PX) * 100}cqw`
}

const MARGIN = tv(48)

const PLACEMENT: Record<Position, CSSProperties> = {
  center: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' },
  'top-left': { top: MARGIN, left: MARGIN },
  'top-right': { top: MARGIN, right: MARGIN },
  'bottom-left': { bottom: MARGIN, left: MARGIN },
  'bottom-right': { bottom: MARGIN, right: MARGIN },
}

function RemoteImage({ src, style, className }: { src: string; style?: CSSProperties; className?: string }) {
  const [failed, setFailed] = useState(false)
  if (!src.trim() || failed) return null
  return <img src={src} alt="" style={style} className={className} onError={() => setFailed(true)} />
}

export function CardPreview({ payload }: { payload: NotificationPayload }) {
  const title = payload.title.trim()
  const message = payload.message.trim() || 'Your message appears here'

  return (
    <div
      className="relative aspect-video w-full overflow-hidden rounded-lg"
      style={{
        containerType: 'inline-size',
        background:
          'radial-gradient(ellipse at 30% 20%, #3b4f7a 0%, #1c2540 45%, #0a0d1a 100%)',
      }}
    >
      <div className="absolute inset-0 bg-black transition-opacity" style={{ opacity: payload.dim }} />
      <div
        className="absolute flex overflow-hidden"
        style={{
          ...PLACEMENT[payload.position],
          width: `${payload.widthPercent}%`,
          background: payload.background,
          color: payload.textColor,
          borderRadius: tv(16),
          boxShadow: `0 ${tv(6)} ${tv(24)} rgba(0,0,0,0.45)`,
        }}
      >
        {payload.accent && <div style={{ width: tv(10), flexShrink: 0, background: payload.accent }} />}
        <div className="flex min-w-0 flex-1 flex-col" style={{ padding: tv(28) }}>
          {(title || payload.icon.trim()) && (
            <div className="flex items-center" style={{ gap: tv(16) }}>
              <RemoteImage
                key={payload.icon}
                src={payload.icon}
                style={{ width: tv(48), height: tv(48), objectFit: 'contain', flexShrink: 0 }}
              />
              {title && (
                <div
                  className="line-clamp-2 font-bold"
                  style={{ fontSize: tv(30), lineHeight: 1.2 }}
                >
                  {title}
                </div>
              )}
            </div>
          )}
          <div
            className="line-clamp-[8] whitespace-pre-wrap"
            style={{ fontSize: tv(24), lineHeight: 1.15, marginTop: title || payload.icon.trim() ? tv(12) : 0 }}
          >
            {message}
          </div>
          <RemoteImage
            key={payload.image}
            src={payload.image}
            style={{ marginTop: tv(20), maxHeight: `${(0.4 * 1080 / TV_WIDTH_PX) * 100}cqw`, width: '100%', objectFit: 'contain' }}
          />
        </div>
      </div>
    </div>
  )
}
