# YRRAK

YRRAK is a small playable Java console game prototype built as a portfolio project.

It is an element-based card battle game where the player defeats three enemies in a row by managing energy, block, card choices, deck cycling, and elemental advantages.

## Project Purpose

This project is designed as a college portfolio piece, not a commercial game.

The main goals are:

- Build a complete small game from the ground up.
- Practice Java object-oriented programming through real gameplay systems.
- Show clear engineering structure instead of putting everything in `Main.java`.
- Create a project that can later grow into a Java Swing GUI version.
- Keep development notes that show learning, iteration, and design decisions.

## Gameplay Summary

The player fights three enemies:

1. Training Dummy
2. Fire Spirit
3. Thunder Beast

Each player turn:

- Energy is restored.
- Cards are drawn into the hand.
- The player chooses cards to play.
- Cards can deal damage, generate block, or both.

Each enemy turn:

- The enemy performs its shown intent.
- Block absorbs damage before HP is reduced.

The player wins by defeating all three enemies.

## Element System

The game currently has three main elements:

- Fire
- Water
- Thunder

Element advantage:

- Fire beats Thunder.
- Thunder beats Water.
- Water beats Fire.

When a card has element advantage against an enemy, its damage is increased.

## Current Features

- Playable console MVP.
- Turn-based player and enemy flow.
- Player HP, max HP, energy, and block.
- Enemy HP, element, intent, and behavior patterns.
- Element advantage damage calculation.
- Cards can deal damage, generate block, and heal the player.
- Draw pile, hand, and discard pile.
- Cards move to the discard pile after being played.
- Discard pile recycles into the draw pile.
- Reward card choices after defeated enemies.
- Victory and game-over conditions.
- Clear console sections for battles, turns, rewards, victory, and defeat.
- In-game rule explanation at startup.
- Final run summary with enemies defeated, cards played, rewards chosen, and final HP.
- Input/output abstraction through `GameIO` for future GUI support.
- A small Java Swing GUI prototype with three enemies, reward choices, and battle logs.

## Enemy Behavior

- `Training Dummy`: always uses a normal attack.
- `Fire Spirit`: uses a stronger Flame Burst every third enemy turn.
- `Thunder Beast`: alternates between quick attacks and Charged Strike.

## Code Structure

```text
src/
├── Main.java
├── battle/
│   └── BattleManager.java
├── model/
│   ├── Card.java
│   ├── Element.java
│   ├── Enemy.java
│   └── Player.java
├── gui/
│   └── GuiMain.java
└── util/
    ├── ConsoleGameIO.java
    └── GameIO.java
```

Class responsibilities:

- `Main`: starts the game.
- `battle.BattleManager`: controls battle flow, turns, rewards, and deck movement.
- `model.Player`: stores player state and damage/block logic.
- `model.Enemy`: stores enemy state, element, intent, and behavior pattern.
- `model.Card`: stores card stats and calculates element advantage damage.
- `model.Element`: defines elements and advantage rules.
- `gui.GuiMain`: runs a small Java Swing battle prototype.
- `util.GameIO`: separates game logic from input/output.
- `util.ConsoleGameIO`: console implementation of `GameIO`.

## How to Run

From the project root:

```bash
./scripts/run.sh
```

To run the prepared demo path:

```bash
./scripts/demo.sh
```

To run the GUI prototype:

```bash
./scripts/gui.sh
```

Manual compile and run commands:

```bash
javac -encoding UTF-8 -d out/production/YRRAK $(find src -name "*.java")
java -cp out/production/YRRAK Main
```

## Controls

During the player turn:

- Enter a card number to play that card.
- Enter `0` to end the turn.

## Example Output

```text
----------------------------------------
Battle 1 of 3
----------------------------------------
A new enemy appears: Training Dummy [None]

Player HP: 30/30 | Block: 0 | Energy: 3/3
Enemy HP: 18/18 | Enemy: Training Dummy | Element: None
Enemy intent: Attack for 4 damage
```

For a full sample run, see `docs/demo-output.txt`.

## Future Plans

- Add more card types.
- Add more enemy actions.
- Add screenshots or a short gameplay recording.
- Clean up the repository before uploading to GitHub.
- Build a Java Swing GUI after the console version is stable.
- Consider double-sided cards as a special future card type.

## Development Notes

See `DEVLOG.md` for the development log and design notes.
