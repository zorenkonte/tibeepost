# Development

```
tv/        Android TV app (Kotlin, Jetpack Compose settings screen, embedded HTTP server)
client/    React web client for composing and sending notifications
docs/      This documentation site
examples/  curl examples for every endpoint
```

## TV app

Tests run on the JVM without a device. They start the real embedded HTTP server on an ephemeral
port and drive it with plain HTTP requests, covering routing, CORS, auth, payload validation, the
queue and static file serving:

```sh
cd tv
./gradlew testDebugUnitTest
./gradlew lint
```

Build a specific version locally the same way the release workflow does:

```sh
cd tv
./gradlew assembleDebug -PtibeeVersionName=0.2.1 -PtibeeVersionCode=42
```

The APK build runs `npm ci` and `npm run build` in `client/` and packs the output into the app's
assets so the TV can serve the web client. Without `npm` on the PATH both steps are skipped and the
APK builds without the client.

Render every screen of the TV app without a device. The previews live in
`tv/app/src/screenshotTest` and the images land in `tv/app/src/screenshotTestDebug/reference/`:

```sh
cd tv
./gradlew updateDebugScreenshotTest
```

Regenerate the chime after editing `tv/tools/gen_chime.py`:

```sh
cd tv
python3 tools/gen_chime.py app/src/main/res/raw/chime.wav
```

## Web client

```sh
cd client
npm install
npm run dev
npm run lint
npm run build
```

Set `VITE_BASE` when the app lives under a sub-path. The Pages deploy builds it with
`VITE_BASE=/tibeepost/app/`; the TV build uses `/`.

## Documentation

This site is built with VitePress from the Markdown in `docs/`:

```sh
cd docs
npm install
npm run dev
npm run build
```

The build fails on broken internal links, and CI runs it on every pull request.

## Workflows

| Workflow | Runs on | Does |
| --- | --- | --- |
| `ci.yml` | pushes to `main`, pull requests | TV tests, Android lint, APK build, client lint and build, docs build |
| `release.yml` | tags `v*`, manual dispatch | JVM tests, versioned APK, GitHub Release with the APK attached |
| `pages.yml` | pushes to `main` touching `client/` or `docs/` | Builds the docs and the client and deploys both to GitHub Pages |

The Pages deploy places the docs at the site root and the client under `/app/`. One-time setup in
the repository: **Settings > Pages > Build and deployment > Source** must be **GitHub Actions**.

## Contributing

Work happens on a branch and lands on `main` through a pull request; CI must be green. Commits are
atomic with imperative subjects. Source files carry no comments; the documentation in `docs/` is
where explanations live.
