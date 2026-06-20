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
        private final Enemy enemy;
        private final List<Card> hand;
        private final JLabel playerStatusLabel;
        private final JLabel enemyStatusLabel;
        private final JLabel intentLabel;
        private final JTextArea logArea;
        private final JPanel handPanel;

        GameFrame() {
            player = new Player(30, 3);
            enemy = new Enemy("Training Dummy", Element.NONE, 18, 4);
            hand = createStarterHand();

            playerStatusLabel = new JLabel();
            enemyStatusLabel = new JLabel();
            intentLabel = new JLabel();
            logArea = new JTextArea(10, 48);
            handPanel = new JPanel(new GridLayout(0, 2, 8, 8));

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
            JPanel statusPanel = new JPanel(new GridLayout(3, 1, 4, 4));
            statusPanel.setBorder(BorderFactory.createTitledBorder("Battle Status"));
            statusPanel.add(playerStatusLabel);
            statusPanel.add(enemyStatusLabel);
            statusPanel.add(intentLabel);

            logArea.setEditable(false);
            logArea.setLineWrap(true);
            logArea.setWrapStyleWord(true);
            JScrollPane logScrollPane = new JScrollPane(logArea);
            logScrollPane.setBorder(BorderFactory.createTitledBorder("Battle Log"));

            handPanel.setBorder(BorderFactory.createTitledBorder("Hand"));

            JButton endTurnButton = new JButton("End Turn");
            endTurnButton.addActionListener(event -> runEnemyTurn());

            JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
            bottomPanel.add(handPanel, BorderLayout.CENTER);
            bottomPanel.add(endTurnButton, BorderLayout.SOUTH);

            add(statusPanel, BorderLayout.NORTH);
            add(logScrollPane, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            addLog("GUI prototype started.");
            addLog("Defeat the Training Dummy to test the visual battle flow.");
        }

        private void refreshView() {
            playerStatusLabel.setText("Player HP: " + player.getHealth() + "/" + player.getMaxHealth()
                    + " | Block: " + player.getBlock()
                    + " | Energy: " + player.getEnergy() + "/" + player.getMaxEnergy());
            enemyStatusLabel.setText("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                    + " | Enemy: " + enemy.getName()
                    + " | Element: " + enemy.getElement().getDisplayName());
            intentLabel.setText("Enemy intent: " + enemy.getIntentDescription());

            handPanel.removeAll();

            for (Card card : hand) {
                JButton cardButton = new JButton(buildCardText(card));
                cardButton.addActionListener(event -> playCard(card));
                cardButton.setEnabled(player.isAlive() && enemy.isAlive());
                handPanel.add(cardButton);
            }

            handPanel.revalidate();
            handPanel.repaint();
        }

        private void playCard(Card card) {
            if (!player.canSpendEnergy(card.getCost())) {
                addLog("Not enough energy for " + card.getName() + ".");
                return;
            }

            player.spendEnergy(card.getCost());

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
                addLog("Victory! " + enemy.getName() + " was defeated.");
            }

            refreshView();
        }

        private void runEnemyTurn() {
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
            }

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

        private List<Card> createStarterHand() {
            List<Card> cards = new ArrayList<>();
            cards.add(new Card("Fire Strike", Element.FIRE, 1, 6, 0, 0));
            cards.add(new Card("Water Shield", Element.WATER, 1, 0, 5, 0));
            cards.add(new Card("Thunder Bolt", Element.THUNDER, 2, 10, 0, 0));
            cards.add(new Card("River Blade", Element.WATER, 1, 4, 2, 0));
            cards.add(new Card("Healing Rain", Element.WATER, 1, 0, 3, 5));
            return cards;
        }
    }
}
