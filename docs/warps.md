# Warps System

[← Back to Main Documentation](../README.md)

The Warps system in FrizzlenEssentials provides a server-wide location management system with public and private warp points that can be accessed by players based on permissions.

## Features

- **Public and Private Warps**: Create warps that are accessible to everyone or only specific players
- **Access Control**: Fine-grained control over who can access each warp
- **Creator Ownership**: Warp creators maintain control over their warps
- **Warp Information**: View detailed information about warps including location and creator
- **Permissions System**: Comprehensive permissions for managing and using warps

## Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/setwarp <name>` | Create a warp at your current location | `/setwarp shop` | `frizzlenessentials.setwarp` |
| `/warp <name>` | Teleport to a warp | `/warp shop` | `frizzlenessentials.warp` |
| `/delwarp <name>` | Delete a warp | `/delwarp shop` | `frizzlenessentials.delwarp` |
| `/warpinfo <name>` | View information about a warp | `/warpinfo shop` | `frizzlenessentials.warpinfo` |
| `/warpaccess <warp> <add\|remove\|public\|private> [player]` | Modify access permissions for a warp | `/warpaccess shop add Alex` | `frizzlenessentials.warpaccess` |

## Configuration Options

In the `config.yml` file, you can customize the warps system with the following options:

```yaml
warps:
  # If true, warps can be accessed by all players by default
  public-by-default: true
  
  # Whether to show warps that a player doesn't have permission to use in the warp list
  show-inaccessible-warps: false
```

## Permissions

### Basic Permissions
- `frizzlenessentials.warp` - Allows teleporting to warps
- `frizzlenessentials.warpinfo` - Allows viewing information about warps

### Administrative Permissions
- `frizzlenessentials.setwarp` - Allows creating warps
- `frizzlenessentials.delwarp` - Allows deleting warps
- `frizzlenessentials.warpaccess` - Allows modifying warp access
- `frizzlenessentials.warp.others` - Allows access to all warps, even private ones
- `frizzlenessentials.delwarp.others` - Allows deleting warps created by other players

## Usage Examples

### Creating a Warp

1. Travel to the location where you want to create a warp
2. Type `/setwarp <name>` to create a warp at that location
3. Example: `/setwarp shop` creates a warp named "shop" at your current location

### Using a Warp

1. Type `/warp <name>` to teleport to a warp
2. Example: `/warp shop` teleports you to the "shop" warp

### Managing Warp Access

1. By default, warps are either public or private based on the `public-by-default` setting
2. To change a warp's access:
   - `/warpaccess shop public` makes the warp accessible to everyone
   - `/warpaccess shop private` makes the warp private
   - `/warpaccess shop add Alex` adds Alex to the warp's access list
   - `/warpaccess shop remove Alex` removes Alex from the warp's access list

### Viewing Warp Information

1. Type `/warpinfo <name>` to view information about a warp
2. Example: `/warpinfo shop` shows details about the "shop" warp including its location, creator, and access settings

## Notes

- Warp names are case-insensitive ("Shop" and "shop" are considered the same)
- Warp creators automatically have access to their own warps
- Warps can be managed by their creators or by administrators with appropriate permissions
- Warp data is stored in the `warps.yml` file and is loaded when the server starts
- If a warp is set in a world that gets deleted, attempting to teleport to that warp will result in an error message
- Warp teleportation is subject to the same cooldowns and delays as other teleportation commands
- Private warps will only be visible to players who have explicit access or appropriate permissions 