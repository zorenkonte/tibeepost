import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'TibeePost',
  description: 'Turn an Android TV into a notification screen with a plain HTTP API.',
  base: '/tibeepost/',
  cleanUrls: true,
  lastUpdated: true,
  head: [['link', { rel: 'icon', type: 'image/svg+xml', href: '/tibeepost/logo.svg' }]],
  themeConfig: {
    logo: '/logo.svg',
    nav: [
      { text: 'Guide', link: '/guide/getting-started', activeMatch: '/guide/' },
      { text: 'API', link: '/api/', activeMatch: '/api/' },
      { text: 'Development', link: '/development' },
      { text: 'Open the client', link: 'https://zorenkonte.github.io/tibeepost/app/' },
      { text: 'Releases', link: 'https://github.com/zorenkonte/tibeepost/releases' },
    ],
    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Getting started', link: '/guide/getting-started' },
          { text: 'TV settings screen', link: '/guide/tv-settings' },
          { text: 'Web client', link: '/guide/web-client' },
          { text: 'Troubleshooting', link: '/guide/troubleshooting' },
        ],
      },
      {
        text: 'API',
        items: [
          { text: 'HTTP API reference', link: '/api/' },
          { text: 'Notification behavior', link: '/api/notifications' },
        ],
      },
      {
        text: 'Project',
        items: [{ text: 'Development', link: '/development' }],
      },
    ],
    socialLinks: [{ icon: 'github', link: 'https://github.com/zorenkonte/tibeepost' }],
    editLink: {
      pattern: 'https://github.com/zorenkonte/tibeepost/edit/main/docs/:path',
      text: 'Edit this page on GitHub',
    },
    search: { provider: 'local' },
    outline: [2, 3],
    footer: {
      message: 'Everything stays on your network. No cloud relay, no push service.',
    },
  },
})
