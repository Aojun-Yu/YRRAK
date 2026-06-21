package battle;

import model.Card;
import model.Enemy;
import model.Player;
import util.GameIO;

import java.util.List;

public class BattleManager {
    private static final int CARDS_PER_TURN = 5;

    private final GameIO io;
    private final GameState state;

    public BattleManager(GameIO io) {
        this.io = io;
        state = new GameState(false);
    }

    public void runGame() {
        showSectionTitle("YRRAK: Element Card Prototype");
        showRules();

        while (!state.isVictory()) {
            Enemy enemy = state.currentEnemy();
            showSectionTitle("Battle " + (state.getCurrentEnemyIndex() + 1) + " of " + state.getEnemyCount());
            showEnemyIntro(enemy);

            runBattle();

            if (!state.getPlayer().isAlive()) {
                showSectionTitle("Game Over");
                io.show("You were defeated after defeating " + state.getCurrentEnemyIndex() + " enemies.");
                showFinalSummary();
                return;
            }

            state.advanceEnemy();
            showBattleResult(enemy);

            if (!state.isVictory()) {
                chooseRewardCard();
            }
        }

        showSectionTitle("Victory");
        Player player = state.getPlayer();
        io.show("You defeated all enemies with " + player.getHealth()
                + "/" + player.getMaxHealth() + " HP remaining.");
        showFinalSummary();
    }

    private void runBattle() {
        Enemy enemy = state.currentEnemy();
        Player player = state.getPlayer();

        while (player.isAlive() && enemy.isAlive()) {
            startPlayerTurn();
            runPlayerTurn(enemy);

            if (enemy.isAlive()) {
                runEnemyTurn(enemy);
            }
        }
    }

    private void runPlayerTurn(Enemy enemy) {
        showSectionTitle("Player Turn");
        Player player = state.getPlayer();
        DeckManager deckManager = state.getDeckManager();

        while (player.getEnergy() > 0 && enemy.isAlive() && deckManager.getHandSize() > 0) {
            showBattleState(enemy);
            showHand();
            io.show("Choose a card number, or 0 to end turn:");

            int choice = readChoice();

            if (choice == 0) {
                io.show("Player ends the turn.");
                return;
            }

            if (choice < 1 || choice > deckManager.getHandSize()) {
                io.show("Invalid choice.");
                continue;
            }

            Card selectedCard = deckManager.getHand().get(choice - 1);
            if (playCard(selectedCard, enemy)) {
                deckManager.moveFromHandToDiscard(selectedCard);
            }
        }

        if (player.getEnergy() == 0 && enemy.isAlive()) {
            io.show("No energy left.");
        }

        if (deckManager.getHandSize() == 0 && enemy.isAlive()) {
            io.show("No cards left in hand.");
        }
    }

    private void runEnemyTurn(Enemy enemy) {
        showSectionTitle("Enemy Turn");
        Player player = state.getPlayer();
        int damage = enemy.getIntentDamage();
        player.takeDamage(damage);
        io.show(enemy.getName() + " uses " + enemy.getIntentDescription() + ".");
        enemy.finishTurn();
    }

    private boolean playCard(Card card, Enemy enemy) {
        Player player = state.getPlayer();

        if (!player.canSpendEnergy(card.getCost())) {
            io.show("Not enough energy.");
            return false;
        }

        player.spendEnergy(card.getCost());
        state.recordPlayedCard();

        int finalDamage = card.calculateDamageAgainst(enemy);
        enemy.takeDamage(finalDamage);
        player.gainBlock(card.getBlock());
        player.heal(card.getHeal());

        io.show("Played " + card.getName() + ".");

        if (finalDamage > 0) {
            io.show("Dealt " + finalDamage + " damage.");
        }

        if (card.hasAdvantageAgainst(enemy)) {
            io.show("Element advantage!");
        }

        if (card.getBlock() > 0) {
            io.show("Gained " + card.getBlock() + " block.");
        }

        if (card.getHeal() > 0) {
            io.show("Healed " + card.getHeal() + " HP.");
        }

        return true;
    }

