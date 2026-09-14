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
docs/      Documentation site, published to GitHub Pages
examples/  curl examples for every endpoint
```

Everything stays on your network. There is no cloud relay and no push service.

## Documentation

The full documentation lives at **https://zorenkonte.github.io/tibeepost/**:

- [Getting started](https://zorenkonte.github.io/tibeepost/guide/getting-started): requirements,
  installing the APK, the overlay permission, finding the TV's address, releases
- [TV settings screen](https://zorenkonte.github.io/tibeepost/guide/tv-settings): first-run setup,
  the home screen, every setting
- [Web client](https://zorenkonte.github.io/tibeepost/guide/web-client): the copy served by the TV,
  the hosted copy, running it locally
- [Troubleshooting](https://zorenkonte.github.io/tibeepost/guide/troubleshooting)
- [HTTP API reference](https://zorenkonte.github.io/tibeepost/api/): every endpoint, field, response
  and error
- [Notification behavior](https://zorenkonte.github.io/tibeepost/api/notifications): queueing,
  replacing by id, persistent cards, sound and speech
- [Development](https://zorenkonte.github.io/tibeepost/development): tests, builds, screenshot
  previews, workflows

The hosted web client is at https://zorenkonte.github.io/tibeepost/app/. The copy the TV serves at
`http://TV_IP:8090/` is the one to use from a phone; see the web client page for why.

## Requirements

- Android TV (or any Android device) running Android 8.0 (API 26) or newer.
- `adb` reachable from a PC, to install the APK and grant the overlay permission on TVs that hide
  the settings page for it.

## Install

Download the newest APK from the [Releases page](https://github.com/zorenkonte/tibeepost/releases)
and install it over the network:

```sh
adb connect TV_IP:5555
adb install -r tibeepost-0.2.1.apk
```

Open **TibeePost** from the TV launcher once. The first launch walks through the overlay
permission and sends a test card. If the TV has no settings page for the permission, grant it with
adb; the app prints this exact command on screen:

```sh
adb shell appops set com.zorenkonte.tibeepost SYSTEM_ALERT_WINDOW allow
```

The TV's address, for example `http://192.168.1.50:8090`, is the largest text on the home screen.

## curl quickstart

Replace `TV_IP` with the address above. `examples/curl.sh` has a runnable version of each.

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
except `GET /health`. The [API reference](https://zorenkonte.github.io/tibeepost/api/) lists every
field.

## Development

```sh
cd tv && ./gradlew testDebugUnitTest lint
cd client && npm ci && npm run lint && npm run build
cd docs && npm ci && npm run build
```

Pull requests against `main` run all three in CI. Tags `v*` publish a release with the APK, and
pushes to `main` deploy the docs and the client to GitHub Pages. Details are on the
[Development](https://zorenkonte.github.io/tibeepost/development) page.
