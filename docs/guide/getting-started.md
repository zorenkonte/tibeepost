# Getting started

TibeePost is an Android TV app that runs a foreground service with an embedded HTTP server on
port `8090`. Anything on the LAN that can make an HTTP request can `POST /notify`, and the TV
draws a large card over whatever is playing, optionally dims the rest of the screen, plays a
chime or a sound URL, and can read the message aloud.

The HTTP API is the product. The bundled web client is one consumer of it; curl, a shell script,
Home Assistant, a point-of-sale system or a cron job work just as well. Everything stays on your
network. There is no cloud relay and no push service.

## Requirements

TV side:

- Android TV (or any Android device) running Android 8.0 (API 26) or newer.
  Check yours with `adb shell getprop ro.build.version.sdk`; the number must be 26 or higher.
- Developer options with USB or network debugging enabled, so `adb` can install the APK and
  grant the overlay permission.

Only needed when building from source:

- JDK 17 or newer (JDK 21 is fine).
- Android SDK with platform 36 and build-tools 36. Android Studio installs these, or use the
  command line tools and `sdkmanager "platforms;android-36" "build-tools;36.0.0"`.
- Node.js 20 or newer. The APK build runs `npm run build` in `client/` and packs the result into
  the app so the TV can serve the web client itself. Without `npm` on the PATH the APK still builds,
  just without the bundled client.

## Install the TV app

### From a release

Every tag that starts with `v` publishes a GitHub Release with the APK attached. Grab the newest
APK from the [Releases page](https://github.com/zorenkonte/tibeepost/releases) and install it over
the network:

```sh
adb connect TV_IP:5555
adb install -r tibeepost-0.2.1.apk
```

`-r` upgrades an installed copy in place. Releases and locally built APKs share the same debug
signing key on purpose, so they upgrade each other freely; see [Releases](#releases) for why.

Without adb, copy the APK to a USB stick or use any "send files to TV" app and open it with a file
manager on the TV. You still need adb once to grant the overlay permission on TVs that hide the
settings page for it.

### From source

```sh
cd tv
./gradlew assembleDebug
```

If Gradle cannot find the SDK, create `tv/local.properties` with `sdk.dir=/path/to/android-sdk`.
The APK lands at `tv/app/build/outputs/apk/debug/app-debug.apk`. Install it the same way:

```sh
adb connect TV_IP:5555
adb install -r tv/app/build/outputs/apk/debug/app-debug.apk
```

### First launch

Open **TibeePost** from the TV launcher once. Opening it starts the foreground service, which keeps
the HTTP server alive, and begins a short [first-run setup](/guide/tv-settings#first-run) that walks
through the overlay permission and sends a test card. After that the app opens on the home screen
with the TV's address in large type.

## Grant the overlay permission

TibeePost draws its cards with `SYSTEM_ALERT_WINDOW`, the "display over other apps" permission.
Many Android TV builds, including CHiQ sets, ship no settings page where you can grant it, so
grant it with adb:

```sh
adb shell appops set com.zorenkonte.tibeepost SYSTEM_ALERT_WINDOW allow
```

The TV detects the missing permission and prints this exact command on screen. Until it is
granted, the server still answers, `GET /info` reports `"overlayPermission": false`, and
`POST /notify` returns `503` with the command in the error message instead of drawing a card.

If the device does have the settings page, the TV app also offers a **Try system settings**
button that opens it. The setup checklist on the home screen keeps a red row with a **Fix**
button until the permission is granted.

## Find the TV's IP address

- Open TibeePost on the TV. The address, for example `http://192.168.1.50:8090`, is the
  largest text on the screen, next to a QR code that opens the web client on a phone.
- Or ask adb: `adb shell ip -4 addr show wlan0` (use `eth0` for a wired TV).
- Or look at your router's client list.

Give the TV a DHCP reservation so the address does not change.

## Send your first notification

Replace `TV_IP` with the address above.

```sh
curl http://TV_IP:8090/health

curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"message":"Dinner is ready"}'
```

A white card appears in the center of the screen for a few seconds. From here:

- The [API reference](/api/) lists every field you can put on that JSON body.
- [Notification behavior](/api/notifications) explains queueing, replacing by id and persistent cards.
- The [web client](/guide/web-client) lets you compose cards from a phone with a live preview.
- When a token is set on the TV, add `-H 'Authorization: Bearer YOUR_TOKEN'` to every request
  except `GET /health`.

## Releases

Every tag that starts with `v` publishes a GitHub Release with the APK attached, built by
`.github/workflows/release.yml`. To cut a release from a checkout:

```sh
git tag v0.2.1
git push origin v0.2.1
```

The workflow runs the JVM tests, builds the APK with `versionName` taken from the tag (`v0.2.1`
becomes `0.2.1`) and `versionCode` set to the workflow run number, which only ever goes up, so
every release installs over the previous one.

Without a local git checkout, publish from the Actions tab instead: open **Release APK**, choose
**Run workflow**, and enter a plain version such as `0.2.1`. The run creates the `v0.2.1` tag on the
current `main` and publishes the full release. Any other input, or the default, produces a
pre-release named `manual-<run>` for trying a build.

APKs are signed with `tv/debug.keystore`, a debug key committed to the repo on purpose. Android only
allows `adb install -r` to upgrade an app when the new APK is signed with the same key as the
installed one; a fresh debug key per CI run would force an uninstall before every update. The key
carries no secrets and is unsuitable for Play Store publishing, which this app does not do. Local
`./gradlew assembleDebug` builds use the same key, so locally built and released APKs upgrade each
other freely.

Pushes to `main` and pull requests also run `.github/workflows/ci.yml`, which executes the TV tests,
Android lint, the client build and this documentation build, so a tag never publishes broken code.
