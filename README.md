# FrizzlenEssentials

A comprehensive essentials plugin for Minecraft Paper servers designed to provide a wide range of administrative, utility, and quality-of-life features.

## Overview

FrizzlenEssentials is a feature-rich plugin for Paper Minecraft servers that aims to provide server administrators with essential tools for managing their server while also offering players convenient commands to enhance their gameplay experience. The plugin has been designed with flexibility and customization in mind, allowing server owners to tailor the functionality to their specific needs.

## Features

FrizzlenEssentials includes a wide variety of features organized into the following categories:

- [**Teleportation System**](docs/teleportation.md) - A comprehensive teleportation system with teleport requests, cooldowns, and history
- [**Homes System**](docs/homes.md) - Allows players to set, teleport to, and manage multiple homes
- [**Warps System**](docs/warps.md) - Public and private warp points with access control
- [**Spawn Management**](docs/spawn.md) - Server spawn point management and player teleportation
- [**Player Utilities**](docs/player-utilities.md) - Various player-focused utility commands
- [**Fun Commands**](docs/fun-commands.md) - Commands that add fun interactions and animations
- [**Administrative Tools**](docs/admin-tools.md) - Server administration and management commands

## Installation

1. Download the latest FrizzlenEssentials.jar from the [releases page](https://github.com/frizzlenpop/frizzlenessentials/releases)
2. Place the jar file in your server's `plugins` folder
3. Restart your server
4. Edit the configuration files in the `plugins/FrizzlenEssentials` folder to customize the plugin to your needs

## Configuration

FrizzlenEssentials stores its configuration in the following files:

- `config.yml` - Main configuration file
- `homes.yml` - Home data
- `warps.yml` - Warp data
- `spawn.yml` - Spawn data
- `locations.yml` - Location data (for teleportation)
- `players.yml` - Player-specific data

The `config.yml` file contains settings for all aspects of the plugin, including:

- Storage configuration
- Teleportation settings (cooldowns, delays, etc.)
- Home settings (limits, permissions)
- Warp settings
- Spawn settings
- Player utility settings
- Administrative settings
- Custom messages and colors

## Permissions

FrizzlenEssentials uses a permission-based system to control access to commands and features. The plugin includes the following permission groups:

- `frizzlenessentials.*` - Grants access to all commands and features
- `frizzlenessentials.admin` - Grants access to administrative commands
- `frizzlenessentials.user` - Grants access to basic user commands

Individual permissions for each command follow the format `frizzlenessentials.<command>`. For more detailed information, see the feature-specific documentation.

## Commands

For a full list of commands, see the feature-specific documentation linked in the [Features](#features) section.

## Support

If you encounter any issues or have questions about FrizzlenEssentials, please [create an issue](https://github.com/frizzlenpop/frizzlenessentials/issues) on the GitHub repository.

## License

FrizzlenEssentials is released under the [MIT License](LICENSE). 