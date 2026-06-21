# Recording Guide

Use this guide to record a short portfolio demo for GitHub or a university application portfolio.

## Recommended Demo Length

45 to 60 seconds.

## Recommended Format

- Resolution: 1080p if possible.
- Audio: system audio on, microphone optional.
- Show the GUI version first.
- Keep the video focused on gameplay and project features.

## Run The GUI

```bash
./scripts/gui.sh
```

If using IntelliJ:

```text
Main class: gui.GuiMain
Module: YRRAK
```

## Video Structure

### 0-5 seconds: Start Screen

Show the game window and title.

Suggested voiceover:

```text
This is YRRAK, a Java Swing card roguelike prototype I built as a computer science portfolio project.
```

### 5-15 seconds: Core Rules

Show player HP, energy, enemy intent, and hand.

Suggested voiceover:

```text
The player uses energy to play elemental cards. Each turn, the player chooses whether to attack, block, or heal based on the enemy's next action.
```

### 15-30 seconds: Card Play And Element Advantage

Play an attack card. If possible, show elemental advantage.

Suggested voiceover:

```text
The game includes Fire, Water, and Thunder elements. When a card has advantage against an enemy, its damage increases.
```

### 30-40 seconds: Enemy Turn And Deck System

Click End Turn and show the enemy attacking. Show that energy returns and cards redraw.

Suggested voiceover:

```text
Cards move through a draw pile, hand, and discard pile. At the start of each turn, energy is restored and a new hand is drawn.
```

### 40-50 seconds: Reward Choice

Defeat an enemy and show reward cards.

Suggested voiceover:

```text
After each battle, the player chooses a reward card that enters the deck cycle.
```

### 50-60 seconds: Technical Summary

Show the GUI while briefly explaining the code structure.

Suggested voiceover:

```text
The project separates model classes, battle logic, console input/output, and the Swing GUI, making it easier to expand later.
```

## Screenshots To Capture

- Main GUI during a player turn.
- A card with elemental advantage.
- Enemy intent and player energy display.
- Reward card choice.
- Victory screen or battle log.

## Console Demo

To show a deterministic console run:

```bash
./scripts/demo.sh
```

The input sequence is stored in:

```text
docs/demo-input.txt
```

The sample output is stored in:

```text
docs/demo-output.txt
```

## Upload Notes

Recommended GitHub README media section:

```text
## Demo

Short gameplay video: coming soon.

Screenshots:
- Player turn
- Reward choice
- Victory log
```
