import { Link, Surface } from '@cladd-ui/react'
import { servedOverHttps } from '../lib/pageProtocol'

const README_URL = 'https://github.com/zorenkonte/tibeepost#hosted-client'

export function InsecureContentNotice() {
  if (!servedOverHttps()) return null
  return (
    <Surface color="yellow" variant="solid" outline className="p-3">
      <div className="flex flex-col gap-1 text-cladd-xs">
        <span className="font-semibold">This page is HTTPS, but the TV only speaks plain HTTP.</span>
        <span className="text-cladd-fg-soft">
          Browsers block that mix by default, so sends will fail as Unreachable until you allow insecure content for this
          site once. Chrome and Edge: click the padlock, open Site settings, set Insecure content to Allow, then reload.
          Firefox: this page cannot reach the TV; run the client locally instead.{' '}
          <Link as="a" href={README_URL} target="_blank" rel="noreferrer">
            Details in the README
          </Link>
        </span>
      </div>
    </Surface>
  )
}
