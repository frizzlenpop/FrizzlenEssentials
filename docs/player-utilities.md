# Player Utilities

[← Back to Main Documentation](../README.md)

The Player Utilities in FrizzlenEssentials provide a wide range of helpful commands for players to enhance their gameplay and perform common tasks more efficiently.

## Features

- **Player Status Management**: Commands to modify player health, hunger, flight, and other statuses
- **Movement Enhancements**: Adjust walking and flying speed
- **Item Management**: Tools for manipulating, repairing, and customizing items
- **Visual Effects**: Commands to modify player appearance and effects
- **Quality of Life**: Various utility commands to improve the gameplay experience

## Commands

### Player Status Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/fly [player]` | Toggle flight mode | `/fly` or `/fly Steve` | `frizzlenessentials.fly` |
| `/god [player]` | Toggle god mode (invulnerability) | `/god` or `/god Alex` | `frizzlenessentials.god` |
| `/heal [player]` | Restore health | `/heal` or `/heal Steve` | `frizzlenessentials.heal` |
| `/feed [player]` | Replenish hunger | `/feed` or `/feed Alex` | `frizzlenessentials.feed` |

### Movement Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/speed <1-10> [player] [fly\|walk]` | Set movement speed | `/speed 5 fly` | `frizzlenessentials.speed` |
| `/walkspeed <1-10> [player]` | Set walking speed | `/walkspeed 3` | `frizzlenessentials.walkspeed` |
| `/jumpboost <0-10> [player] [duration]` | Set jump boost level | `/jumpboost 2 60` | `frizzlenessentials.jumpboost` |

### Item Management Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/hat` | Wear the item in your hand as a hat | `/hat` | `frizzlenessentials.hat` |
| `/itemname <name>` | Rename the item in your hand | `/itemname Excalibur` | `frizzlenessentials.itemname` |
| `/itemlore <lore>` | Add lore to the item in your hand | `/itemlore A legendary sword` | `frizzlenessentials.itemlore` |
| `/repair [hand/all]` | Repair items | `/repair hand` or `/repair all` | `frizzlenessentials.repair` |
| `/stack` | Stack similar items in your inventory | `/stack` | `frizzlenessentials.stack` |
| `/more` | Get a full stack of the item in your hand | `/more` | `frizzlenessentials.more` |

## Configuration Options

In the `config.yml` file, you can customize player utilities with the following options:

```yaml
player-utilities:
  # Whether players' godmode status is persistent between server restarts
  persistent-godmode: false
  
  # Whether players' flight mode is persistent between server restarts
  persistent-flight: false
  
  # Maximum walking speed (vanilla default is 0.2)
  max-walk-speed: 1.0
  
  # Maximum flying speed (vanilla default is 0.1)
  max-fly-speed: 1.0
  
  # Maximum jump boost level
  max-jump-boost: 10
```

## Permissions

### Basic Permissions
- `frizzlenessentials.fly` - Allows toggling flight mode
- `frizzlenessentials.god` - Allows toggling god mode
- `frizzlenessentials.heal` - Allows restoring health
- `frizzlenessentials.feed` - Allows replenishing hunger
- `frizzlenessentials.speed` - Allows changing movement speed
- `frizzlenessentials.walkspeed` - Allows changing walking speed
- `frizzlenessentials.jumpboost` - Allows setting jump boost level
- `frizzlenessentials.hat` - Allows wearing items as hats
- `frizzlenessentials.itemname` - Allows renaming items
- `frizzlenessentials.itemlore` - Allows modifying item lore
- `frizzlenessentials.repair` - Allows repairing items
- `frizzlenessentials.stack` - Allows stacking similar items
- `frizzlenessentials.more` - Allows getting full stacks of items

### Administrative Permissions
- `frizzlenessentials.fly.others` - Allows toggling flight mode for other players
- `frizzlenessentials.god.others` - Allows toggling god mode for other players
- `frizzlenessentials.heal.others` - Allows restoring health for other players
- `frizzlenessentials.feed.others` - Allows replenishing hunger for other players
- `frizzlenessentials.speed.others` - Allows changing movement speed for other players
- `frizzlenessentials.walkspeed.others` - Allows changing walking speed for other players
- `frizzlenessentials.jumpboost.others` - Allows setting jump boost level for other players

## Usage Examples

### Player Status Management

1. Enable flight mode:
   - Type `/fly` to toggle flight mode for yourself
   - Type `/fly Steve` to toggle flight mode for another player (with permission)

2. Enable god mode:
   - Type `/god` to toggle invulnerability for yourself
   - Type `/god Alex` to toggle invulnerability for another player (with permission)

3. Restore health and hunger:
   - Type `/heal` to restore your health
   - Type `/feed` to replenish your hunger

### Movement Enhancements

1. Set movement speed:
   - Type `/speed 5 fly` to set your flying speed to level 5
   - Type `/speed 3 walk` to set your walking speed to level 3
   - Type `/walkspeed 4` to set your walking speed to level 4

2. Set jump boost:
   - Type `/jumpboost 2` to set your jump boost level to 2
   - Type `/jumpboost 3 120` to set your jump boost level to 3 for 120 seconds

### Item Management

1. Customize items:
   - Hold an item and type `/hat` to wear it as a hat
   - Hold an item and type `/itemname Excalibur` to rename it
   - Hold an item and type `/itemlore A legendary sword` to add lore to it

2. Item utilities:
   - Type `/repair hand` to repair the item in your hand
   - Type `/repair all` to repair all repairable items in your inventory
   - Type `/stack` to merge similar items in your inventory
   - Hold an item and type `/more` to get a full stack of it

## Notes

- Player status changes (flight, god mode) can be configured to persist between server restarts
- Speed values are scaled for user-friendliness (e.g., a value of 5 is translated to an appropriate internal value)
- The jump boost command applies the vanilla Minecraft jump boost effect
- Item manipulation commands work with most items but may have limitations based on the server's configuration
- The `/repair` command can repair items with durability, including tools, weapons, and armor
- The `/more` command respects the maximum stack size of the item 