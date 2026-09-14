# Web client

`client/` is a React app that talks to the TV directly from the browser. It stores devices and
presets in `localStorage`, previews the card at the TV's proportions while you type, sends to one or
many TVs with per-device results, keeps a session history of sends, and exports any notification as
a curl command.

## Served by the TV

**This is the copy to use.** The APK bundles the built client and the TV serves it at
`http://TV_IP:8090/`, the same origin as the API, so there is no HTTPS block and no setup: open
that address on any phone or laptop on the Wi-Fi, or scan the QR code on the TV's home screen.
The client adds **This TV** as a device automatically when it is loaded that way.

Static files are served without a token; the API behind them still requires one when set. Enter
the token on the device's card in the **Devices** tab.

After upgrading the APK, reload the page on your phone once so the browser fetches the new client
instead of a cached copy.

## Hosted on GitHub Pages

A hosted copy is deployed alongside this documentation on every push to `main`:

```
https://zorenkonte.github.io/tibeepost/app/
```

**The hosted page is HTTPS and the TV is HTTP.** Browsers block a secure page from calling an
insecure address, so on the Pages URL every send fails as "Blocked" until you allow it once for that
site. The client shows a banner explaining this whenever it is served over HTTPS.

- Chrome, Edge, Brave on desktop: click the padlock left of the address, open **Site settings**, set
  **Insecure content** to **Allow**, then reload the page. This is a per-site setting; the rest of
  your browsing is unaffected.
- Mobile browsers have no such switch. Use the copy served by the TV instead.
- Firefox has no per-site switch either. Run the client locally or use the TV-served copy.

## Running locally

```sh
cd client
npm install
npm run dev
```

Vite serves the client on `http://localhost:5173`. Plain `http://` means there is no mixed-content
block, so sends work as soon as you add a device.

To host the built files on your own plain-HTTP server:

```sh
cd client
npm run build
```

`dist/` is static. Set `VITE_BASE` when the app lives under a sub-path, for example
`VITE_BASE=/tibeepost/app/ npm run build`.

## Using the client

- **Devices** holds every TV you send to. Add one by IP address; port `8090` is appended when
  omitted. Give it a nickname and paste the bearer token if the TV has one set. The client polls
  `GET /health` every five seconds and shows Online, Offline or Checking per device. Tick the
  checkbox on each TV you want to send to.
- **Compose** carries every payload field. The preview on the right is a 16:9 stand-in for a 1080p
  TV; card width, position, colors, dim, text sizes and the accent stripe use the same proportions
  as the overlay. Two presets ship, **Loud red alert** and **Quiet info card**; save your own with
  **Save as preset**. Presets and devices live in the browser's local storage.
- **Send** posts to every selected device in parallel and lists success or the actual error per
  device. **Clear card by ID** sends `DELETE /notify/{id}` for the id in the composer.
- **History** lists this session's sends with their result and can resend or reload any of them into
  the composer.
- **curl** turns the current form into a curl command for scripts and other integrations.

The client is one consumer of the [HTTP API](/api/); anything it can do, a curl command can do.
