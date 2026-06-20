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

### Next Small Goal

Add `maxEnergy` to `Player` so `restoreEnergy()` can restore energy without receiving a hard-coded number from `Main`.
