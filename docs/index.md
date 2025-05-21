---
layout: home
hero:
  name: VersionGate
  text: Version-Based Access Control
  tagline: Restrict world and plugin access based on client protocol version
  actions:
    - theme: brand
      text: Get Started
      link: /guide/
    - theme: alt
      text: Download
      link: /download
features:
  - title: World Restrictions
    details: Control which Minecraft versions can access specific worlds
  - title: Plugin Restrictions
    details: Manage which plugins are available to specific client versions
  - title: ViaVersion Integration
    details: Seamlessly integrates with ViaVersion to support all protocol versions
  - title: Easy Configuration
    details: Simple YAML configuration with automatic plugin detection
---

# VersionGate

VersionGate is a Minecraft server plugin that allows server administrators to restrict access to worlds and plugins based on client protocol versions. By leveraging ViaVersion, it supports all Minecraft versions without hardcoding specific version numbers.

## Key Features

- **World-specific version restrictions**: Block certain client versions from accessing specific worlds
- **Plugin-specific version restrictions**: Control which plugins can be used by which client versions
- **Automatic plugin detection**: Automatically detects installed plugins and adds them to the configuration
- **Runtime configuration reloading**: Changes take effect immediately without server restart
- **Customizable messages**: Fully customizable messages with placeholders

## Quick Installation

1. Ensure ViaVersion is installed on your server
2. Download the latest version of VersionGate
3. Place the jar file in your server's plugins folder
4. Restart your server
5. Edit the configuration file at `plugins/VersionGate/config.yml`

See the [Installation Guide](/guide/installation) for detailed instructions. 