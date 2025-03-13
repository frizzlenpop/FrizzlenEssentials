# Administrative Tools

[← Back to Main Documentation](../README.md)

The Administrative Tools in FrizzlenEssentials provide server administrators with powerful commands for server management, player moderation, and technical operations.

## Features

- **Server Management**: Commands for server information and control
- **Player Moderation**: Tools for managing players and their actions
- **Environment Control**: Modify server environment settings
- **Technical Operations**: Access to technical server functions
- **Inventory Management**: View and modify player inventories

## Commands

### Server Information and Control

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/uptime` | Display server uptime | `/uptime` | `frizzlenessentials.uptime` |
| `/gc` | Run garbage collection | `/gc` | `frizzlenessentials.gc` |
| `/debug <player>` | Get debug information about a player | `/debug Steve` | `frizzlenessentials.debug` |
| `/worldinfo` | Display information about the current world | `/worldinfo` | `frizzlenessentials.worldinfo` |
| `/broadcast <message>` | Send a message to all players | `/broadcast Server restart in 5 minutes` | `frizzlenessentials.broadcast` |

### Time and Weather Control

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/daylock` | Lock the time to day | `/daylock` | `frizzlenessentials.daylock` |
| `/nightlock` | Lock the time to night | `/nightlock` | `frizzlenessentials.nightlock` |
| `/ptime <time\|reset> [player]` | Set player time | `/ptime day Steve` | `frizzlenessentials.ptime` |
| `/pweather <clear\|rain\|thunder\|reset> [player]` | Set player weather | `/pweather clear Alex` | `frizzlenessentials.pweather` |

### Player Management

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/sudo <player> <command>` | Force a player to run a command | `/sudo Steve /say Hello` | `frizzlenessentials.sudo` |
| `/kill <player>` | Kill a player | `/kill Alex` | `frizzlenessentials.kill` |
| `/clearinventory <player>` | Clear a player's inventory | `/clearinventory Steve` | `frizzlenessentials.clearinventory` |
| `/vanish [player]` | Toggle invisibility | `/vanish` or `/vanish Alex` | `frizzlenessentials.vanish` |
| `/glow [player] <color>` | Make a player glow with a specified color | `/glow Steve red` | `frizzlenessentials.glow` |

### Inventory Management

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/invsee <player>` | View a player's inventory | `/invsee Steve` | `frizzlenessentials.invsee` |
| `/endersee <player>` | View a player's ender chest | `/endersee Alex` | `frizzlenessentials.endersee` |

## Configuration Options

In the `config.yml` file, you can customize the administrative tools with the following options:

```yaml
administration:
  # Whether to log all administrative commands to console
  log-admin-commands: true
  
  # Whether vanished players can interact with blocks and entities
  vanish-interaction: false
  
  # Whether vanished players can pick up items
  vanish-item-pickup: false
```

## Permissions

### Basic Administrative Permissions
- `frizzlenessentials.uptime` - Allows checking server uptime
- `frizzlenessentials.gc` - Allows running garbage collection
- `frizzlenessentials.debug` - Allows viewing debug information
- `frizzlenessentials.worldinfo` - Allows viewing world information
- `frizzlenessentials.broadcast` - Allows broadcasting messages

### Time and Weather Permissions
- `frizzlenessentials.daylock` - Allows locking time to day
- `frizzlenessentials.nightlock` - Allows locking time to night
- `frizzlenessentials.ptime` - Allows setting player time
- `frizzlenessentials.pweather` - Allows setting player weather

### Player Management Permissions
- `frizzlenessentials.sudo` - Allows forcing players to run commands
- `frizzlenessentials.kill` - Allows killing players
- `frizzlenessentials.clearinventory` - Allows clearing player inventories
- `frizzlenessentials.vanish` - Allows toggling invisibility
- `frizzlenessentials.glow` - Allows making players glow

### Inventory Management Permissions
- `frizzlenessentials.invsee` - Allows viewing player inventories
- `frizzlenessentials.invsee.edit` - Allows modifying player inventories
- `frizzlenessentials.endersee` - Allows viewing player ender chests
- `frizzlenessentials.endersee.edit` - Allows modifying player ender chests

### Special Permissions
- `frizzlenessentials.sudo.exempt` - Prevents being targeted by the sudo command
- `frizzlenessentials.vanish.see` - Allows seeing vanished players

## Usage Examples

### Server Management

1. Check server uptime:
   - Type `/uptime` to see how long the server has been running

2. Run garbage collection:
   - Type `/gc` to manually trigger Java garbage collection
   - This can help free up memory if the server is experiencing lag

3. View world information:
   - Type `/worldinfo` to see details about the current world
   - Information includes world type, seed, difficulty, and more

### Time and Weather Control

1. Control world time:
   - Type `/daylock` to lock the time to day
   - Type `/nightlock` to lock the time to night
   - These commands affect the entire server

2. Set player-specific time and weather:
   - Type `/ptime day` to set your time to day
   - Type `/ptime night Steve` to set Steve's time to night
   - Type `/pweather clear` to set your weather to clear
   - Type `/pweather rain Alex` to set Alex's weather to rain

### Player Management

1. Use sudo to control players:
   - Type `/sudo Steve /say Hello` to make Steve say "Hello"
   - Type `/sudo Alex /me is dancing` to make Alex perform an emote

2. Manage player status:
   - Type `/kill Steve` to kill a player
   - Type `/clearinventory Alex` to clear a player's inventory
   - Type `/vanish` to become invisible to other players
   - Type `/glow red` to make yourself glow red

### Inventory Management

1. View and modify inventories:
   - Type `/invsee Steve` to view Steve's inventory
   - With the appropriate permissions, you can also modify the items
   - Type `/endersee Alex` to view Alex's ender chest contents

## Notes

- Administrative commands are logged to the console if enabled in the configuration
- The `/vanish` command has multiple options to control interaction and visibility
- Player-specific time and weather settings do not affect other players
- The `/sudo` command cannot be used on players with the `frizzlenessentials.sudo.exempt` permission
- Inventory viewing and editing is a powerful tool and should be granted only to trusted administrators
- Server performance commands like `/gc` should be used sparingly 