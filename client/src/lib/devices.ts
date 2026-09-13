export interface Device {
  id: string
  name: string
  host: string
  token: string
}

export type Reachability = 'unknown' | 'online' | 'offline'

export interface DeviceStatus {
  reachability: Reachability
  version?: string
  checkedAt?: number
}

export const DEFAULT_PORT = 8090

export function normalizeHost(input: string): string {
  let host = input.trim().replace(/^https?:\/\//i, '').replace(/\/+$/, '')
  if (host && !/:\d+$/.test(host)) host = `${host}:${DEFAULT_PORT}`
  return host
}

export function baseUrl(device: Pick<Device, 'host'>): string {
  return `http://${normalizeHost(device.host)}`
}

export function newDeviceId(): string {
  return crypto.randomUUID()
}

export function servingTvHost(): string | null {
  if (typeof window === 'undefined') return null
  const { hostname, port, protocol } = window.location
  if (protocol !== 'http:' || port !== String(DEFAULT_PORT)) return null
  if (hostname === 'localhost' || hostname === '127.0.0.1') return null
  return `${hostname}:${port}`
}
