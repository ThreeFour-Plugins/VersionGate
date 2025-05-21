# VersionGate

VersionGate is a Minecraft server plugin that allows administrators to restrict access to worlds and plugins based on client protocol version. By leveraging ViaVersion, it supports all Minecraft versions without hardcoding specific version numbers.

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/threefour-Plugins/VersionGate/build.yml?branch=main)
![License](https://img.shields.io/github/license/threefour/VersionGate)

## Features

- **World-specific version restrictions**: Block certain client versions from accessing specific worlds
- **Plugin-specific version restrictions**: Control which plugins can be used by which client versions
- **Automatic plugin detection**: Automatically detects installed plugins and adds them to the configuration
- **Runtime configuration reloading**: Changes take effect immediately without server restart
- **Customizable messages**: Fully customizable messages with placeholders

## Requirements

- Minecraft Server: Spigot, Paper, or any compatible fork (1.13+)
- [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/) plugin

## Installation

1. Download the latest release from the [Releases](https://github.com/threefour/VersionGate/releases) page
2. Place the jar file in your server's `plugins` folder
3. Start or restart your server
4. The default configuration will be created at `plugins/VersionGate/config.yml`

## Configuration

The configuration file (`config.yml`) is divided into three main sections:

### Worlds

Define which protocol versions are blocked from accessing specific worlds:

```yaml
worlds:
  world_nether:
    blocked_versions: [47, 340]   # Block 1.8.x and 1.12.x
```

### Plugin Interactions

Control which plugins are available to which protocol versions:

```yaml
plugin_interactions:
  blocked_by_version:
    SomeFancyPlugin: [47, 340]    # Block 1.8.x and 1.12.x
  versions:
    47: [BadPlugin, LegacyWarp]   # Block these plugins for 1.8.x
```

### Messages

Customize messages displayed to players:

```yaml
messages:
  kick_on_login: "&cYou are still on version {version}, which is not allowed in world \"{world}\"."
```

## Commands

- `/versiongate reload` - Reload the configuration
- `/versiongate info` - Display plugin information

## Permissions

- `versiongate.command` - Access to base command
- `versiongate.reload` - Permission to reload configuration
- `versiongate.info` - Permission to view plugin information

## Development

### Building from Source

1. Clone the repository
2. Build using Gradle:

```
./gradlew build
```

The compiled jar will be in `build/libs/`.

### Documentation

Documentation is built using VitePress. To work on the documentation:

1. Install dependencies:
```
npm install
```

2. Start the development server:
```
npm run docs:dev
```

3. Build the documentation:
```
npm run docs:build
```

### Continuous Integration

The project uses GitHub Actions for continuous integration:

- **Build Workflow**: Automatically builds the plugin and documentation
- **Release Workflow**: Attaches built JARs to GitHub Releases
- **Documentation Deployment**: Automatically deploys documentation to GitHub Pages

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute to this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 