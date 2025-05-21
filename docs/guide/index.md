# Introduction to VersionGate

VersionGate is a powerful Minecraft plugin designed to help server administrators manage client version compatibility across their server. By leveraging the ViaVersion API, VersionGate allows you to:

- **Restrict access to specific worlds** based on client protocol version
- **Control plugin interactions** for different client versions
- **Customize messages** displayed to players when restrictions are enforced

## How It Works

VersionGate uses ViaVersion to detect each player's client protocol version. When a player tries to:

1. **Log in to the server**: Their version is checked against the world they're spawning in
2. **Teleport between worlds**: Their version is checked against the destination world
3. **Use a command**: The command is analyzed to determine which plugin it belongs to
4. **Receive plugin messages**: The message channel is checked to determine the source plugin

If any of these actions would violate version restrictions defined in your configuration, the action is blocked and a customizable message is sent to the player.

## Key Concepts

### Protocol Versions

Minecraft uses protocol version numbers to identify different client versions. For example:

- Protocol 4: Minecraft 1.7.1
- Protocol 5: Minecraft 1.7.2
- Protocol 47: Minecraft 1.8.x
- Protocol 340: Minecraft 1.12.x
- Protocol 754: Minecraft 1.16.5
- Protocol 757: Minecraft 1.17.1

ViaVersion handles the translation between these protocols, and VersionGate uses them to define restrictions.

### Configuration Structure

VersionGate's configuration is divided into three main sections:

1. **`worlds`**: Defines which protocol versions are blocked from accessing specific worlds
2. **`plugin_interactions`**: Controls which plugins can be used by which protocol versions
3. **`messages`**: Customizable messages displayed when restrictions are enforced

See the [Configuration](/guide/configuration) page for details on each section.

## Next Steps

- [Installation](/guide/installation): Learn how to install and set up VersionGate
- [Configuration](/guide/configuration): Understand the configuration options
- [Commands](/guide/commands): Explore the available commands 