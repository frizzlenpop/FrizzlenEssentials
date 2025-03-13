# Spawn Management

[← Back to Main Documentation](../README.md)

The Spawn Management system in FrizzlenEssentials allows server administrators to set and manage the server spawn point, and provides commands for players to teleport to spawn.

## Features

- **Server Spawn**: Set a custom spawn point for the server
- **Spawn Teleportation**: Easy command for players to teleport to spawn
- **Join and Respawn Options**: Configure whether players should teleport to spawn on join or respawn
- **Persistent Storage**: Spawn location is saved and persists through server restarts

## Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/setspawn` | Set the server spawn location at your current position | `/setspawn` | `frizzlenessentials.setspawn` |
| `/spawn` | Teleport to the server spawn location | `/spawn` | `frizzlenessentials.spawn` |

## Configuration Options

In the `config.yml` file, you can customize the spawn system with the following options:

```yaml
spawn:
  # Whether to teleport players to spawn on join
  teleport-on-join: false
  
  # Whether to teleport players to spawn on respawn
  teleport-on-respawn: true
```

## Permissions

### Basic Permissions
- `frizzlenessentials.spawn` - Allows teleporting to spawn

### Administrative Permissions
- `frizzlenessentials.setspawn` - Allows setting the server spawn location
- `frizzlenessentials.spawn.others` - Allows teleporting other players to spawn

## Usage Examples

### Setting the Server Spawn

1. Travel to the location where you want to set the server spawn
2. Type `/setspawn` to set the spawn point at your current location
3. The spawn point will be saved to the `spawn.yml` file

### Teleporting to Spawn

1. Type `/spawn` to teleport to the server spawn location
2. Administrators can forcibly teleport other players to spawn with the appropriate permission

## Technical Details

The spawn location is stored in the `spawn.yml` file with the following information:
- World name
- X, Y, Z coordinates
- Pitch and yaw (direction)

When a player joins the server, the plugin checks the `teleport-on-join` setting to determine if they should be teleported to spawn automatically.

Similarly, when a player respawns after death, the plugin checks the `teleport-on-respawn` setting to determine if they should be teleported to spawn instead of their bed or default world spawn.

## Notes

- The `/setspawn` command also sets the world's default spawn point, affecting where new players spawn
- Setting a spawn point in a world that gets deleted will result in using the default world spawn instead
- Spawn teleportation is subject to the same cooldowns and delays as other teleportation commands
- If no custom spawn point has been set, the `/spawn` command will teleport players to the default world spawn
- The spawn location is saved immediately when set with the `/setspawn` command 