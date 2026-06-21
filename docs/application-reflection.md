# Application Reflection

## Why This Project Is Relevant

YRRAK is relevant to a Computer Science, Software Engineering, Game Development, or Interactive Media application because it shows independent project development. It is not only a classroom exercise: it has a playable loop, structured code, GUI, assets, audio, documentation, and version control.

For university applications, the most important value of this project is not that it is a large commercial game. Its value is that it demonstrates consistent iteration, problem solving, and the ability to finish a scoped technical project.

## Design Decisions

### Scope Control

I deliberately kept the project small. Instead of building a large game with maps, save files, online systems, and many cards, I focused on a minimum playable version with three enemies, a small deck, and a complete battle loop. This made the project achievable and easier to polish.

### Object-Oriented Structure

The code is organised into separate packages:

- `model` stores core game data such as cards, enemies, elements, and the player.
- `battle` stores combat rules, deck logic, and shared game state.
- `gui` stores the Swing interface.
- `util` stores console input/output helpers.

This structure makes the project easier to understand and easier to expand later.

### Logic and Interface Separation

One important goal was to avoid placing all logic in the GUI. The console and GUI versions share the same battle concepts, which means future interfaces can reuse the same core rules. This is important because a real software project should not depend on one interface forever.

### Readability Over Complexity

I avoided over-engineering. The project uses simple Java classes and clear method names rather than complex frameworks. This makes the code easier to explain in an interview or portfolio review.

## Problems Solved

### Energy and Playable Cards

At first, the GUI used disabled buttons when the player did not have enough energy. This caused the card text to become dark and hard to read. I fixed this by keeping the card button enabled and adding custom logic that blocks invalid plays while keeping the text white.

### Deck Cycling

The game needed a system where cards move from draw pile to hand to discard pile, and the discard pile returns to the draw pile when needed. This required careful state management so cards did not disappear or duplicate incorrectly.

### GUI Polish

The early GUI looked too plain, so I added a dark fantasy visual style, generated element icons, character portraits, a background image, a card frame, and custom card rendering. These assets are placeholders, but they make the game more presentable and create a foundation for future art.

### Audio Without External Files

Instead of downloading sound files, I generated simple sound effects and background music using Java audio APIs. This avoids copyright issues and keeps the project self-contained.

## What I Would Improve Next

- Replace generated placeholder art with original drawn assets.
- Create a better custom card component instead of relying on buttons.
- Add unit tests for damage, element advantage, healing, block, and deck cycling.
- Add screenshots and a 45-60 second gameplay video.
- Package the game so users can run it without opening an IDE.

## Interview Talking Points

- I started with simple classes and gradually refactored the project as complexity increased.
- I separated game logic from UI so the console and GUI versions could share the same rules.
- I kept the scope realistic to make sure the project could be completed.
- I used Git commits to track progress and make the project suitable for GitHub.
- I learned that polish, documentation, and presentation matter as much as raw features in a portfolio project.

## Honest Limitations

This is still a prototype. The art is placeholder art, the GUI can be improved, and the game balance needs more playtesting. However, the project is complete enough to demonstrate programming ability, design thinking, and independent iteration.
