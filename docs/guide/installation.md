# Installation

This guide will walk you through the process of installing and setting up VersionGate on your Minecraft server.

## Prerequisites

Before installing VersionGate, make sure you have:

- A Spigot, Paper, or compatible Minecraft server (1.13+)
- [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/) plugin installed
- Server operator permissions

## Installation Steps

1. **Download VersionGate**
   
   Download the latest version of VersionGate from the [Download](/download) page or [GitHub Releases](https://github.com/threefour/VersionGate/releases/latest).

2. **Install the Plugin**
   
   Place the downloaded `.jar` file in your server's `plugins` folder.

3. **Start or Restart Your Server**
   
   If your server is already running, use the reload command or restart it:
   
   ```
   /reload
   ```
   
   ::: warning
   Using `/reload` may cause issues on some servers. A full restart is recommended.
   :::

4. **Verify Installation**
   
   After the server starts, you should see messages in the console indicating that VersionGate has been enabled. You can also run the following command to verify:
   
   ```
   /versiongate info
   ```

5. **Configure the Plugin**
   
   VersionGate creates a default configuration file at `plugins/VersionGate/config.yml`. Edit this file to set up your world and plugin restrictions.

## Configuration Overview

After installation, VersionGate will automatically detect all installed plugins and add them to the configuration with empty restriction lists. You'll then need to:

1. Define which client versions are blocked from accessing specific worlds
2. Define which plugins should be unavailable to specific client versions
3. Customize the messages shown to players

For detailed configuration instructions, see the [Configuration](/guide/configuration) page.

## Troubleshooting

### ViaVersion Dependency

If you see an error message indicating that ViaVersion is required:

1. Make sure ViaVersion is installed in your plugins folder
2. Ensure ViaVersion loads before VersionGate (should happen automatically)
3. Check that ViaVersion is properly configured and functioning

### Permission Issues

If you're having trouble using commands:

- Make sure you have the necessary permissions:
  - `versiongate.command` for basic access
  - `versiongate.reload` for reloading the configuration
  - `versiongate.info` for viewing plugin information

### Configuration Not Applying

If your configuration changes don't seem to be taking effect:

1. Make sure your YAML syntax is correct
2. Use `/versiongate reload` to reload the configuration
3. Check the server logs for any error messages

## Next Steps

Once VersionGate is installed and running, you should:

1. Review and customize the [Configuration](/guide/configuration)
2. Learn about the available [Commands](/guide/commands)
3. Test your setup with players using different Minecraft versions 