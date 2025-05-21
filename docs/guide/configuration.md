# Configuration

VersionGate uses a YAML configuration file to define all plugin settings. This guide explains each section of the configuration and how to customize it.

## Configuration Location

The configuration file is located at:

```
plugins/VersionGate/config.yml
```

After installing VersionGate and starting your server, this file will be automatically created with default values.

## Understanding Protocol Versions

VersionGate uses protocol version numbers to identify Minecraft client versions. Here are some common protocol versions:

| Minecraft Version | Protocol Version |
|-------------------|------------------|
| 1.7.1             | 4                |
| 1.7.2-1.7.5       | 5                |
| 1.8-1.8.9         | 47               |
| 1.12-1.12.2       | 340              |
| 1.16.5            | 754              |
| 1.17.1            | 757              |

For a complete list of protocol versions, see the [Minecraft Wiki](https://wiki.vg/Protocol_version_numbers).

## Configuration Structure

The configuration file has three main sections:

1. `worlds`: Controls which versions can access specific worlds
2. `plugin_interactions`: Controls which plugins are available to which versions
3. `messages`: Customizable messages displayed to players

Let's look at each section in detail:

### Worlds Section

The `worlds` section defines which protocol versions are blocked from accessing specific worlds:

```yaml
worlds:
  world_nether:
    blocked_versions: [47, 340]   # Block 1.8.x and 1.12.x
  world_old:
    blocked_versions: [5, 4]      # Block 1.7.2 & 1.7.1
```

In this example:
- Players using Minecraft 1.8.x or 1.12.x cannot access the world named `world_nether`
- Players using Minecraft 1.7.1 or 1.7.2 cannot access the world named `world_old`

To add a new world restriction:

```yaml
worlds:
  your_world_name:
    blocked_versions: [list, of, protocol, versions]
```

Replace `your_world_name` with the actual name of your world folder, and list the protocol versions you want to block.

### Plugin Interactions Section

The `plugin_interactions` section controls which plugins are available to which protocol versions. It has two subsections:

#### 1. blocked_by_version

This subsection lists plugins and which protocol versions cannot use them:

```yaml
plugin_interactions:
  blocked_by_version:
    SomeFancyPlugin: [47, 340]    # Block 1.8.x and 1.12.x
    AnotherPlugin: [754, 757]     # Block 1.16.5 and 1.17.1
```

In this example:
- Players using Minecraft 1.8.x or 1.12.x cannot use `SomeFancyPlugin`
- Players using Minecraft 1.16.5 or 1.17.1 cannot use `AnotherPlugin`

#### 2. versions

This subsection lists protocol versions and which plugins they cannot use:

```yaml
plugin_interactions:
  versions:
    47: [BadPlugin, LegacyWarp]   # Block these plugins for 1.8.x
    757: [DynamicShop]            # Block these plugins for 1.17.1
```

In this example:
- Players using Minecraft 1.8.x cannot use `BadPlugin` or `LegacyWarp`
- Players using Minecraft 1.17.1 cannot use `DynamicShop`

Both formats work together to provide flexible control over plugin access.

### Messages Section

The `messages` section lets you customize the messages players see when their actions are blocked:

```yaml
messages:
  kick_on_login: "&cYou are still on version {version}, which is not allowed in world \"{world}\"."
  cancel_on_teleport: "&cTeleport blocked: your client version ({version}) cannot enter world \"{world}\"."
  cancel_on_command: "&cCommand blocked: plugin \"{plugin}\" is unavailable for your version ({version})."
  cancel_on_plugin_message: "&cAction blocked: plugin messages for \"{plugin}\" are disabled on your version ({version})."
```

Available placeholders:
- `{version}`: The player's protocol version
- `{world}`: The world name
- `{plugin}`: The plugin name

Color codes using the standard Minecraft color code format (`&` followed by a character) are supported.

## Auto-Detection of Plugins

VersionGate automatically detects all installed plugins when it starts up or reloads. It adds any new plugins to the `blocked_by_version` section with an empty list.

After the auto-detection, you'll need to manually add version restrictions for these plugins if needed.

## Applying Configuration Changes

After editing the configuration file, reload VersionGate using the command:

```
/versiongate reload
```

You don't need to restart your server for changes to take effect.

## Full Configuration Example

Here's a complete example configuration:

```yaml
worlds:
  world_nether:
    blocked_versions: [47, 340]
  world_old:
    blocked_versions: [5, 4]
  resource_world:
    blocked_versions: [4, 5, 47]

plugin_interactions:
  blocked_by_version:
    SomeFancyPlugin: [47, 340]
    AnotherPlugin: [754, 757]
    LegacySupport: [754, 757]
    ModernFeatures: [4, 5, 47]
  versions:
    47: [BadPlugin, LegacyWarp]
    757: [DynamicShop]
    4: [NewFeatures, ModernShop]
    5: [NewFeatures, ModernShop]

messages:
  kick_on_login: "&cYour client version ({version}) is not allowed in world \"{world}\". Please update your client."
  cancel_on_teleport: "&cYou cannot enter world \"{world}\" with your version ({version})."
  cancel_on_command: "&cPlugin \"{plugin}\" is not compatible with your version ({version})."
  cancel_on_plugin_message: "&cThis feature from \"{plugin}\" is not available on your version ({version})."
```

## Next Steps

Now that you understand the configuration, learn about the [Commands](/guide/commands) available to manage VersionGate at runtime. 