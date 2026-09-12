export function servedOverHttps(): boolean {
  return typeof window !== 'undefined' && window.location.protocol === 'https:'
}