    private void showBattleState(Enemy enemy) {
        Player player = state.getPlayer();
        DeckManager deckManager = state.getDeckManager();
        io.show("");
        io.show("Player HP: " + player.getHealth() + "/" + player.getMaxHealth()
                + " | Block: " + player.getBlock()
                + " | Energy: " + player.getEnergy() + "/" + player.getMaxEnergy());
        io.show("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                + " | Enemy: " + enemy.getName()
                + " | Element: " + enemy.getElement().getDisplayName());
        io.show("Enemy intent: " + enemy.getIntentDescription());
        io.show("Draw pile: " + deckManager.getDrawPileSize()
                + " | Hand: " + deckManager.getHandSize()
                + " | Discard pile: " + deckManager.getDiscardPileSize());
    }

    private void showHand() {
        io.show("Hand:");
        List<Card> hand = state.getDeckManager().getHand();

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            io.show((i + 1) + ". " + card.getName()
                    + " [" + card.getElement().getDisplayName() + "]"
                    + " Cost: " + card.getCost()
                    + " Damage: " + card.getDamage()
                    + " Block: " + card.getBlock()
                    + " Heal: " + card.getHeal());
        }
    }

    private int readChoice() {
        String input = io.readLine();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void startPlayerTurn() {
        Player player = state.getPlayer();
        DeckManager deckManager = state.getDeckManager();
        player.startTurn();
        deckManager.startTurn(CARDS_PER_TURN);

        if (deckManager.wasDiscardRecycledLastDraw()) {
            io.show("Discard pile is recycled into draw pile.");
        }
    }

    private void chooseRewardCard() {
        List<Card> rewards = GameContent.createRewardChoices(state.getSelectedRewards());

        showSectionTitle("Reward");
        io.show("Choose one reward card:");

        for (int i = 0; i < rewards.size(); i++) {
            Card card = rewards.get(i);
            io.show((i + 1) + ". " + card.getName()
                    + " [" + card.getElement().getDisplayName() + "]"
                    + " Cost: " + card.getCost()
                    + " Damage: " + card.getDamage()
                    + " Block: " + card.getBlock()
                    + " Heal: " + card.getHeal());
        }

        while (true) {
            int choice = readChoice();

            if (choice < 1 || choice > rewards.size()) {
                io.show("Invalid reward choice. Choose 1, 2, or 3:");
                continue;
            }

            Card selectedReward = rewards.get(choice - 1);
            state.getDeckManager().addToDiscard(selectedReward);
            state.recordSelectedReward();
            io.show(selectedReward.getName() + " was added to your discard pile.");
            return;
        }
    }

    private void showRules() {
        io.show("Goal: defeat 3 enemies in a row.");
        io.show("Controls: enter a card number to play it, or 0 to end your turn.");
        io.show("Elements: Fire beats Thunder, Thunder beats Water, Water beats Fire.");
        io.show("Block absorbs damage before HP is reduced.");
        io.show("Watch enemy intent to decide whether to attack or defend.");
    }

    private void showEnemyIntro(Enemy enemy) {
        io.show("A new enemy appears: " + enemy.getName()
                + " [" + enemy.getElement().getDisplayName() + "]");
        io.show("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
    }

    private void showBattleResult(Enemy enemy) {
        showDivider();
        io.show(enemy.getName() + " defeated. (" + state.getCurrentEnemyIndex() + "/" + state.getEnemyCount() + ")");
        Player player = state.getPlayer();
        io.show("Player HP: " + player.getHealth() + "/" + player.getMaxHealth());
    }

    private void showFinalSummary() {
        Player player = state.getPlayer();
        showDivider();
        io.show("Run Summary");
        io.show("Enemies defeated: " + state.getCurrentEnemyIndex() + "/" + state.getEnemyCount());
        io.show("Cards played: " + state.getPlayedCards());
        io.show("Reward cards chosen: " + state.getSelectedRewards());
        io.show("Final HP: " + player.getHealth() + "/" + player.getMaxHealth());
    }

    private void showSectionTitle(String title) {
        io.show("");
        showDivider();
        io.show(title);
        showDivider();
    }

    private void showDivider() {
        io.show("----------------------------------------");
    }

}
