import { baseUrl, type Device } from './devices'
import type { WirePayload } from './payload'

function shellQuote(text: string): string {
  return `'${text.replace(/'/g, `'\\''`)}'`
}

export function curlForNotify(payload: WirePayload, device?: Pick<Device, 'host' | 'token'>): string {
  const target = device ? baseUrl(device) : 'http://TV_IP:8090'
  const lines = [`curl -X POST ${target}/notify`, `  -H 'Content-Type: application/json'`]
  if (device?.token) lines.push(`  -H ${shellQuote(`Authorization: Bearer ${device.token}`)}`)
  lines.push(`  -d ${shellQuote(JSON.stringify(payload, null, 2))}`)
  return lines.join(' \\\n')
}

export function curlForClear(id: string, device?: Pick<Device, 'host' | 'token'>): string {
  const target = device ? baseUrl(device) : 'http://TV_IP:8090'
  const lines = [`curl -X DELETE ${target}/notify/${encodeURIComponent(id)}`]
  if (device?.token) lines.push(`  -H ${shellQuote(`Authorization: Bearer ${device.token}`)}`)
  return lines.join(' \\\n')
}
