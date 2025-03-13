# Homes System

[← Back to Main Documentation](../README.md)

The Homes system in FrizzlenEssentials allows players to set, manage, and teleport to multiple personal home locations throughout the server.

## Features

- **Multiple Homes**: Players can set multiple homes with unique names
- **Permission-Based Limits**: Configurable limits on how many homes a player can have
- **Easy Management**: Simple commands for listing, teleporting to, and deleting homes
- **Persistent Storage**: Home locations are saved and persist through server restarts

## Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/sethome [name]` | Set a home at your current location | `/sethome base` or just `/sethome` for default | `frizzlenessentials.sethome` |
| `/home [name]` | Teleport to one of your homes | `/home base` or just `/home` for default | `frizzlenessentials.home` |
| `/delhome <name>` | Delete one of your homes | `/delhome base` | `frizzlenessentials.delhome` |
| `/listhomes` | List all of your homes | `/listhomes` | `frizzlenessentials.listhomes` |

## Configuration Options

In the `config.yml` file, you can customize the homes system with the following options:

```yaml
homes:
  # Default maximum homes per player
  default-max-homes: 3
  
  # Maximum homes by permission node (permission is frizzlenessentials.homes.<amount>)
  # The highest permission node will be used
  max-homes-by-permission:
    - 5
    - 10
    - 15
    - 20
    
  # Whether player can override an existing home with the same name
  allow-override: true
```

## Permissions

### Basic Permissions
- `frizzlenessentials.sethome` - Allows setting homes
- `frizzlenessentials.home` - Allows teleporting to homes
- `frizzlenessentials.delhome` - Allows deleting homes
- `frizzlenessentials.listhomes` - Allows listing homes

### Home Limit Permissions
- `frizzlenessentials.homes.5` - Allows setting up to 5 homes
- `frizzlenessentials.homes.10` - Allows setting up to 10 homes
- `frizzlenessentials.homes.15` - Allows setting up to 15 homes
- `frizzlenessentials.homes.20` - Allows setting up to 20 homes
- Custom values can be added to the configuration

### Administrative Permissions
- `frizzlenessentials.sethome.unlimited` - Allows setting unlimited homes
- `frizzlenessentials.sethome.others` - Allows managing other players' homes
- `frizzlenessentials.home.others` - Allows teleporting to other players' homes

## Usage Examples

### Setting a Home

1. Travel to the location where you want to set a home
2. Type `/sethome` for a default home or `/sethome <name>` for a named home
3. Example: `/sethome farm` sets a home named "farm" at your current location

### Teleporting to a Home

1. Type `/home` to teleport to your default home
2. Type `/home <name>` to teleport to a specific home
3. Example: `/home farm` teleports you to your "farm" home

### Managing Homes

1. Type `/listhomes` to see a list of all your homes
2. To delete a home, type `/delhome <name>`
3. Example: `/delhome farm` deletes your "farm" home

### Handling Home Limits

1. Players receive a limit based on their permissions or the default limit
2. When they reach their limit, they'll need to delete a home before setting a new one
3. Alternatively, with the `allow-override: true` setting, they can override an existing home with the same name

## Notes

- Home names are case-insensitive ("Farm" and "farm" are considered the same)
- If no name is provided with `/sethome` or `/home`, a default home named "home" is used
- Homes are player-specific and cannot be accessed by other players (unless an admin with appropriate permissions)
- Home data is stored in the `homes.yml` file and is loaded when the server starts
- If a player sets a home in a world that gets deleted, attempting to teleport to that home will result in an error message
- Home teleportation is subject to the same cooldowns and delays as other teleportation commands 