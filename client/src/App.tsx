import { SectionTitle, Surface, Tab, TabPanel, Tabs, TabsList } from '@cladd-ui/react'
import { useCallback, useState } from 'react'
import { CardPreview } from './components/CardPreview'
import { ComposeForm } from './components/ComposeForm'
import { CurlExport } from './components/CurlExport'
import { DevicesPanel } from './components/DevicesPanel'
import { HistoryList } from './components/HistoryList'
import { PresetsBar } from './components/PresetsBar'
import { SendPanel } from './components/SendPanel'
import { useDevices } from './hooks/useDevices'
import { useHistory } from './hooks/useHistory'
import { usePresets } from './hooks/usePresets'
import { useSender, type SendOutcome } from './hooks/useSender'
import { DEFAULT_PAYLOAD, type NotificationPayload } from './lib/payload'

export default function App() {
  const devices = useDevices()
  const presets = usePresets()
  const history = useHistory()
  const { record } = history
  const [payload, setPayload] = useState<NotificationPayload>(DEFAULT_PAYLOAD)
  const [tab, setTab] = useState('compose')
  const recordSend = useCallback((outcome: SendOutcome) => record(outcome.payload, outcome.outcomes), [record])
  const sender = useSender(devices.selectedDevices, recordSend)

  const loadIntoComposer = (next: NotificationPayload) => {
    setPayload(next)
    setTab('compose')
  }

  return (
    <div className="app-container flex min-h-full flex-col gap-4 p-4">
      <header className="flex items-baseline gap-3">
        <h1 className="text-cladd-md font-semibold">TibeePost</h1>
        <span className="text-cladd-xs text-cladd-fg-soft">Compose and send overlay notifications to your TV</span>
      </header>
      <div className="grid flex-1 items-start gap-4 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
        <Surface className="p-4">
          <Tabs value={tab} onValueChange={setTab}>
            <TabsList className="mb-4">
              <Tab value="compose">Compose</Tab>
              <Tab value="devices">Devices</Tab>
              <Tab value="history">History</Tab>
              <Tab value="curl">curl</Tab>
            </TabsList>
            <TabPanel value="compose">
              <div className="flex flex-col gap-4">
                <PresetsBar store={presets} current={payload} onApply={setPayload} />
                <ComposeForm value={payload} onChange={setPayload} />
              </div>
            </TabPanel>
            <TabPanel value="devices">
              <DevicesPanel store={devices} />
            </TabPanel>
            <TabPanel value="history">
              <HistoryList
                store={history}
                sending={sender.busy}
                onLoad={loadIntoComposer}
                onResend={(entry) => void sender.send(entry)}
              />
            </TabPanel>
            <TabPanel value="curl">
              <CurlExport payload={payload} devices={devices} />
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
            <SendPanel payload={payload} targetCount={devices.selectedDevices.length} sender={sender} />
          </Surface>
        </div>
      </div>
    </div>
  )
}
