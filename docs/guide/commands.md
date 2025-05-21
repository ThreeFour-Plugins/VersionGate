# Commands

VersionGate provides a set of commands to manage the plugin at runtime. This page documents all available commands, their usage, and required permissions.

## Command Syntax

All VersionGate commands follow the syntax:

```
/versiongate <subcommand> [arguments]
```

You can also use the alias `/vg` instead of `/versiongate`.

## Available Commands

### /versiongate reload

Reloads the configuration from disk and applies changes immediately.

**Usage:**
```
/versiongate reload
```

**Permission:** `versiongate.reload`

**Description:**
- Reloads the `config.yml` file from disk
- Re-scans all installed plugins and adds any new ones to the configuration
- Applies all changes without requiring a server restart
- Confirms with a success message

**Example:**
```
/versiongate reload
```
Output: `VersionGate configuration reloaded successfully.`

### /versiongate info

Displays information about the VersionGate plugin and its dependencies.

**Usage:**
```
/versiongate info
```

**Permission:** `versiongate.info`

**Description:**
Displays:
- Plugin version
- Authors
- ViaVersion status (available or not)

**Example:**
```
/versiongate info
```
Output:
```
=== VersionGate Information ===
Version: 1.0
Author: threefour
ViaVersion: true
```

### /versiongate help

Shows a list of available commands and brief descriptions.

**Usage:**
```
/versiongate help
```
or simply:
```
/versiongate
```

**Permission:** `versiongate.command`

**Description:**
Displays a list of all available commands and their basic usage.

**Example:**
```
/versiongate help
```
Output:
```
=== VersionGate Commands ===
/versiongate reload - Reload configuration
/versiongate info - Show plugin information
```

## Command Permissions

VersionGate uses a permission system to control access to commands:

| Permission | Description | Default |
|------------|-------------|---------|
| `versiongate.command` | Base permission for all commands | All players |
| `versiongate.reload` | Permission to reload the plugin configuration | Operators |
| `versiongate.info` | Permission to view plugin information | All players |

You can adjust these permissions using a permissions plugin like LuckPerms.

## Command Aliases

For convenience, VersionGate provides command aliases:

- `/vg` is an alias for `/versiongate`

This means you can use:
- `/vg reload` instead of `/versiongate reload`
- `/vg info` instead of `/versiongate info`

## Tab Completion

VersionGate supports tab completion for all commands. Press Tab while typing a command to see available options and autocomplete them.

For example:
1. Type `/versiongate` and press Tab
2. Available subcommands (`reload`, `info`) will be suggested

## Using Commands in Console

All VersionGate commands can be used from the server console as well as in-game. When using commands from console, omit the leading slash:

```
versiongate reload
```

This is especially useful for server administrators who need to reload the configuration after making changes without joining the game. 