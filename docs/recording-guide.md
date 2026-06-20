# Recording Guide

Use this guide to record a short portfolio demo.

## Recommended Demo Length

30 to 60 seconds.

## What To Show

1. Start the game.
2. Show the rules at startup.
3. Play a card that deals damage.
4. Show element advantage.
5. Show enemy intent.
6. Choose a reward card.
7. Show the final victory summary.

## Demo Script

Run the project:

```bash
./scripts/run.sh
```

Use the input sequence in `docs/demo-input.txt` for a reliable victory path.

To auto-play the prepared demo path:

```bash
./scripts/demo.sh
```

To show the early GUI prototype:

```bash
./scripts/gui.sh
```

The GUI prototype now supports a visual three-enemy run with reward choices and basic styling. It is intended as an early interface demo, while the console version remains the more complete rules reference.

## Suggested Screenshots

- Startup rules.
- A player turn showing hand, enemy intent, and deck piles.
- Reward card choice.
- Victory run summary.
