package battle;

import model.Card;
import model.Element;
import model.Enemy;
import model.Player;
import util.GameIO;

import java.util.ArrayList;
import java.util.List;

public class BattleManager {
    private final GameIO io;
    private final Player player;
    private final List<Card> drawPile;
    private final List<Card> hand;
    private final List<Card> discardPile;
    private final List<Enemy> enemies;
    private int rewardCount;
    private int playedCards;
    private int selectedRewards;

    public BattleManager(GameIO io) {
        this.io = io;
        this.player = new Player(30, 3);
        this.drawPile = createStarterDeck();
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.enemies = createEnemies();
        this.rewardCount = 0;
        this.playedCards = 0;
        this.selectedRewards = 0;
    }

    public void runGame() {
        showSectionTitle("YRRAK: Element Card Prototype");
        showRules();

        int defeatedEnemies = 0;

        for (Enemy enemy : enemies) {
            showSectionTitle("Battle " + (defeatedEnemies + 1) + " of " + enemies.size());
            showEnemyIntro(enemy);

            runBattle(enemy);

            if (!player.isAlive()) {
                showSectionTitle("Game Over");
                io.show("You were defeated after defeating " + defeatedEnemies + " enemies.");
                showFinalSummary(defeatedEnemies);
                return;
            }

            defeatedEnemies++;
            showBattleResult(enemy, defeatedEnemies);

            if (defeatedEnemies < enemies.size()) {
                chooseRewardCard();
            }
        }

        showSectionTitle("Victory");
        io.show("You defeated all enemies with " + player.getHealth()
                + "/" + player.getMaxHealth() + " HP remaining.");
        showFinalSummary(defeatedEnemies);
    }

    private void runBattle(Enemy enemy) {
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

        while (player.getEnergy() > 0 && enemy.isAlive() && !hand.isEmpty()) {
            showBattleState(enemy);
            showHand();
            io.show("Choose a card number, or 0 to end turn:");

            int choice = readChoice();

            if (choice == 0) {
                io.show("Player ends the turn.");
                return;
            }

            if (choice < 1 || choice > hand.size()) {
                io.show("Invalid choice.");
                continue;
            }

            Card selectedCard = hand.get(choice - 1);
            if (playCard(selectedCard, enemy)) {
                hand.remove(choice - 1);
                discardPile.add(selectedCard);
            }
        }

        if (player.getEnergy() == 0 && enemy.isAlive()) {
            io.show("No energy left.");
        }

        if (hand.isEmpty() && enemy.isAlive()) {
            io.show("No cards left in hand.");
        }
    }

    private void runEnemyTurn(Enemy enemy) {
        showSectionTitle("Enemy Turn");
        int damage = enemy.getIntentDamage();
        player.takeDamage(damage);
        io.show(enemy.getName() + " uses " + enemy.getIntentDescription() + ".");
        enemy.finishTurn();
    }

    private boolean playCard(Card card, Enemy enemy) {
        if (!player.canSpendEnergy(card.getCost())) {
            io.show("Not enough energy.");
            return false;
        }

        player.spendEnergy(card.getCost());
        playedCards++;

        int finalDamage = card.calculateDamageAgainst(enemy);
        enemy.takeDamage(finalDamage);
        player.gainBlock(card.getBlock());

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

        return true;
    }

    private void showBattleState(Enemy enemy) {
        io.show("");
        io.show("Player HP: " + player.getHealth() + "/" + player.getMaxHealth()
                + " | Block: " + player.getBlock()
                + " | Energy: " + player.getEnergy() + "/" + player.getMaxEnergy());
        io.show("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                + " | Enemy: " + enemy.getName()
                + " | Element: " + enemy.getElement().getDisplayName());
        io.show("Enemy intent: " + enemy.getIntentDescription());
        io.show("Draw pile: " + drawPile.size()
                + " | Hand: " + hand.size()
                + " | Discard pile: " + discardPile.size());
    }

