# Project Status

## Current State

YRRAK is currently a playable Java card roguelike MVP for a portfolio project.

The project has:

- Clean object-oriented structure.
- A complete console game loop.
- A playable Java Swing GUI prototype.
- Lightweight generated GUI sound effects and looping background music.
- Three enemies.
- Element advantage rules.
- Deck, hand, and discard pile.
- Enemy intent.
- Different enemy behavior patterns.
- Reward card choices.
- Victory and game-over conditions.
- Asset folders for future UI, icon, and character art.
- Placeholder image support when art files are missing.
- Generated placeholder assets for the current GUI art pass.
- README, development log, demo files, and helper scripts.

## What Is Still Needed

Before using this as a polished portfolio project:

- Upload the project to GitHub.
- Add screenshots to the README.
- Record a short gameplay demo.
- Add original or properly licensed art assets.
- Replace generated placeholder art with stronger original art.
- Continue polishing the Swing GUI layout and card visuals.
- Playtest balance and tune card/enemy numbers.

## Current Blocker

GitHub push from the terminal is blocked because command-line GitHub credentials are not configured.

Earlier local commit:

```text
ab13d4f Build playable portfolio MVP
```

Use IntelliJ `Git -> Push...` or configure GitHub credentials before pushing from the terminal.

## Useful Commands

Run the game:

```bash
./scripts/run.sh
```

Run the prepared demo path:

```bash
./scripts/demo.sh
```

Run the GUI prototype:

```bash
./scripts/gui.sh
```

Detailed run instructions are in `docs/how-to-run.md`.
