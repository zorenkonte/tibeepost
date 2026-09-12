import { baseUrl, type Device } from './devices'
import type { WirePayload } from './payload'

export interface HealthInfo {
  version: string
}

export class TvApiError extends Error {
  readonly status: number | null

  constructor(message: string, status: number | null) {
    super(message)
    this.status = status
  }
}

async function readError(response: Response): Promise<string> {
  const text = await response.text().catch(() => '')
  try {
    const parsed = JSON.parse(text) as { error?: string }
    if (parsed.error) return `${response.status}: ${parsed.error}`
  } catch {
    return text ? `${response.status}: ${text}` : `HTTP ${response.status}`
  }
  return `HTTP ${response.status}`
}

function describeNetworkError(error: unknown): string {
  if (error instanceof DOMException && error.name === 'TimeoutError') return 'Timed out waiting for the TV'
  if (error instanceof TypeError) return 'Unreachable (network error or blocked by the browser)'
  return error instanceof Error ? error.message : String(error)
}

export async function checkHealth(device: Device, timeoutMs = 2500): Promise<HealthInfo> {
  try {
    const response = await fetch(`${baseUrl(device)}/health`, { signal: AbortSignal.timeout(timeoutMs) })
    if (!response.ok) throw new TvApiError(await readError(response), response.status)
    const body = (await response.json()) as { version?: string }
    return { version: body.version ?? 'unknown' }
  } catch (error) {
    if (error instanceof TvApiError) throw error
    throw new TvApiError(describeNetworkError(error), null)
  }
}

export interface NotifyResult {
  id: string
  result: string
}

function authHeaders(device: Device): HeadersInit {
  return device.token ? { Authorization: `Bearer ${device.token}` } : {}
}

export async function sendNotification(device: Device, payload: WirePayload, timeoutMs = 6000): Promise<NotifyResult> {
  try {
    const response = await fetch(`${baseUrl(device)}/notify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeaders(device) },
      body: JSON.stringify(payload),
      signal: AbortSignal.timeout(timeoutMs),
    })
    if (!response.ok) throw new TvApiError(await readError(response), response.status)
    return (await response.json()) as NotifyResult
  } catch (error) {
    if (error instanceof TvApiError) throw error
    throw new TvApiError(describeNetworkError(error), null)
  }
}

export async function clearNotification(device: Device, id: string, timeoutMs = 6000): Promise<NotifyResult> {
  try {
    const response = await fetch(`${baseUrl(device)}/notify/${encodeURIComponent(id)}`, {
      method: 'DELETE',
      headers: authHeaders(device),
      signal: AbortSignal.timeout(timeoutMs),
    })
    if (!response.ok) throw new TvApiError(await readError(response), response.status)
    return (await response.json()) as NotifyResult
  } catch (error) {
    if (error instanceof TvApiError) throw error
    throw new TvApiError(describeNetworkError(error), null)
  }
}
