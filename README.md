# YRRAK

YRRAK is an original card roguelike prototype built in Java.

The current version focuses on a small turn-based combat system. The long-term goal is to grow it into a complete playable game with original card mechanics, enemy behavior, progression, and a visual interface.

## Project Goals

- Build a card roguelike from the ground up in Java.
- Practice object-oriented programming through real gameplay systems.
- Design original mechanics instead of only copying existing card games.
- Keep a clear development record for a future portfolio or college application.

## Current Features

- Player health and energy.
- Enemy health and attack damage.
- Cards with damage, block, and energy cost.
- Energy checks before playing a card.
- Block absorbs damage before health is reduced.
- Player and enemy defeat checks.
- Simple turn simulation in the console.

## Original Design Direction

The project may include special double-sided cards as one card type.

Double-sided cards are not meant to replace all cards. They are designed to create future consequences: using a powerful front side can flip the card into a weaker, defensive, or risky back side.

## Current Classes

- `Main`: Runs the current combat test.
- `Player`: Stores player health, energy, block, and survival logic.
- `Enemy`: Stores enemy health, attack damage, and survival logic.
- `Card`: Stores card name, damage, block, and energy cost.
- `Element`: Reserved for a future element system.

## Next Steps

- Add `maxEnergy` to the player.
- Create a real turn structure.
- Add a hand, deck, discard pile, and draw pile.
- Add more card types.
- Add multiple enemies and target selection.
- Build a visual version after the console prototype is stable.

## How to Run

Compile and run from the project root:

```bash
javac -encoding UTF-8 -d out/production/YRRAK src/*.java
java -cp out/production/YRRAK Main
```
