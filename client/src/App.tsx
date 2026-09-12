import { Surface } from '@cladd-ui/react'

export default function App() {
  return (
    <div className="app-container flex min-h-full flex-col gap-4 p-4">
      <header className="flex items-baseline gap-3">
        <h1 className="text-cladd-md font-semibold">TibeePost</h1>
        <span className="text-cladd-xs text-cladd-fg-soft">Compose and send overlay notifications to your TV</span>
      </header>
      <Surface className="flex-1 p-4">
        <p className="text-cladd-fg-soft">Nothing here yet.</p>
      </Surface>
    </div>
  )
}
