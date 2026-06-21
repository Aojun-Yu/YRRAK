# YRRAK

YRRAK is a small Java card roguelike prototype built as a portfolio project.

The game focuses on turn-based card combat, elemental advantage, energy management, enemy intent, deck cycling, reward cards, and a simple Swing GUI. The goal is not to build a large commercial game yet, but to create a complete and readable Java project that can be shown in a college application portfolio.

## Project Goal

This project demonstrates:

- Object-oriented Java programming
- Turn-based battle system design
- Card effects such as damage, block, healing, and energy cost
- Element advantage rules
- Deck, hand, and discard pile management
- Separation between game logic and user interface
- A basic Java Swing desktop GUI

## Gameplay

The player fights three enemies in sequence:

- Training Dummy
- Fire Spirit
- Thunder Beast

Cards use one of three elements:

- Fire beats Thunder
- Thunder beats Water
- Water beats Fire

When a card has elemental advantage against an enemy, its damage is increased.

Each turn, the player:

1. Draws cards.
2. Gets Energy restored.
3. Plays cards to attack, block, or heal.
4. Ends the turn.
5. The enemy attacks based on its visible intent.

Defeat all three enemies to win.

## Project Structure

```text
src/
├── Main.java
├── battle/
│   ├── BattleActions.java
│   ├── BattleManager.java
│   ├── CardPlayResult.java
│   ├── DeckManager.java
│   ├── GameContent.java
│   └── GameState.java
├── gui/
│   └── GuiMain.java
├── model/
│   ├── Card.java
│   ├── Element.java
│   ├── Enemy.java
│   └── Player.java
└── util/
    ├── ConsoleGameIO.java
    └── GameIO.java
```

```text
assets/
├── icons/
├── characters/
└── ui/
```

The game can run without image files. If an image is missing, the GUI shows a generated placeholder.

## How To Run

Run the console version:

```bash
./scripts/run.sh
```

Run the Swing GUI version:

```bash
./scripts/gui.sh
```

Run the prepared console demo:

```bash
./scripts/demo.sh
```

## IntelliJ Run Settings

For the GUI version:

```text
Main class: gui.GuiMain
Module: YRRAK
```

For the console version:

```text
Main class: Main
Module: YRRAK
```

## Current Status

Implemented:

- Playable console MVP
- Java Swing GUI prototype
- Lightweight generated sound effects and looping background music
- Elemental combat
- Energy-based card play
- Enemy intent system
- Deck, hand, and discard pile
- Reward card choices
- Victory and game-over conditions
- Asset folder structure and placeholder image support
- Generated placeholder art for element icons, character portraits, background, and card frame

Still planned:

- Original card and character art
- Screenshots for the README
- Short gameplay demo video
- Better GUI layout and visual polish
- More playtesting and balance tuning

## Portfolio Notes

YRRAK is designed to show consistent iteration rather than one large final product. The most important parts of the project are the clean Java structure, the playable game loop, and the ability to expand from console gameplay into a GUI-based desktop game.

## Portfolio Materials

Additional application and presentation materials are included in the `docs/` folder:

- `docs/portfolio-summary.md`: English project summary for a portfolio page.
- `docs/application-reflection.md`: design reflection, challenges, limitations, and interview talking points.
- `docs/recording-guide.md`: 45-60 second demo video script and screenshot plan.
- `docs/art-direction.md`: visual direction and asset plan.
- `docs/how-to-run.md`: detailed running instructions.

These documents are intended to help present the project clearly for a university application, GitHub portfolio, or project interview.
