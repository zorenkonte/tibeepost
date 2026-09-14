# Notification behavior

What the TV does with the cards once they arrive.

## One card at a time

While a card is showing, new notifications queue in arrival order. The queue holds 20; when full,
the oldest waiting card is dropped and the response says `queued-dropped-oldest`.

## Replace by id

A `POST` with an `id` that is already on screen updates that card in place and restarts its timer.
The response says `replaced`. An `id` already waiting in the queue is replaced in its queue
position and the response says `queued`.

Reusing a stable id per source is the way to build status displays: a kitchen order, a build job or
a doorbell each keep one id and post updates to it rather than stacking new cards.

## Persistent cards

A card with `"persistent": true` stays until `DELETE /notify/{id}` removes it. Transient
notifications that arrive meanwhile temporarily take over the screen, and the persistent card comes
back when the queue is empty. Only one persistent card exists at a time; a new one replaces the
previous one.

## Accent stripe

The `accent` field draws a vertical stripe on the card's left edge. Omit it to use the TV's default
accent; send `"none"`, `""` or `null` to force no stripe even when the TV has a default set.

## Dim layer

`dim` adds a full-screen black view behind the card at the given opacity. It is added before the
card and removed with it, so the two always appear and disappear together.

## Images

Images are fetched off the main thread with a 5 s timeout and a 5 MB limit. A failed fetch shows
the card without the image rather than not at all. Large images are downsampled to fit the screen.
Plain `http://` URLs are allowed.

## Sound and speech

Sound plays through the notification audio stream: the bundled chime for `default`, or a streamed
audio file for a URL. Text-to-speech starts after the sound finishes when `speak` is true. A TV
without a speech engine simply skips speaking.

## Text and focus

The title is 30 sp and the message 24 sp, so text scales with screen density and reads from a couch
on a 1080p or 4K panel. The card never takes focus or touch input, so the remote keeps controlling
the app underneath.

## Without the overlay permission

The server still answers. `GET /info` reports `"overlayPermission": false` and `POST /notify`
returns `503` with the adb command to run in the error message. See
[Grant the overlay permission](/guide/getting-started#grant-the-overlay-permission).
