import { Surface, Tab, TabPanel, Tabs, TabsList } from '@cladd-ui/react'
import { DevicesPanel } from './components/DevicesPanel'
import { useDevices } from './hooks/useDevices'

export default function App() {
  const devices = useDevices()

  return (
    <div className="app-container flex min-h-full flex-col gap-4 p-4">
      <header className="flex items-baseline gap-3">
        <h1 className="text-cladd-md font-semibold">TibeePost</h1>
        <span className="text-cladd-xs text-cladd-fg-soft">Compose and send overlay notifications to your TV</span>
      </header>
      <div className="grid flex-1 gap-4 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
        <Surface className="p-4">
          <Tabs defaultValue="compose">
            <TabsList className="mb-4">
              <Tab value="compose">Compose</Tab>
              <Tab value="devices">Devices</Tab>
              <Tab value="history">History</Tab>
            </TabsList>
            <TabPanel value="compose">
              <p className="text-cladd-fg-soft">Compose form arrives next.</p>
            </TabPanel>
            <TabPanel value="devices">
              <DevicesPanel store={devices} />
            </TabPanel>
            <TabPanel value="history">
              <p className="text-cladd-fg-soft">Nothing sent yet.</p>
            </TabPanel>
          </Tabs>
        </Surface>
        <Surface className="p-4">
          <p className="text-cladd-fg-soft">
            {devices.selectedDevices.length} of {devices.devices.length} devices selected for sending.
          </p>
        </Surface>
      </div>
    </div>
  )
}
