# Troubleshooting

## `POST /notify` returns 503, or the TV says the overlay permission is missing

Run the appops command from [Grant the overlay permission](/guide/getting-started#grant-the-overlay-permission):

```sh
adb shell appops set com.zorenkonte.tibeepost SYSTEM_ALERT_WINDOW allow
```

Reopen the app afterwards; the status line updates within a few seconds.

## Nothing listens on port 8090

Open TibeePost once after installing; the service starts from the launcher and, when **Start on
boot** is on, after every reboot. Some TV builds delay `BOOT_COMPLETED` for apps that have never
been opened.

Also check the port. adb's network debugging uses port `5555`; TibeePost listens on `8090`. A
device entry such as `192.168.1.50:5555` in the web client fails as unreachable.

## "Port 8090 is busy" in the service notification

Something else on the TV owns the port. TibeePost retries with backoff; free the port or reboot the
TV.

## Images or sound URLs never load

Use `http://` or `https://` URLs reachable from the TV itself. Cleartext HTTP is allowed. Images
over 5 MB or slower than 5 s are skipped.

## The browser client says "Unreachable" or "Blocked" while curl works

Either the TV is on a different network or VLAN from the laptop, or the page is served over
`https://` and the browser blocked the plain-HTTP request to the TV. See
[Hosted on GitHub Pages](/guide/web-client#hosted-on-github-pages) for the per-site browser
setting, or open the copy the TV serves at `http://TV_IP:8090/`.

## The accent stripe shows although the client has it off

Update to 0.2.0 or newer. Older TV builds applied the TV's default accent when the field was
omitted; the client now sends `"accent": "none"` explicitly, and the TV honors it.

## The client shows a blank page when opened from the TV's address

Update to 0.2.1 or newer. Plain-HTTP pages lack some browser APIs that the client used for ids
and clipboard copies; the client now falls back and shows an error screen instead of a blank
page if anything else goes wrong. Reload the page once after upgrading.

## Cards appear but the TV's own dialogs hide them

System dialogs and some apps are allowed to hide overlays. That is Android behavior, not a
TibeePost setting.

## The APK refuses to install

Either the TV runs Android below 8.0, or the installed copy was signed with a different key. Check
`adb shell getprop ro.build.version.sdk`; the app needs 26 or higher. Releases and local builds
share one signing key, so a signature mismatch means a build from a fork or a modified keystore.
Uninstall the old copy first in that case.
