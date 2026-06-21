# Development Log

This log records design decisions, programming progress, and lessons learned while building YRRAK.

## 2026-06-20

### Current Focus

Building the foundation of a Java card roguelike combat system.

### What Was Implemented

- Created basic `Player`, `Enemy`, and `Card` classes.
- Added card damage and energy cost.
- Added energy checking before playing a card.
- Added enemy attacks against the player.
- Added health clamping so health does not go below 0.
- Added `isAlive()` checks for both player and enemy.
- Added block so damage is absorbed before health is reduced.
- Added cards that can provide damage and block.
- Refactored repeated card-playing code into `playCard()`.
- Added energy restoration to simulate a new turn.

### Design Notes

The game should not depend only on standard card combat. A possible original direction is to include double-sided cards as a special card type. These cards can create long-term consequences because using one side changes what the card becomes later.

### Programming Lessons

- Fields store object state.
- Constructors set the starting state of an object.
- Getter methods expose needed information safely.
- Methods reduce repeated code and make the program easier to extend.
- `Math.min()` is useful when one value should not exceed another, such as block absorbing damage.

### MVP Refactor

The project was reorganized into a clearer object-oriented structure:

- `model` contains game data classes.
- `battle` contains the combat flow.
- `util` contains input/output helpers.
- `Main` only starts the game.

The game now has a playable console MVP:

- Three core elements: Fire, Water, and Thunder.
- Element advantage increases damage.
- Player turns allow choosing cards from a hand.
- The game now has a draw pile, hand, and discard pile.
- Played cards move to the discard pile.
- The discard pile is recycled when the draw pile is empty.
- Enemy turns automatically attack.
- Enemy intent is shown during the player turn.
- Enemies now have simple behavior patterns based on their turn count.
- Fire Spirit uses a stronger attack every third enemy turn.
- Thunder Beast alternates between quick attacks and charged strikes.
- The player wins after defeating three enemies.
- After defeating an enemy, the player can choose one reward card.
- Reward cards are added to the discard pile so they enter the deck cycle later.
- Console output now has clearer section titles for battles, player turns, enemy turns, rewards, victory, and game over.
- The game now explains its rules at startup.
- The game now shows a final run summary with enemies defeated, cards played, rewards chosen, and final HP.
- Input/output goes through `GameIO`, which makes future GUI work easier.
- README was reorganized into a clearer portfolio-style project page with gameplay, code structure, running instructions, and future plans.
- Portfolio support files were added:
  - `docs/portfolio-summary.md`
  - `docs/demo-output.txt`
  - `docs/demo-input.txt`
  - `docs/recording-guide.md`
- Helper scripts were added:
  - `scripts/run.sh`
  - `scripts/demo.sh`
- Cards now support healing.
- `Healing Rain` now restores HP and still provides a small amount of block.
- Player healing is capped at max HP.
- Added a small Java Swing GUI prototype in `gui.GuiMain`.
- Added `scripts/gui.sh` to launch the GUI prototype.
- The GUI prototype now supports three enemies, reward card choices, and a victory/game-over log.
- The GUI prototype now has a basic dark theme, colored card buttons, styled panels, and improved log readability.

### Next Small Goal

Capture screenshots of the console and styled GUI versions for the portfolio.

## 2026-06-22

### Current Focus

Preparing the project for GitHub and university application presentation.

### What Was Implemented

- Added generated placeholder art for element icons, character portraits, background, and card frame.
- Added GUI support for loading assets from the `assets/` folder.
- Added custom card button rendering so cards look more like game cards.
- Added lightweight generated sound effects and looping background music.
- Fixed card readability when the player does not have enough energy by keeping card text white.
- Added a Java asset generator in `tools/AssetGenerator.java`.
- Updated README and project status documentation.
- Added application-focused documentation:
  - `docs/portfolio-summary.md`
  - `docs/application-reflection.md`
  - `docs/recording-guide.md`
  - `docs/art-direction.md`

### Design Notes

The project is now positioned as a compact Java portfolio game rather than a commercial-scale game. The priority is to show a complete, readable, and presentable project with clear object-oriented structure, not to add large features too early.

### Programming Lessons

- GUI polish can create technical issues, such as Swing changing disabled button text colors.
- Keeping UI state readable sometimes requires custom rendering rather than default component behavior.
- Generating placeholder assets in code can make a project more presentable without introducing licensing problems.
- Application projects need documentation and reflection, not only working code.

### Next Small Goal

Capture screenshots and record a 45-60 second GUI demo video.
