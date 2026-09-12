import { CladdProvider } from '@cladd-ui/react'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <CladdProvider
      theme="dark"
      accentColor="brand"
      defaults={{ Input: { size: 'md' }, Textarea: { size: 'md' }, Button: { size: 'md' } }}
    >
      <App />
    </CladdProvider>
  </StrictMode>,
)
