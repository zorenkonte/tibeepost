# TV settings screen

The app opens on a single home screen built for the remote. Everything is reachable with the
D-pad: up and down move between rows, left and right change a value, OK presses a button.

- **Header chips** show the server, overlay permission and token state at a glance.
- **Connection** shows the address to send to, `IP:8090`, and a QR code. Scanning it with a phone
  opens the [web client](/guide/web-client) served by the TV itself, already pointed at this TV.
- **Setup** is a checklist: draw over other apps, notification server, network. A red row has a
  **Fix** button that opens the permission guide.
- **Settings tabs**: Card (test notification, default width, duration, dim, position), Colors
  (background, text, accent swatches), Sound (default chime, preview), Security (token), Startup
  (start on boot, run setup again).

## Permission guide

The permission guide lists where the "Display over other apps" switch lives on Android TV, tries the
system permission page and falls back through the app info page and the main Settings app when the
TV lacks it, and shows the adb command for TVs with no page at all:

```sh
adb shell appops set com.zorenkonte.tibeepost SYSTEM_ALERT_WINDOW allow
```

It re-checks every few seconds and reports success on its own.

## First run

The first launch after installing opens a three-step setup instead of the settings screen:

1. **Welcome** explains what the app does and shows the address this TV listens on.
2. **Permission** walks through the overlay permission. When the TV offers a system page for it, a
   **Try system settings** button opens it; otherwise the adb command is shown on screen. The step
   re-checks every few seconds and moves on by itself once the permission is granted. It can be
   skipped, but every following screen then states that TibeePost is unusable until the permission
   is granted, and the settings screen keeps saying so on every launch.
3. **Test** sends a real notification through the local server so you can see a card appear before
   any remote client is involved.

The setup runs once. **Run setup again** under the Startup tab brings it back.

## Settings reference

- **Send test notification** performs a real `POST` to `127.0.0.1:8090`, exercising the full
  server, parser and overlay path.
- **Default card width, duration, dim** are sliders; left and right move them in steps.
- **Default position, sound** cycle with left and right.
- **Background, text color, accent** cycle through swatches; "None" is a valid accent.
- **Auth token** Generate, regenerate or clear. The token is 32 random bytes, base64url encoded.
  See [Authentication](/api/#authentication) for what it protects.
- **Start on boot** toggles the boot receiver.
- **Run setup again** replays the first-run flow.

These become the defaults for fields a `POST` omits. The [API reference](/api/#post-notify) marks
every such field as "TV setting".