    private void showHand() {
        io.show("Hand:");

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            io.show((i + 1) + ". " + card.getName()
                    + " [" + card.getElement().getDisplayName() + "]"
                    + " Cost: " + card.getCost()
                    + " Damage: " + card.getDamage()
                    + " Block: " + card.getBlock());
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
        player.startTurn();
        discardHand();
        drawCards(5);
    }

    private void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    private void drawCards(int amount) {
        for (int i = 0; i < amount; i++) {
            if (drawPile.isEmpty()) {
                recycleDiscardPile();
            }

            if (drawPile.isEmpty()) {
                return;
            }

            hand.add(drawPile.remove(0));
        }
    }

    private void recycleDiscardPile() {
        if (discardPile.isEmpty()) {
            return;
        }

        drawPile.addAll(discardPile);
        discardPile.clear();
        io.show("Discard pile is recycled into draw pile.");
    }

    private void chooseRewardCard() {
        List<Card> rewards = createRewardChoices();

        showSectionTitle("Reward");
        io.show("Choose one reward card:");

        for (int i = 0; i < rewards.size(); i++) {
            Card card = rewards.get(i);
            io.show((i + 1) + ". " + card.getName()
                    + " [" + card.getElement().getDisplayName() + "]"
                    + " Cost: " + card.getCost()
                    + " Damage: " + card.getDamage()
                    + " Block: " + card.getBlock());
        }

        while (true) {
            int choice = readChoice();

            if (choice < 1 || choice > rewards.size()) {
                io.show("Invalid reward choice. Choose 1, 2, or 3:");
                continue;
            }

            Card selectedReward = rewards.get(choice - 1);
            discardPile.add(selectedReward);
            selectedRewards++;
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

    private void showBattleResult(Enemy enemy, int defeatedEnemies) {
        showDivider();
        io.show(enemy.getName() + " defeated. (" + defeatedEnemies + "/" + enemies.size() + ")");
        io.show("Player HP: " + player.getHealth() + "/" + player.getMaxHealth());
    }

    private void showFinalSummary(int defeatedEnemies) {
        showDivider();
        io.show("Run Summary");
        io.show("Enemies defeated: " + defeatedEnemies + "/" + enemies.size());
        io.show("Cards played: " + playedCards);
        io.show("Reward cards chosen: " + selectedRewards);
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

    private List<Card> createRewardChoices() {
        List<Card> rewards = new ArrayList<>();

        if (rewardCount == 0) {
            rewards.add(new Card("Flame Surge", Element.FIRE, 2, 12, 0));
            rewards.add(new Card("Healing Rain", Element.WATER, 1, 0, 8));
            rewards.add(new Card("Quick Spark", Element.THUNDER, 0, 3, 0));
        } else {
            rewards.add(new Card("Blazing Guard", Element.FIRE, 1, 4, 4));
            rewards.add(new Card("Tidal Crash", Element.WATER, 2, 9, 2));
            rewards.add(new Card("Storm Breaker", Element.THUNDER, 2, 11, 0));
        }

        rewardCount++;
        return rewards;
    }

    private List<Card> createStarterDeck() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card("Fire Strike", Element.FIRE, 1, 6, 0));
        cards.add(new Card("Water Shield", Element.WATER, 1, 0, 5));
        cards.add(new Card("Thunder Bolt", Element.THUNDER, 2, 10, 0));
        cards.add(new Card("River Blade", Element.WATER, 1, 4, 2));
        cards.add(new Card("Storm Guard", Element.THUNDER, 2, 5, 5));
        cards.add(new Card("Ember Guard", Element.FIRE, 1, 3, 3));
        cards.add(new Card("Rain Lance", Element.WATER, 2, 8, 0));
        cards.add(new Card("Spark Step", Element.THUNDER, 1, 4, 1));
        return cards;
    }

    private List<Enemy> createEnemies() {
        List<Enemy> enemyList = new ArrayList<>();
        enemyList.add(new Enemy("Training Dummy", Element.NONE, 18, 4));
        enemyList.add(new Enemy("Fire Spirit", Element.FIRE, 20, 5));
        enemyList.add(new Enemy("Thunder Beast", Element.THUNDER, 24, 6));
        return enemyList;
    }
}
