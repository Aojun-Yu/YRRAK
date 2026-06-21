# Portfolio Summary

## Project Title

YRRAK: Java Element Card Roguelike Prototype

## One-Sentence Description

YRRAK is a Java Swing card roguelike prototype where the player manages energy, deck cycling, elemental advantage, block, healing, and enemy intent to defeat three enemies in sequence.

## Short Portfolio Description

YRRAK is a small but complete Java game project developed for a computer science portfolio. The project began as a console-based card battle system and was gradually refactored into a clearer object-oriented structure with reusable battle logic, deck management, enemy behavior, and a Java Swing graphical interface.

The game uses three elements: Fire, Water, and Thunder. Element advantage increases card damage, while energy limits how many cards can be played each turn. The player must decide whether to attack, defend, heal, or save resources based on visible enemy intent. After defeating enemies, the player chooses reward cards that enter the deck cycle.

## Technologies Used

- Java
- Java Swing
- Object-oriented programming
- Git and GitHub
- Procedural placeholder art generation with Java
- Generated audio using Java sound APIs

## What I Built

- A playable console version with a complete game loop.
- A Java Swing GUI version with cards, enemy status, player status, battle logs, reward choices, generated art, and audio.
- A battle system with damage, block, healing, energy costs, enemy intent, and victory/game-over conditions.
- A deck system with draw pile, hand, discard pile, and discard recycling.
- A small asset pipeline using generated placeholder icons, portraits, background art, and a card frame.
- Documentation for running, recording, and presenting the project.

## Programming Concepts Demonstrated

- Classes and objects for `Player`, `Enemy`, `Card`, and `Element`.
- Encapsulation through fields, constructors, and getter methods.
- Separation of game logic from UI through `battle`, `model`, `gui`, and `util` packages.
- Refactoring shared logic into `BattleActions`, `DeckManager`, and `GameState`.
- Event-driven GUI programming with Swing.
- State management across turns, battles, cards, rewards, and audio.
- Defensive programming, such as preventing HP from going below zero and keeping the game runnable if art or audio fails.

## Main Challenges

The biggest challenge was keeping the game logic separate from the interface. Early versions placed too much behavior in `Main`, which made the code harder to expand. I refactored the project so that the battle rules could be reused by both the console version and the GUI version.

Another challenge was GUI state management. Cards should look disabled when the player lacks energy, but the text still needs to remain readable. Instead of using Swing's disabled button state, I used a custom card button style that keeps white text and adds a dark overlay for unplayable cards.

Adding audio also required care because sound should improve the game without breaking it. The current version generates lightweight sound effects and looping background music in code. If the audio device is unavailable, the game ignores the audio error and continues running.

## What I Learned

This project helped me understand how small software systems grow over time. I learned that finishing a project requires more than adding features: the code structure, documentation, visual presentation, and user experience all matter. I also learned how to scope a project so that it remains achievable while still demonstrating technical depth.

## Future Improvements

- Replace generated placeholder art with stronger original art.
- Improve the card layout using a custom component instead of buttons.
- Add screenshots and a short gameplay demo video.
- Add automated tests for battle calculations and deck behavior.
- Package the game into a downloadable desktop application.

## Personal Statement Version

I developed YRRAK to practise building a complete software project rather than isolated programming exercises. The project allowed me to apply object-oriented programming, event-driven GUI development, and iterative refactoring to a playable game system. I focused on keeping the scope realistic while still building a full loop with cards, enemies, rewards, audio, and visual feedback. This experience strengthened my interest in computer science because it showed how design decisions, code structure, and user experience work together in a finished application.
