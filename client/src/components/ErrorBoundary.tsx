import { Button, Surface } from '@cladd-ui/react'
import { Component, type ErrorInfo, type ReactNode } from 'react'

interface ErrorBoundaryProps {
  children: ReactNode
}

interface ErrorBoundaryState {
  error: Error | null
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  state: ErrorBoundaryState = { error: null }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error(error, info.componentStack)
  }

  render() {
    const { error } = this.state
    if (!error) return this.props.children
    return (
      <div className="app-container flex min-h-full items-center justify-center p-4">
        <Surface className="max-w-lg p-4">
          <div className="flex flex-col gap-3">
            <h1 className="text-cladd-md font-semibold">Something broke in the client</h1>
            <p className="text-cladd-fg-soft">
              The page hit an error and stopped rendering. Reload to continue; if it keeps happening, copy the message below into an
              issue.
            </p>
            <pre className="overflow-x-auto whitespace-pre-wrap font-mono text-cladd-2xs text-cladd-red">
              {error.name}: {error.message}
            </pre>
            <div>
              <Button onClick={() => window.location.reload()}>Reload</Button>
            </div>
          </div>
        </Surface>
      </div>
    )
  }
}
