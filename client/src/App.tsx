import { SectionTitle, Surface, Tab, TabPanel, Tabs, TabsList } from '@cladd-ui/react'
import { useState } from 'react'
import { CardPreview } from './components/CardPreview'
import { ComposeForm } from './components/ComposeForm'
import { DevicesPanel } from './components/DevicesPanel'
import { SendPanel } from './components/SendPanel'
import { useDevices } from './hooks/useDevices'
import { DEFAULT_PAYLOAD, type NotificationPayload } from './lib/payload'

export default function App() {
  const devices = useDevices()
  const [payload, setPayload] = useState<NotificationPayload>(DEFAULT_PAYLOAD)

  return (
    <div className="app-container flex min-h-full flex-col gap-4 p-4">
      <header className="flex items-baseline gap-3">
        <h1 className="text-cladd-md font-semibold">TibeePost</h1>
        <span className="text-cladd-xs text-cladd-fg-soft">Compose and send overlay notifications to your TV</span>
      </header>
      <div className="grid flex-1 items-start gap-4 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
        <Surface className="p-4">
          <Tabs defaultValue="compose">
            <TabsList className="mb-4">
              <Tab value="compose">Compose</Tab>
              <Tab value="devices">Devices</Tab>
              <Tab value="history">History</Tab>
            </TabsList>
            <TabPanel value="compose">
              <ComposeForm value={payload} onChange={setPayload} />
            </TabPanel>
            <TabPanel value="devices">
              <DevicesPanel store={devices} />
            </TabPanel>
            <TabPanel value="history">
              <p className="text-cladd-fg-soft">Nothing sent yet.</p>
            </TabPanel>
          </Tabs>
        </Surface>
        <div className="flex flex-col gap-4 lg:sticky lg:top-4">
          <Surface className="p-4">
            <div className="flex flex-col gap-3">
              <div className="flex items-baseline justify-between">
                <SectionTitle>Preview</SectionTitle>
                <span className="text-cladd-xs text-cladd-fg-soft">1080p TV, proportions match the overlay</span>
              </div>
              <CardPreview payload={payload} />
            </div>
          </Surface>
          <Surface className="p-4">
            <SendPanel payload={payload} devices={devices} />
          </Surface>
        </div>
      </div>
    </div>
  )
}
