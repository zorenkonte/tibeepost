# HTTP API reference

Base URL: `http://TV_IP:8090`. All responses are JSON with `Content-Type: application/json`, and
every response carries permissive [CORS](#cors) headers so browser apps can call the TV directly.

## Endpoints

| Method | Path | Auth | Purpose |
| --- | --- | --- | --- |
| `POST` | `/notify` | yes | Show a notification, or replace the one with the same `id` |
| `DELETE` | `/notify/{id}` | yes | Remove a notification that is showing or queued |
| `GET` | `/health` | no | Liveness check, returns the app version |
| `GET` | `/info` | yes | Device name, IP, port, screen size, auth and permission state |
| `OPTIONS` | any | no | CORS preflight, returns `204` |
| `GET` | anything else | no | The bundled [web client](/guide/web-client) |

"Auth: yes" means the request needs `Authorization: Bearer <token>` when a token has been set on
the TV. With no token set, nothing requires a header.

## curl quickstart

Replace `TV_IP` with the TV's address. A runnable version of each of these lives in
[`examples/curl.sh`](https://github.com/zorenkonte/tibeepost/blob/main/examples/curl.sh).

::: code-group

```sh [Health]
curl http://TV_IP:8090/health
```

```sh [Simplest]
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"message":"Dinner is ready"}'
```

```sh [Loud]
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
```

```sh [Replace]
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"id":"order-1042","title":"Order #1042","message":"Now being prepared"}'
```

```sh [Persistent]
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"id":"kitchen","message":"Kitchen closes in 10 minutes","persistent":true}'

curl -X DELETE http://TV_IP:8090/notify/kitchen
```

:::

When a token is set on the TV, add `-H 'Authorization: Bearer YOUR_TOKEN'` to every request except
`GET /health`.

## `POST /notify`

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
| `accent` | string (hex) | none (TV setting) | Color of a vertical stripe on the card's left edge. Omit the field to use the TV's default; send `"none"`, `""` or `null` to force no stripe even when the TV has a default. |
| `dim` | number | 0 (TV setting) | Opacity, 0 to 1, of a full-screen black layer behind the card. `0.9` hides what is playing almost completely. |
| `sound` | string | `default` (TV setting) | `none`, `default` for the bundled chime, or an `http(s)` URL of an audio file to stream. |
| `speak` | boolean | `false` | Read the title and message aloud with the TV's text-to-speech engine after the sound finishes. |

Hex colors accept `#RGB`, `#RRGGBB`, `#ARGB` and `#AARRGGBB`, with or without the `#`.
Fields marked "TV setting" take their default from the [TV settings screen](/guide/tv-settings),
so a bare `{"message": "..."}` looks the way the TV owner configured it.

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

## `DELETE /notify/{id}`

Removes the card with that `id` whether it is on screen, waiting in the queue, or the persistent
card that returns after the queue drains.

| Status | Body |
| --- | --- |
| `200` | `{"id": "kitchen", "result": "dismissed"}` or `{"id": "...", "result": "removed-from-queue"}` |
| `404` | `{"id": "kitchen", "result": "unknown"}` |

## `GET /health`

Always unauthenticated so monitoring and the web client's reachability check work without a token.

```json
{"status": "ok", "app": "TibeePost", "version": "0.2.1"}
```

## `GET /info`

```json
{
  "deviceName": "Living room TV",
  "ip": "192.168.1.50",
  "port": 8090,
  "screenWidth": 1920,
  "screenHeight": 1080,
  "version": "0.2.1",
  "authEnabled": false,
  "overlayPermission": true
}
```

`ip` is `null` when the TV has no network address.

## Static files

Any `GET` that is not one of the API paths above is answered from the web client bundled in the
APK, with `index.html` as the fallback for unknown paths so the client's routes work after a
reload. These files are served without a token; the API behind them still requires one when set.

## CORS

Every response, including errors and `401`, carries:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 86400
```

`OPTIONS` on any path returns `204`. This is why a browser page served from anywhere can post to
the TV, and why "works in curl, fails in the browser" is not a thing here. The one exception is a
page served over HTTPS, which the browser itself blocks from calling plain HTTP; see
[the hosted client](/guide/web-client#hosted-on-github-pages).

## Authentication

Auth is off by default. Generate a token on the TV settings screen and the server starts requiring
`Authorization: Bearer <token>` on `/notify`, `/notify/{id}` and `/info`. Clear the token to turn
auth off again. Tokens are compared in constant time. The token travels in plain HTTP on your LAN;
it keeps guests and stray scripts from posting, not a determined attacker with a packet capture.
