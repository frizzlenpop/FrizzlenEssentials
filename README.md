# FrizzlenEssentials

A comprehensive essentials plugin for Minecraft Paper servers.

## Features

- Teleportation system with requests and cooldowns
- Home system with multiple homes per player
- Warp system with public and private warps
- Spawn management
- Player utility commands
- Fun interaction commands
- Administrative tools
- Configurable messages and permissions

## Commands

### Teleportation Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/tp <player>` | Teleport to another player | `frizzlenessentials.tp` |
| `/tpa <player>` | Send a teleport request | `frizzlenessentials.tpa` |
| `/tpahere <player>` | Request a player to teleport to you | `frizzlenessentials.tpahere` |
| `/tpaccept` | Accept a teleport request | `frizzlenessentials.tpaccept` |
| `/tpdeny` | Deny a teleport request | `frizzlenessentials.tpdeny` |
| `/tphere <player>` | Teleport a player to you | `frizzlenessentials.tphere` |
| `/tpall` | Teleport all players to you | `frizzlenessentials.tpall` |
| `/lastdeath` | Teleport to your last death location | `frizzlenessentials.lastdeath` |
| `/tpworld <world>` | Teleport to a specific world | `frizzlenessentials.tpworld` |
| `/back` | Return to your previous location | `frizzlenessentials.back` |

### Home Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/sethome [name]` | Set a home at your location | `frizzlenessentials.sethome` |
| `/home [name]` | Teleport to your home | `frizzlenessentials.home` |
| `/delhome <name>` | Delete a home | `frizzlenessentials.delhome` |
| `/listhomes` | List all your homes | `frizzlenessentials.listhomes` |

### Warp Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/setwarp <name>` | Create a warp at your location | `frizzlenessentials.setwarp` |
| `/warp <name>` | Teleport to a warp | `frizzlenessentials.warp` |
| `/delwarp <name>` | Delete a warp | `frizzlenessentials.delwarp` |
| `/warpinfo <name>` | View information about a warp | `frizzlenessentials.warpinfo` |
| `/warpaccess <warp> <add|remove|public|private> [player]` | Modify warp access | `frizzlenessentials.warpaccess` |

### Spawn Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/setspawn` | Set the server spawn location | `frizzlenessentials.setspawn` |
| `/spawn` | Teleport to spawn | `frizzlenessentials.spawn` |

### Utility Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/fly [player]` | Toggle flight mode | `frizzlenessentials.fly` |
| `/god [player]` | Toggle god mode | `frizzlenessentials.god` |
| `/heal [player]` | Restore health and hunger | `frizzlenessentials.heal` |
| `/feed [player]` | Restore hunger | `frizzlenessentials.feed` |
| `/speed <1-10> [player] [fly|walk]` | Set movement speed | `frizzlenessentials.speed` |
| `/walkspeed <1-10> [player]` | Set walking speed | `frizzlenessentials.walkspeed` |
| `/jumpboost <0-10> [player] [duration]` | Set jump boost level | `frizzlenessentials.jumpboost` |
| `/hat` | Wear item as hat | `frizzlenessentials.hat` |
| `/itemname <name>` | Rename held item | `frizzlenessentials.itemname` |
| `/itemlore <add|set|clear> [line] [text]` | Modify item lore | `frizzlenessentials.itemlore` |
| `/repair [all]` | Repair items | `frizzlenessentials.repair` |
| `/stack` | Stack similar items | `frizzlenessentials.stack` |
| `/more` | Fill item stack | `frizzlenessentials.more` |

### Fun Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/sit` | Sit on the ground | `frizzlenessentials.sit` |
| `/lay` | Lay on the ground | `frizzlenessentials.lay` |
| `/crawl` | Crawl on the ground | `frizzlenessentials.crawl` |
| `/spin` | Spin in place | `frizzlenessentials.spin` |

### Administrative Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/uptime` | Display server uptime | `frizzlenessentials.uptime` |
| `/gc` | Run garbage collection | `frizzlenessentials.gc` |
| `/debug [info]` | Toggle debug mode | `frizzlenessentials.debug` |
| `/daylock` | Lock time to day | `frizzlenessentials.daylock` |
| `/nightlock` | Lock time to night | `frizzlenessentials.nightlock` |
| `/ptime <time|reset> [player]` | Set player time | `frizzlenessentials.ptime` |
| `/pweather <clear|rain|reset> [player]` | Set player weather | `frizzlenessentials.pweather` |
| `/worldinfo` | Display world information | `frizzlenessentials.worldinfo` |
| `/sudo <player> <command>` | Force player to run command | `frizzlenessentials.sudo` |
| `/invsee <player>` | View player's inventory | `frizzlenessentials.invsee` |
| `/endersee <player>` | View player's ender chest | `frizzlenessentials.endersee` |
| `/broadcast <message>` | Broadcast a message | `frizzlenessentials.broadcast` |
| `/kill <player>` | Kill a player | `frizzlenessentials.kill` |
| `/clearinventory <player>` | Clear player's inventory | `frizzlenessentials.clearinventory` |
| `/vanish [player]` | Toggle invisibility | `frizzlenessentials.vanish` |
| `/glow [player] <color>` | Make player glow | `frizzlenessentials.glow` |

## Permission Groups

### Administrator Permissions
- `frizzlenessentials.admin` - Grants access to all administrative commands
  - Includes all administrative command permissions
  - Access to modify warps and spawn points
  - Ability to manage other players

### User Permissions
- `frizzlenessentials.user` - Grants access to basic user commands
  - Basic teleportation commands
  - Home management
  - Basic warp usage
  - Fun commands

### Special Permissions
- `frizzlenessentials.sudo.exempt` - Prevents being targeted by sudo command
- `frizzlenessentials.vanish.see` - Ability to see vanished players
- `frizzlenessentials.*.others` - Allows using commands on other players (e.g., `frizzlenessentials.fly.others`)

## Configuration

The plugin uses a main configuration file (`config.yml`) that includes:
- Message customization
- Color schemes
- Command cooldowns
- Feature toggles

### Message Format
Messages use color codes and placeholders:
- `{PRIMARY}`, `{SECONDARY}`, `{HIGHLIGHT}` - Color scheme placeholders
- `{player}`, `{world}`, etc. - Dynamic content placeholders
- Standard Minecraft color codes (e.g., &a, &b) 