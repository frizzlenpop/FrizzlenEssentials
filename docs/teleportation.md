# Teleportation System

[← Back to Main Documentation](../README.md)

The teleportation system in FrizzlenEssentials provides a comprehensive set of commands for moving players around the server with features like teleport requests, cooldowns, and location history.

## Features

- **Direct Teleportation**: Instantly teleport to other players or coordinates
- **Teleport Requests**: Send and receive teleport requests for permission-based teleportation
- **Location History**: Return to previous locations with the `/back` command
- **Cooldowns and Delays**: Configurable cooldowns and delays for balanced gameplay
- **World Teleportation**: Teleport between different worlds
- **Death Location**: Return to your last death location
- **Highest Block**: Teleport to the highest block at your current XZ coordinates
- **Upward Teleportation**: Go up a specified number of blocks

## Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/tp <player>` | Teleport to another player | `/tp Steve` | `frizzlenessentials.tp` |
| `/tpa <player>` | Send a teleport request | `/tpa Alex` | `frizzlenessentials.tpa` |
| `/tpahere <player>` | Request a player to teleport to you | `/tpahere Alex` | `frizzlenessentials.tpahere` |
| `/tpaccept` | Accept a teleport request | `/tpaccept` | `frizzlenessentials.tpaccept` |
| `/tpdeny` | Deny a teleport request | `/tpdeny` | `frizzlenessentials.tpdeny` |
| `/tphere <player>` | Teleport a player to you | `/tphere Steve` | `frizzlenessentials.tphere` |
| `/tpall` | Teleport all players to you | `/tpall` | `frizzlenessentials.tpall` |
| `/lastdeath` | Teleport to your last death location | `/lastdeath` | `frizzlenessentials.lastdeath` |
| `/tpworld <world>` | Teleport to a specific world | `/tpworld world_nether` | `frizzlenessentials.tpworld` |
| `/back` | Return to your previous location | `/back` | `frizzlenessentials.back` |
| `/top` | Teleport to the highest block at your current position | `/top` | `frizzlenessentials.top` |
| `/up [distance]` | Teleport upward by a specified distance (default: 10) and place a glass block beneath you | `/up 20` | `frizzlenessentials.up` |

## Configuration Options

In the `config.yml` file, you can customize the teleportation system with the following options:

```yaml
teleportation:
  # Cooldown in seconds between teleportations
  cooldown: 3
  
  # Delay in seconds before teleportation (set to 0 for instant teleportation)
  delay: 3
  
  # Whether to cancel teleportation when the player moves during the delay
  cancel-on-move: true
  
  # Maximum time in seconds a teleport request stays active
  request-timeout: 60
  
  # Whether to save player's last location when they teleport
  save-last-location: true
  
  # Maximum back locations to save per player
  max-back-locations: 5
```

## Permissions

### Basic Permissions
- `frizzlenessentials.tp` - Allows teleporting to other players
- `frizzlenessentials.tpa` - Allows sending teleport requests
- `frizzlenessentials.tpahere` - Allows sending teleport here requests
- `frizzlenessentials.tpaccept` - Allows accepting teleport requests
- `frizzlenessentials.tpdeny` - Allows denying teleport requests
- `frizzlenessentials.back` - Allows returning to previous locations
- `frizzlenessentials.lastdeath` - Allows teleporting to last death location
- `frizzlenessentials.tpworld` - Allows teleporting between worlds
- `frizzlenessentials.top` - Allows teleporting to the highest block
- `frizzlenessentials.up` - Allows teleporting upward

### Administrative Permissions
- `frizzlenessentials.tphere` - Allows teleporting other players to you
- `frizzlenessentials.tpall` - Allows teleporting all players to you
- `frizzlenessentials.tp.others` - Allows teleporting other players to other players
- `frizzlenessentials.up.unlimited` - Allows teleporting upward with no distance limit

## Usage Examples

### Teleport Requests

1. Player Alex wants to teleport to Player Steve:
   - Alex types: `/tpa Steve`
   - Steve receives a message asking to accept or deny
   - If Steve types `/tpaccept`, Alex is teleported to Steve
   - If Steve types `/tpdeny` or doesn't respond within the timeout period, the request is canceled

### Returning to Previous Locations

1. Player uses any teleportation command to move to a new location
2. The player can use `/back` to return to their previous location
3. This is particularly useful after death, allowing players to quickly return to where they were

### Using the Top Command

The `/top` command is useful for quickly getting to the surface when underground:
1. Player is deep underground while mining
2. Player types `/top`
3. Player is instantly teleported to the highest block at their current X,Z coordinates

### Using the Up Command

The `/up` command allows players to quickly build platforms or reach higher locations:
1. Player is building a structure and needs to work on a higher level
2. Player types `/up 5`
3. Player is teleported 5 blocks upward and a glass block is placed beneath them

## Notes

- Teleport requests expire after the configured timeout period
- Players can only have one active teleport request at a time
- Using `/back` multiple times lets you traverse through your location history
- The `/top` command will only teleport you upwards, never downwards
- The `/up` command will place a glass block beneath you, ensuring you don't fall 