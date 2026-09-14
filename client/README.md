# TibeePost web client

A React 19 app for composing and sending TibeePost notifications. It calls the TV's HTTP API
straight from the browser; there is no backend.

## Served by the TV

The TibeePost APK bundles this client and serves it at `http://TV_IP:8090/`. Open that address, or
scan the QR code on the TV's home screen, and the client comes up with "This TV" already added as a
device. Nothing below is needed for that; it is for developing the client or hosting it elsewhere.

That page runs on a plain-HTTP origin, which browsers treat as an insecure context: `crypto.randomUUID`
and `navigator.clipboard` are unavailable there. The client generates ids from `crypto.getRandomValues`
and copies through a hidden textarea when needed, so every feature works on the TV-served copy.

## Run it

```sh
npm install
npm run dev
```

Open the printed URL, usually `http://localhost:5173`. Serve it over plain `http://`: the TV
speaks plain HTTP and browsers block mixed content from an `https://` page.

Production build:

```sh
npm run build
npm run preview
```

`dist/` is static and can be hosted from anything on the LAN, including the TV owner's NAS
or a Raspberry Pi. Serve it over `http://` to reach the TV without extra browser settings.

Set `VITE_BASE` when the app lives under a sub-path, for example `VITE_BASE=/tibeepost/app/ npm run build`
for GitHub Pages. It defaults to `/`, which is also what the TV build uses: `tv/app/build.gradle.kts`
runs `npm run build` and copies `dist/` into the APK assets.

## Hosted on GitHub Pages

Every push to `main` that touches `client/` or `docs/` deploys the app to
`https://zorenkonte.github.io/tibeepost/app/` through `.github/workflows/pages.yml`, next to the
documentation site at `https://zorenkonte.github.io/tibeepost/`. Pages is HTTPS-only,
and browsers block an HTTPS page from calling the TV's plain-HTTP server, so the hosted app shows a
banner when it detects HTTPS. In Chrome and Edge, click the padlock, open Site settings, set Insecure
content to Allow, and reload; Firefox has no per-site override, so use `npm run dev` there. Details
are on the [web client page](https://zorenkonte.github.io/tibeepost/guide/web-client) of the docs.

## Using it

1. **Devices tab.** Add a TV by IP address. Port `8090` is appended when omitted. Give it a
   nickname and paste the bearer token if the TV has one set. The client polls `/health`
   every five seconds and shows Online, Offline or Checking per device. Tick the checkbox on
   each TV you want to send to.
2. **Compose tab.** Every payload field is here. The preview on the right is a 16:9 stand-in
   for a 1080p TV; card width, position, colors, dim, text sizes and the accent stripe use the
   same proportions as the overlay. Two presets ship: **Loud red alert** and **Quiet info
   card**. Save your own with **Save as preset**; right-click a custom preset to delete it.
3. **Send.** Posts to every selected device in parallel and lists success or the actual error
   per device. **Clear card by ID** sends `DELETE /notify/{id}` for the ID in the composer,
   which is how you remove a persistent card.
   Turning the accent switch off sends `"accent": "none"`, which overrides any default accent set on the TV.
4. **History tab.** Every send in this browser session, with per-device results. Re-send it
   as-is, or load it back into the composer.
5. **curl tab.** The composed notification as a working `curl` command against the chosen
   device, token included, plus the matching `DELETE` command when an ID is set. Copy it into
   your own scripts to integrate without this UI.

Devices, selection and custom presets persist in `localStorage`. History lives in memory
until the page reloads.

## Stack

- React 19, Vite 8, TypeScript
- Tailwind CSS v4 through `@tailwindcss/vite`
- [cladd](https://cladd.io/react) for every control, dialog and toast

Two cladd rules the project follows, because each fails quietly when broken:

- `src/index.css` imports Tailwind first and `@cladd-ui/react/css` second.
- `src/main.tsx` wraps the app once in `CladdProvider`, which supplies theme, accent color
  and the portals used by dialogs, popovers and toasts.

## Layout

```
src/
  App.tsx                 Tabs, layout, wiring between hooks and panels
  main.tsx                CladdProvider and root render
  index.css               Tailwind + cladd imports, page background
  lib/payload.ts          Payload type, defaults, wire format, validation
  lib/tvApi.ts            fetch wrappers for /health, /notify, DELETE /notify/{id}
  lib/curl.ts             curl command builder
  lib/devices.ts          Device type and host normalization
  lib/presets.ts          Built-in presets
  lib/storage.ts          localStorage helpers
  lib/pageProtocol.ts     Detects HTTPS serving for the mixed-content notice
  hooks/useDevices.ts     Device list, selection, reachability polling
  hooks/useSender.ts      Parallel send/clear with per-device outcomes and toasts
  hooks/usePresets.ts     Built-in plus saved presets
  hooks/useHistory.ts     Session send history
  components/             ComposeForm, CardPreview, SendPanel, DevicesPanel,
                          PresetsBar, HistoryList, CurlExport, Field,
                          InsecureContentNotice
```

## Checks

```sh
npm run build   # type-check and bundle
npm run lint    # oxlint
```
