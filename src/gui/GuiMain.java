package gui;

import model.Card;
import model.Element;
import model.Enemy;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }

    private static class GameFrame extends JFrame {
        private final Player player;
        private final List<Enemy> enemies;
        private final List<Card> hand;
        private final JLabel playerStatusLabel;
        private final JLabel enemyStatusLabel;
        private final JLabel intentLabel;
        private final JLabel progressLabel;
        private final JTextArea logArea;
        private final JPanel handPanel;
        private final JPanel rewardPanel;
        private int currentEnemyIndex;
        private int selectedRewards;
        private int playedCards;

        GameFrame() {
            player = new Player(30, 3);
            enemies = createEnemies();
            hand = createStarterHand();
            currentEnemyIndex = 0;
            selectedRewards = 0;
            playedCards = 0;

            playerStatusLabel = new JLabel();
            enemyStatusLabel = new JLabel();
            intentLabel = new JLabel();
            progressLabel = new JLabel();
            logArea = new JTextArea(10, 48);
            handPanel = new JPanel(new GridLayout(0, 2, 8, 8));
            rewardPanel = new JPanel(new GridLayout(0, 3, 8, 8));

            setTitle("YRRAK GUI Prototype");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout(12, 12));
            setMinimumSize(new Dimension(760, 520));

            buildLayout();
            refreshView();
            pack();
            setLocationRelativeTo(null);
        }

        private void buildLayout() {
            JPanel statusPanel = new JPanel(new GridLayout(4, 1, 4, 4));
            statusPanel.setBorder(BorderFactory.createTitledBorder("Battle Status"));
            statusPanel.add(progressLabel);
            statusPanel.add(playerStatusLabel);
            statusPanel.add(enemyStatusLabel);
            statusPanel.add(intentLabel);

            logArea.setEditable(false);
            logArea.setLineWrap(true);
            logArea.setWrapStyleWord(true);
            JScrollPane logScrollPane = new JScrollPane(logArea);
            logScrollPane.setBorder(BorderFactory.createTitledBorder("Battle Log"));

            handPanel.setBorder(BorderFactory.createTitledBorder("Hand"));
            rewardPanel.setBorder(BorderFactory.createTitledBorder("Rewards"));

            JButton endTurnButton = new JButton("End Turn");
            endTurnButton.addActionListener(event -> runEnemyTurn());

            JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
            bottomPanel.add(handPanel, BorderLayout.CENTER);
            bottomPanel.add(rewardPanel, BorderLayout.NORTH);
            bottomPanel.add(endTurnButton, BorderLayout.SOUTH);

            add(statusPanel, BorderLayout.NORTH);
            add(logScrollPane, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            addLog("GUI prototype started.");
            addLog("Defeat 3 enemies to win.");
            addLog("Current enemy: " + currentEnemy().getName() + ".");
        }

        private void refreshView() {
            Enemy enemy = currentEnemy();

            int displayedBattle = Math.min(currentEnemyIndex + 1, enemies.size());
            progressLabel.setText("Battle " + displayedBattle + " of " + enemies.size()
                    + " | Rewards chosen: " + selectedRewards
                    + " | Cards played: " + playedCards);
            playerStatusLabel.setText("Player HP: " + player.getHealth() + "/" + player.getMaxHealth()
                    + " | Block: " + player.getBlock()
                    + " | Energy: " + player.getEnergy() + "/" + player.getMaxEnergy());
            enemyStatusLabel.setText("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                    + " | Enemy: " + enemy.getName()
                    + " | Element: " + enemy.getElement().getDisplayName());
            intentLabel.setText("Enemy intent: " + enemy.getIntentDescription());

            handPanel.removeAll();

            boolean gameActive = player.isAlive() && currentEnemyIndex < enemies.size() && enemy.isAlive();

            for (Card card : hand) {
                JButton cardButton = new JButton(buildCardText(card));
                cardButton.addActionListener(event -> playCard(card));
                cardButton.setEnabled(gameActive && rewardPanel.getComponentCount() == 0);
                handPanel.add(cardButton);
            }

            handPanel.revalidate();
            handPanel.repaint();
        }

        private void playCard(Card card) {
            Enemy enemy = currentEnemy();

            if (!player.canSpendEnergy(card.getCost())) {
                addLog("Not enough energy for " + card.getName() + ".");
                return;
            }

            player.spendEnergy(card.getCost());
            playedCards++;

            int finalDamage = card.calculateDamageAgainst(enemy);
            enemy.takeDamage(finalDamage);
            player.gainBlock(card.getBlock());
            player.heal(card.getHeal());

            addLog("Played " + card.getName() + ".");

            if (finalDamage > 0) {
                addLog("Dealt " + finalDamage + " damage.");
            }

            if (card.hasAdvantageAgainst(enemy)) {
                addLog("Element advantage!");
            }

            if (card.getBlock() > 0) {
                addLog("Gained " + card.getBlock() + " block.");
            }

            if (card.getHeal() > 0) {
                addLog("Healed " + card.getHeal() + " HP.");
            }

            if (!enemy.isAlive()) {
                addLog(enemy.getName() + " was defeated.");
                handleEnemyDefeated();
            }

            refreshView();
        }

        private void runEnemyTurn() {
            Enemy enemy = currentEnemy();

            if (!player.isAlive() || !enemy.isAlive()) {
                return;
            }

            int damage = enemy.getIntentDamage();
            player.takeDamage(damage);
            addLog(enemy.getName() + " uses " + enemy.getIntentDescription() + ".");
            enemy.finishTurn();
            player.startTurn();
            addLog("A new player turn begins.");

            if (!player.isAlive()) {
                addLog("Game Over.");
                addLog("Defeated enemies: " + currentEnemyIndex + "/" + enemies.size() + ".");
            }

            refreshView();
        }

        private void handleEnemyDefeated() {
            currentEnemyIndex++;

            if (currentEnemyIndex >= enemies.size()) {
                addLog("Victory! All enemies were defeated.");
                addLog("Cards played: " + playedCards + ".");
                addLog("Final HP: " + player.getHealth() + "/" + player.getMaxHealth() + ".");
                return;
            }

            showRewardChoices();
        }

        private void showRewardChoices() {
            rewardPanel.removeAll();

            for (Card reward : createRewardChoices()) {
                JButton rewardButton = new JButton(buildCardText(reward));
                rewardButton.addActionListener(event -> chooseReward(reward));
                rewardPanel.add(rewardButton);
            }

            rewardPanel.revalidate();
            rewardPanel.repaint();
            addLog("Choose one reward card.");
        }

        private void chooseReward(Card reward) {
            hand.add(reward);
            selectedRewards++;
            rewardPanel.removeAll();
            rewardPanel.revalidate();
            rewardPanel.repaint();

            player.startTurn();
            addLog(reward.getName() + " was added to your hand.");
            addLog("Next enemy: " + currentEnemy().getName() + ".");
            refreshView();
        }

        private String buildCardText(Card card) {
            return "<html><b>" + card.getName() + "</b><br>"
                    + card.getElement().getDisplayName()
                    + " | Cost " + card.getCost()
                    + "<br>Damage " + card.getDamage()
                    + " | Block " + card.getBlock()
                    + " | Heal " + card.getHeal()
                    + "</html>";
        }

        private void addLog(String message) {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }

        private Enemy currentEnemy() {
            return enemies.get(Math.min(currentEnemyIndex, enemies.size() - 1));
        }

        private List<Card> createStarterHand() {
            List<Card> cards = new ArrayList<>();
            cards.add(new Card("Fire Strike", Element.FIRE, 1, 6, 0, 0));
            cards.add(new Card("Water Shield", Element.WATER, 1, 0, 5, 0));
            cards.add(new Card("Thunder Bolt", Element.THUNDER, 2, 10, 0, 0));
            cards.add(new Card("River Blade", Element.WATER, 1, 4, 2, 0));
            cards.add(new Card("Healing Rain", Element.WATER, 1, 0, 3, 5));
            return cards;
        }

        private List<Enemy> createEnemies() {
            List<Enemy> enemyList = new ArrayList<>();
            enemyList.add(new Enemy("Training Dummy", Element.NONE, 18, 4));
            enemyList.add(new Enemy("Fire Spirit", Element.FIRE, 20, 5));
            enemyList.add(new Enemy("Thunder Beast", Element.THUNDER, 24, 6));
            return enemyList;
        }

        private List<Card> createRewardChoices() {
            List<Card> rewards = new ArrayList<>();

            if (selectedRewards == 0) {
                rewards.add(new Card("Flame Surge", Element.FIRE, 2, 12, 0, 0));
                rewards.add(new Card("Healing Rain", Element.WATER, 1, 0, 3, 5));
                rewards.add(new Card("Quick Spark", Element.THUNDER, 0, 3, 0, 0));
            } else {
                rewards.add(new Card("Blazing Guard", Element.FIRE, 1, 4, 4, 0));
                rewards.add(new Card("Tidal Crash", Element.WATER, 2, 9, 2, 0));
                rewards.add(new Card("Storm Breaker", Element.THUNDER, 2, 11, 0, 0));
            }

            return rewards;
        }
    }
}
