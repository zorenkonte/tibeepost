---
layout: home

hero:
  name: TibeePost
  text: Your Android TV as a notification screen
  tagline: One HTTP request draws a large card over whatever is playing. No cloud, no accounts, nothing leaves your LAN.
  image:
    src: /logo.svg
    alt: TibeePost
  actions:
    - theme: brand
      text: Get started
      link: /guide/getting-started
    - theme: alt
      text: Open the web client
      link: https://zorenkonte.github.io/tibeepost/app/
    - theme: alt
      text: Download the APK
      link: https://github.com/zorenkonte/tibeepost/releases/latest

features:
  - icon: 📡
    title: A plain HTTP API
    details: POST a JSON body to port 8090 and the card appears. curl, shell scripts, Home Assistant, a point-of-sale system or a cron job all work the same way.
    link: /api/
    linkText: API reference
  - icon: 🖼️
    title: Cards that get noticed
    details: Title, message, image, icon, accent stripe, colors, position, width, dim layer, chime or sound URL, and optional text to speech.
    link: /api/notifications
    linkText: Notification behavior
  - icon: 📱
    title: A web client served by the TV
    details: Scan the QR code on the TV's home screen and compose notifications from any phone or laptop on the Wi-Fi, with a live preview at the TV's proportions.
    link: /guide/web-client
    linkText: Web client
  - icon: 🔒
    title: LAN only, optional token
    details: Nothing is relayed through the internet. Turn on bearer authentication from the TV when the network is shared.
    link: /api/#authentication
    linkText: Authentication
---

## Try it in thirty seconds

Install the APK, grant the overlay permission, then from any machine on the same network:

```sh
curl -X POST http://TV_IP:8090/notify \
  -H 'Content-Type: application/json' \
  -d '{"title": "Kitchen", "message": "Dinner is ready"}'
```

The card shows for eight seconds and slides away. Everything else, from colors and sounds to
persistent cards you clear yourself, is a field on that JSON body. Start with
[Getting started](/guide/getting-started) or jump to the [API reference](/api/).
