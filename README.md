# TibeePost

TibeePost turns an Android TV into a notification screen. A small app runs a foreground
service with an embedded HTTP server on port `8090`. Anything on the LAN that can make an
HTTP request can `POST /notify`, and the TV draws a large card over whatever is playing,
optionally dims the rest of the screen, plays a chime or a sound URL, and can read the
message aloud.

The HTTP API is the product. The bundled web client is one consumer of it; curl, a shell
script, Home Assistant, a point-of-sale system or a cron job work just as well.

```
tv/        Android TV app (Kotlin, Jetpack Compose settings screen, NanoHTTPD server)
client/    React web client for composing and sending notifications
examples/  curl examples for every endpoint
```

Everything stays on your network. There is no cloud relay and no push service.

## Contents

- [Requirements](#requirements)
- [Install the TV app](#install-the-tv-app)
- [Releases](#releases)
- [Grant the overlay permission](#grant-the-overlay-permission)
- [Find the TV's IP address](#find-the-tvs-ip-address)
- [curl quickstart](#curl-quickstart)
- [API reference](#api-reference)
- [Notification behavior](#notification-behavior)
- [TV settings screen](#tv-settings-screen)
- [Web client](#web-client)
- [Hosted client](#hosted-client)
- [Troubleshooting](#troubleshooting)
- [Development](#development)

## Requirements

TV side:

- Android TV (or any Android device) running Android 8.0 (API 26) or newer.
  Check yours with `adb shell getprop ro.build.version.sdk`; the number must be 26 or higher.
- Developer options with USB or network debugging enabled, so `adb` can install the APK and
  grant the overlay permission.

Build side:

- JDK 17 or newer (JDK 21 is fine).
- Android SDK with platform 35 and build-tools 35. Android Studio installs these, or use the
  command line tools and `sdkmanager "platforms;android-35" "build-tools;35.0.0"`.
- Node.js 20 or newer for the web client.

## Install the TV app

Build a debug APK:

```sh
cd tv
./gradlew assembleDebug
```

If Gradle cannot find the SDK, create `tv/local.properties` with `sdk.dir=/path/to/android-sdk`.
The APK lands at `tv/app/build/outputs/apk/debug/app-debug.apk`.

Install it over the network:

```sh
adb connect TV_IP:5555
adb install -r tv/app/build/outputs/apk/debug/app-debug.apk
```

Then open **TibeePost** from the TV launcher once. Opening it starts the foreground service,
which keeps the HTTP server alive, and shows the settings screen with the TV's address in
large type.

## Releases

Every tag that starts with `v` publishes a GitHub Release with the APK attached, built by
`.github/workflows/release.yml`. Grab the newest APK from the
[Releases page](https://github.com/zorenkonte/tibeepost/releases) instead of building locally:

```sh
adb connect TV_IP:5555
adb install -r tibeepost-0.2.0.apk
```

To cut a release:

```sh
git tag v0.2.0
git push origin v0.2.0
```

The workflow runs the JVM tests, builds the APK with `versionName` taken from the tag (`v0.2.0`
becomes `0.2.0`) and `versionCode` set to the workflow run number, which only ever goes up, so
every release installs over the previous one. Running the workflow by hand from the Actions tab
produces a pre-release named `manual-<run>` for trying a branch.

APKs are signed with `tv/debug.keystore`, a debug key committed to the repo on purpose. Android only
allows `adb install -r` to upgrade an app when the new APK is signed with the same key as the
installed one; a fresh debug key per CI run would force an uninstall before every update. The key
carries no secrets and is unsuitable for Play Store publishing, which this app does not do. Local
`./gradlew assembleDebug` builds use the same key, so locally built and released APKs upgrade each
other freely.

Pushes to `main` and pull requests also run `.github/workflows/ci.yml`, which executes the TV tests,
Android lint and the client build, so a tag never publishes broken code.

## Grant the overlay permission

TibeePost draws its cards with `SYSTEM_ALERT_WINDOW`, the "display over other apps"
permission. Many Android TV builds, including CHiQ sets, ship no settings page where you can
grant it, so grant it with adb:

```sh
adb shell appops set com.zorenkonte.tibeepost SYSTEM_ALERT_WINDOW allow
```

The TV settings screen detects the missing permission and prints this exact command on
screen. Until it is granted, the server still answers, `GET /info` reports
`"overlayPermission": false`, and `POST /notify` returns `503` with the command in the error
message instead of drawing a card.

If the device does have the settings page, the TV app also offers a **Try system settings**
button that opens it.

## Find the TV's IP address

- Open TibeePost on the TV. The address, for example `http://192.168.1.50:8090`, is the
  largest text on the screen.
- Or ask adb: `adb shell ip -4 addr show wlan0` (use `eth0` for a wired TV).
- Or look at your router's client list.

Give the TV a DHCP reservation so the address does not change.

## curl quickstart

Replace `TV_IP` with the address above.

```sh
# Is it alive?
curl http://TV_IP:8090/health

# The simplest notification. Only "message" is required.
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"message":"Dinner is ready"}'

# A loud one: red card, dimmed screen, chime, spoken aloud.
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{
    "id": "order-1042",
    "title": "Order #1042",
    "message": "New order received",
    "duration": 20,
    "background": "#B91C1C",
    "textColor": "#FFFFFF",
    "accent": "#FDE047",
    "dim": 0.9,
    "speak": true
  }'

# Update that card in place by reusing the id.
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"id":"order-1042","title":"Order #1042","message":"Now being prepared"}'

# A card that stays until you clear it.
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"id":"kitchen","message":"Kitchen closes in 10 minutes","persistent":true}'

curl -X DELETE http://TV_IP:8090/notify/kitchen
```

When a token is set on the TV, add `-H 'Authorization: Bearer YOUR_TOKEN'` to every request
except `GET /health`. `examples/curl.sh` has a runnable version of each of these.

## API reference

Base URL: `http://TV_IP:8090`. All responses are JSON with
`Content-Type: application/json`, and every response carries permissive CORS headers so
browser apps can call the TV directly.

### Endpoints

| Method | Path | Auth | Purpose |
| --- | --- | --- | --- |
| `POST` | `/notify` | yes | Show a notification, or replace the one with the same `id` |
| `DELETE` | `/notify/{id}` | yes | Remove a notification that is showing or queued |
| `GET` | `/health` | no | Liveness check, returns the app version |
| `GET` | `/info` | yes | Device name, IP, port, screen size, auth and permission state |
| `OPTIONS` | any | no | CORS preflight, returns `204` |

"Auth: yes" means the request needs `Authorization: Bearer <token>` when a token has been set
on the TV. With no token set, nothing requires a header.

### `POST /notify`

Request body is a JSON object. Only `message` is required.

| Field | Type | Default | Meaning |
| --- | --- | --- | --- |
| `id` | string | random UUID | Identifies the card. Posting again with the same `id` replaces the card in place instead of queueing a second one. Max 128 characters, no `/`. |
| `title` | string | none | Bold heading. Hidden when omitted. Truncated at 200 characters. |
| `message` | string | required | Body text. Truncated at 2000 characters. |
| `image` | string (URL) | none | `http(s)` URL of an image shown below the text, fetched with a 5 s timeout and a 5 MB limit. If it cannot be fetched the card shows without it. |
| `icon` | string (URL) | none | `http(s)` URL of a small icon shown next to the title. |
| `duration` | number | 15 (TV setting) | Seconds to show the card, 1 to 3600. Ignored when `persistent` is true. |
| `persistent` | boolean | `false` | Keep the card until `DELETE /notify/{id}` removes it. |
| `position` | string | `center` (TV setting) | `center`, `top-left`, `top-right`, `bottom-left` or `bottom-right`. Corners keep a 48 dp margin. |
| `widthPercent` | number | 60 (TV setting) | Card width as a percentage of screen width, 10 to 100. Height follows the content. |
| `background` | string (hex) | `#FFFFFF` (TV setting) | Card background. |
| `textColor` | string (hex) | `#111111` (TV setting) | Title and message color. |
| `accent` | string (hex) | none (TV setting) | Color of a vertical stripe on the card's left edge. Omit for no stripe. |
| `dim` | number | 0 (TV setting) | Opacity, 0 to 1, of a full-screen black layer behind the card. `0.9` hides what is playing almost completely. |
| `sound` | string | `default` (TV setting) | `none`, `default` for the bundled chime, or an `http(s)` URL of an audio file to stream. |
| `speak` | boolean | `false` | Read the title and message aloud with the TV's text-to-speech engine after the sound finishes. |

Hex colors accept `#RGB`, `#RRGGBB`, `#ARGB` and `#AARRGGBB`, with or without the `#`.
Fields marked "TV setting" take their default from the TV settings screen, so a bare
`{"message": "..."}` looks the way the TV owner configured it.

Successful response, `200`:

```json
{"id": "order-1042", "result": "shown"}
```

`result` is one of:

| Value | Meaning |
| --- | --- |
| `shown` | Drawn immediately |
| `replaced` | A card with the same `id` was on screen and was updated in place |
| `queued` | Another card is showing; this one displays when it is done |
| `queued-dropped-oldest` | Queued, and the queue was full so the oldest waiting card was dropped |

Errors:

| Status | When | Body |
| --- | --- | --- |
| `400` | Malformed JSON or a field of the wrong type or range | `{"error": "field 'duration' must be a number"}` |
| `401` | Token set on the TV and the header is missing or wrong | `{"error": "missing or invalid bearer token"}` plus `WWW-Authenticate: Bearer` |
| `413` | Body larger than 256 KB | `{"error": "body larger than 262144 bytes"}` |
| `503` | Overlay permission not granted | `{"id": "...", "result": "no-overlay-permission", "error": "overlay permission missing; run: adb shell appops set ..."}` |
| `500` | Unexpected failure while drawing | `{"error": "..."}` |

Bad input never crashes the service; every error names the field that caused it.

### `DELETE /notify/{id}`

Removes the card with that `id` whether it is on screen, waiting in the queue, or the
persistent card that returns after the queue drains.

| Status | Body |
| --- | --- |
| `200` | `{"id": "kitchen", "result": "dismissed"}` or `{"id": "...", "result": "removed-from-queue"}` |
| `404` | `{"id": "kitchen", "result": "unknown"}` |

### `GET /health`

Always unauthenticated so monitoring and the web client's reachability check work without a
token.

```json
{"status": "ok", "app": "TibeePost", "version": "0.1.0"}
```

### `GET /info`

```json
{
  "deviceName": "Living room TV",
  "ip": "192.168.1.50",
  "port": 8090,
  "screenWidth": 1920,
  "screenHeight": 1080,
  "version": "0.1.0",
  "authEnabled": false,
  "overlayPermission": true
}
```

`ip` is `null` when the TV has no network address.

### CORS

Every response, including errors and `401`, carries:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 86400
```

`OPTIONS` on any path returns `204`. This is why a browser page served from anywhere can post
to the TV, and why "works in curl, fails in the browser" is not a thing here.

### Authentication

Auth is off by default. Generate a token on the TV settings screen and the server starts
requiring `Authorization: Bearer <token>` on `/notify`, `/notify/{id}` and `/info`. Clear the
token to turn auth off again. Tokens are compared in constant time. The token travels in plain
HTTP on your LAN; it keeps guests and stray scripts from posting, not a determined attacker
with a packet capture.

## Notification behavior

- **One card at a time.** While a card is showing, new notifications queue in arrival order.
  The queue holds 20; when full, the oldest waiting card is dropped.
- **Replace by id.** A `POST` with an `id` that is already on screen updates that card in
  place and restarts its timer. An `id` already waiting in the queue is replaced in its
  queue position.
- **Persistent cards** stay until deleted. Transient notifications that arrive meanwhile
  temporarily take over the screen, and the persistent card comes back when the queue is
  empty. Only one persistent card exists at a time; a new one replaces the previous one.
- **Dim layer** is a full-screen black view added before the card and removed with it, so
  the two always appear and disappear together.
- **Images** are fetched off the main thread with a timeout. A failed fetch shows the card
  without the image rather than not at all. Large images are downsampled to fit the screen.
- **Sound** plays through the notification audio stream: the bundled chime via `SoundPool`,
  URLs via `MediaPlayer`. Text-to-speech starts after the sound finishes. A TV without a
  speech engine simply skips speaking.
- **Text scales with screen density.** The title is 30 sp and the message 24 sp, readable
  from a couch on a 1080p or 4K panel.
- The card never takes focus or touch input, so the remote keeps controlling the app
  underneath.

## TV settings screen

### First run

The first launch after installing opens a three-step setup instead of the settings screen:

1. **Welcome** explains what the app does and shows the address this TV listens on.
2. **Permission** walks through the overlay permission. When the TV offers a system page for it, a
   "Try system settings" button opens it; otherwise the adb command is shown on screen. The step
   re-checks every few seconds and moves on by itself once the permission is granted. It can be
   skipped, but every following screen then states that TibeePost is unusable until the permission
   is granted, and the settings screen keeps saying so on every launch.
3. **Test** sends a real notification through the local server so you can see a card appear before
   any remote client is involved.

The setup runs once. A "Run setup again" row at the bottom of the settings screen brings it back.

### Settings

Open TibeePost from the launcher. Everything is reachable with the D-pad; there are no text
fields.

- **Address** `http://IP:8090`, in large type, plus server, auth and overlay permission
  status. When the permission is missing the adb command appears here.
- **Send test notification** performs a real `POST` to `127.0.0.1:8090`, exercising the full
  server, parser and overlay path.
- **Auth token** Generate, view or clear. The token is 32 random bytes, base64url encoded.
- **Start on boot** Toggle the boot receiver.
- **Default sound** None or chime, with a Preview button.
- **Default card width, duration, dim, position, background, text color, accent** Press left
  and right on a row to step the value. These become the defaults for fields a `POST` omits.
- **Run setup again** Replays the first-run flow.

## Web client

`client/` is a React app that talks to the TV directly from the browser. It stores devices
and presets in `localStorage`, previews the card at the TV's proportions while you type, sends
to one or many TVs with per-device results, keeps a session history of sends, and exports any
notification as a curl command. See [client/README.md](client/README.md) for running it.

```sh
cd client
npm install
npm run dev
```

## Hosted client

`.github/workflows/pages.yml` deploys the client to GitHub Pages on every push to `main` that touches
`client/`, at:

```
https://zorenkonte.github.io/tibeepost/
```

One-time setup in the repository: **Settings > Pages > Build and deployment > Source** must be set to
**GitHub Actions**. Until then the deploy job fails with a "Pages not enabled" error. The workflow can
also be started by hand from the Actions tab.

**The hosted page is HTTPS and the TV is HTTP.** Browsers block a secure page from calling an
insecure address, so on the Pages URL every send fails as "Blocked" until you allow it once for that
site. The client shows a banner explaining this whenever it is served over HTTPS.

- Chrome, Edge, Brave: click the padlock left of the address, open **Site settings**, set
  **Insecure content** to **Allow**, then reload the page. This is a per-site setting; the rest of
  your browsing is unaffected.
- Firefox has no per-site switch. Run the client locally with `npm run dev` instead, or host the
  `dist/` folder on a plain `http://` server on your LAN.

Running locally over `http://localhost` has none of this friction, which is why the README leads
with it.

## Troubleshooting

**`POST /notify` returns 503 or the TV settings screen says the overlay permission is
missing.** Run the appops command from [Grant the overlay permission](#grant-the-overlay-permission).
Reopen the app afterwards; the status line updates within a few seconds.

**Nothing listens on port 8090.** Open TibeePost once after installing; the service starts
from the launcher and, when "Start on boot" is on, after every reboot. Some TV builds delay
`BOOT_COMPLETED` for apps that have never been opened.

**"Port 8090 is busy" in the service notification.** Something else on the TV owns the port.
TibeePost retries with backoff; free the port or reboot the TV.

**Images or sound URLs never load.** Use `http://` or `https://` URLs reachable from the TV
itself. Cleartext HTTP is allowed. Images over 5 MB or slower than 5 s are skipped.

**The browser client says "Unreachable" or "Blocked" while curl works.** Either the TV is on a
different network or VLAN from the laptop, or the page is served over `https://` and the browser
blocked the plain-HTTP request to the TV. See [Hosted client](#hosted-client) for the per-site
browser setting, or run the client over `http://`.

**Cards appear but the TV's own dialogs hide them.** System dialogs and some apps are allowed
to hide overlays. That is Android behavior, not a TibeePost setting.

**The APK refuses to install.** The TV runs Android below 8.0. Check
`adb shell getprop ro.build.version.sdk`; the app needs 26 or higher.

## Branches

The release line is `main`: the CI, Pages and release workflows all watch it. If the repository still
shows `claude/tibeepost-build-5p7u32` as its only branch, create `main` from it and make it the
default under **Settings > General > Default branch**:

```sh
git push origin claude/tibeepost-build-5p7u32:main
```

## Development

TV app tests run on the JVM without a device. They start the real embedded HTTP server on an
ephemeral port and drive it with plain HTTP requests, covering routing, CORS, auth, payload
validation and the queue:

```sh
cd tv
./gradlew testDebugUnitTest
./gradlew lint
```

Build a specific version locally the same way the release workflow does:

```sh
cd tv
./gradlew assembleDebug -PtibeeVersionName=0.2.0 -PtibeeVersionCode=42
```

Regenerate the chime after editing `tv/tools/gen_chime.py`:

```sh
cd tv
python3 tools/gen_chime.py app/src/main/res/raw/chime.wav
```

Client checks:

```sh
cd client
npm run build
npm run lint
```
