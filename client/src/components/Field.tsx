import type { ReactNode } from 'react'

interface FieldProps {
  label: string
  hint?: string
  children: ReactNode
  className?: string
}

export function Field({ label, hint, children, className }: FieldProps) {
  return (
    <div className={`flex flex-col gap-1 ${className ?? ''}`}>
      <span className="text-cladd-xs font-medium text-cladd-fg-soft">{label}</span>
      {children}
      {hint && <span className="text-cladd-2xs text-cladd-fg-softer">{hint}</span>}
    </div>
  )
}
