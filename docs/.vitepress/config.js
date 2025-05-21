export default {
  title: 'VersionGate',
  description: 'Minecraft plugin for version-based world and plugin access control',
  base: '/VersionGate/',
  outDir: '../public',
  head: [
    ['meta', { name: 'theme-color', content: '#3eaf7c' }],
    ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
    ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }]
  ],
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/guide/' },
      { text: 'Download', link: '/download' },
      { text: 'GitHub', link: 'https://github.com/threefour/VersionGate' }
    ],
    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Introduction', link: '/guide/' },
          { text: 'Installation', link: '/guide/installation' },
          { text: 'Configuration', link: '/guide/configuration' },
          { text: 'Commands', link: '/guide/commands' }
        ]
      }
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/threefour/VersionGate' }
    ],
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024 threefour'
    }
  }
} 