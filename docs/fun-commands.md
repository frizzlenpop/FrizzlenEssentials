# Fun Commands

[← Back to Main Documentation](../README.md)

The Fun Commands in FrizzlenEssentials provide entertaining and interactive features for players to enhance their social experience on the server.

## Features

- **Player Animations**: Various animations and poses for players
- **Visual Effects**: Optional particle effects and sounds to accompany animations
- **Social Interaction**: Commands that encourage player interaction and roleplay
- **Customizable Settings**: Configure visual and sound effects through the configuration

## Commands

| Command | Description | Usage | Permission |
|---------|-------------|-------|------------|
| `/sit` | Sit on the ground | `/sit` | `frizzlenessentials.sit` |
| `/lay` | Lay down on the ground | `/lay` | `frizzlenessentials.lay` |
| `/crawl` | Crawl on the ground | `/crawl` | `frizzlenessentials.crawl` |
| `/spin` | Spin in place | `/spin` | `frizzlenessentials.spin` |

## Configuration Options

In the `config.yml` file, you can customize the fun commands with the following options:

```yaml
fun-commands:
  # Whether to show particle effects with animations
  show-particles: true
  
  # Whether to play sounds with animations
  play-sounds: true
```

## Permissions

### Basic Permissions
- `frizzlenessentials.sit` - Allows using the sit command
- `frizzlenessentials.lay` - Allows using the lay command
- `frizzlenessentials.crawl` - Allows using the crawl command
- `frizzlenessentials.spin` - Allows using the spin command

## Usage Examples

### Sitting

1. Find a flat surface where you want to sit
2. Type `/sit` to sit down on the ground
3. Move in any direction or jump to stand up

### Laying Down

1. Find a suitable location where you want to lay down
2. Type `/lay` to lay down on the ground
3. Move in any direction or jump to stand up

### Crawling

1. Type `/crawl` to start crawling on the ground
2. You can move around while crawling
3. Jump to stop crawling and stand up

### Spinning

1. Type `/spin` to start spinning in place
2. The spinning animation will continue until you move or jump

## Technical Implementation

These commands use a combination of techniques to create the animations:

- **Sitting**: Uses an invisible armor stand entity that the player sits on
- **Laying**: Changes the player's pose to simulate laying down
- **Crawling**: Simulates the swimming animation while on land
- **Spinning**: Rapidly rotates the player's view to create a spinning effect

## Notes

- These commands are purely cosmetic and do not affect gameplay mechanics
- Some animations may be interrupted by certain actions or events
- The visual effects can be disabled in the configuration if desired
- These commands work best in open areas where there's enough space for the animations
- Players can only use one animation at a time
- Moving, jumping, teleporting, or taking damage will generally cancel the animation 