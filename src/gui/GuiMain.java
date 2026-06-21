package gui;

import model.Card;
import model.Element;
import model.Enemy;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }

    private static class GameFrame extends JFrame {
        private static final Color BACKGROUND = new Color(12, 15, 27);
        private static final Color PANEL = new Color(25, 31, 52);
        private static final Color PANEL_DARK = new Color(16, 20, 35);
        private static final Color TEXT = new Color(238, 243, 255);
        private static final Color MUTED_TEXT = new Color(176, 187, 215);
        private static final Color ACCENT = new Color(112, 210, 255);
        private static final Color DANGER = new Color(255, 110, 110);
        private static final Color FIRE = new Color(255, 132, 86);
        private static final Color WATER = new Color(90, 176, 255);
        private static final Color THUNDER = new Color(255, 220, 90);
        private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
        private static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, 14);
        private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);
        private static final Font CARD_FONT = new Font("SansSerif", Font.BOLD, 12);

        private final Player player;
        private final List<Enemy> enemies;
        private final List<Card> drawPile;
        private final List<Card> hand;
        private final List<Card> discardPile;
        private final JLabel titleLabel;
        private final JLabel playerStatusLabel;
        private final JLabel enemyStatusLabel;
        private final JLabel intentLabel;
        private final JLabel deckStatusLabel;
        private final JTextArea logArea;
        private final JPanel handPanel;
        private final JPanel rewardPanel;
        private final JButton endTurnButton;
        private final JButton restartButton;
        private int currentEnemyIndex;
        private int selectedRewards;
        private int playedCards;
        private int turnNumber;

        GameFrame() {
            player = new Player(30, 3);
            enemies = createEnemies();
            drawPile = createStarterDeck();
            hand = new ArrayList<>();
            discardPile = new ArrayList<>();
            currentEnemyIndex = 0;
            selectedRewards = 0;
            playedCards = 0;
            turnNumber = 1;

            titleLabel = new JLabel();
            playerStatusLabel = new JLabel();
            enemyStatusLabel = new JLabel();
            intentLabel = new JLabel();
            deckStatusLabel = new JLabel();
            logArea = new JTextArea(12, 48);
            handPanel = new JPanel(new GridLayout(0, 4, 10, 10));
            rewardPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            endTurnButton = new JButton("End Turn - enemy attacks, then Energy returns to 3");
            restartButton = new JButton("Restart");

            setTitle("YRRAK - Element Card Battle");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(980, 680));
            getContentPane().setBackground(BACKGROUND);

            buildLayout();
            startPlayerTurn(false);
            addLog("Goal: defeat 3 enemies.");
            addLog("Rule: click End Turn to let the enemy act, then your Energy returns to 3.");
            refreshView();
            pack();
            setLocationRelativeTo(null);
        }

        private void buildLayout() {
            setLayout(new BorderLayout(14, 14));
            ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            titleLabel.setForeground(TEXT);
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(titleLabel, BorderLayout.NORTH);

            JPanel statusPanel = new JPanel(new GridLayout(4, 1, 6, 6));
            stylePanel(statusPanel, "Status");
            styleStatusLabel(playerStatusLabel);
            styleStatusLabel(enemyStatusLabel);
            styleStatusLabel(intentLabel);
            styleStatusLabel(deckStatusLabel);
            statusPanel.add(playerStatusLabel);
            statusPanel.add(enemyStatusLabel);
            statusPanel.add(intentLabel);
            statusPanel.add(deckStatusLabel);

            logArea.setEditable(false);
            logArea.setLineWrap(true);
            logArea.setWrapStyleWord(true);
            logArea.setBackground(PANEL_DARK);
            logArea.setForeground(TEXT);
            logArea.setCaretColor(TEXT);
            logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            JScrollPane logScrollPane = new JScrollPane(logArea);
            logScrollPane.getViewport().setBackground(PANEL_DARK);
            styleScrollPane(logScrollPane, "Battle Log");

            JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
            centerPanel.setBackground(BACKGROUND);
            centerPanel.add(statusPanel, BorderLayout.NORTH);
            centerPanel.add(logScrollPane, BorderLayout.CENTER);

            stylePanel(handPanel, "Hand - click a card to play it");
            stylePanel(rewardPanel, "Reward - choose one after winning a battle");

            endTurnButton.addActionListener(event -> runEnemyTurn());
            restartButton.addActionListener(event -> restartGame());
            styleButton(endTurnButton, ACCENT);
            styleButton(restartButton, MUTED_TEXT);

            JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            actionPanel.setBackground(BACKGROUND);
            actionPanel.add(endTurnButton);
            actionPanel.add(restartButton);

            JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
            bottomPanel.setBackground(BACKGROUND);
            bottomPanel.add(rewardPanel, BorderLayout.NORTH);
            bottomPanel.add(handPanel, BorderLayout.CENTER);
            bottomPanel.add(actionPanel, BorderLayout.SOUTH);

            add(centerPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        private void refreshView() {
            Enemy enemy = currentEnemy();
            boolean hasRewardChoices = rewardPanel.getComponentCount() > 0;
            boolean gameActive = player.isAlive() && currentEnemyIndex < enemies.size() && enemy.isAlive();

            titleLabel.setText("YRRAK Battle " + Math.min(currentEnemyIndex + 1, enemies.size())
                    + "/" + enemies.size() + " - Turn " + turnNumber);
            playerStatusLabel.setText("Player  HP " + player.getHealth() + "/" + player.getMaxHealth()
                    + "    Block " + player.getBlock()
                    + "    Energy " + player.getEnergy() + "/" + player.getMaxEnergy());
            enemyStatusLabel.setText("Enemy  " + enemy.getName()
                    + "    HP " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                    + "    Element " + enemy.getElement().getDisplayName());
            intentLabel.setText("Enemy Intent  " + enemy.getIntentDescription());
            deckStatusLabel.setText("Deck " + drawPile.size()
                    + "    Hand " + hand.size()
                    + "    Discard " + discardPile.size()
                    + "    Rewards " + selectedRewards
                    + "    Cards Played " + playedCards);

            handPanel.removeAll();

            for (Card card : hand) {
                JButton cardButton = new JButton(buildCardText(card));
                cardButton.addActionListener(event -> playCard(card));
                cardButton.setEnabled(gameActive && !hasRewardChoices && player.canSpendEnergy(card.getCost()));
                styleButton(cardButton, colorForElement(card.getElement()));
                handPanel.add(cardButton);
            }

            endTurnButton.setEnabled(gameActive && !hasRewardChoices);

            handPanel.revalidate();
            handPanel.repaint();
        }

        private void playCard(Card card) {
            Enemy enemy = currentEnemy();

            if (!player.canSpendEnergy(card.getCost())) {
                addLog("Not enough Energy for " + card.getName() + ".");
                return;
            }

            player.spendEnergy(card.getCost());
            hand.remove(card);
            discardPile.add(card);
            playedCards++;

            int finalDamage = card.calculateDamageAgainst(enemy);
            enemy.takeDamage(finalDamage);
            player.gainBlock(card.getBlock());
            player.heal(card.getHeal());

            addLog("Played " + card.getName() + " for " + card.getCost() + " Energy.");

            if (finalDamage > 0) {
                addLog("Dealt " + finalDamage + " damage.");
            }

            if (card.hasAdvantageAgainst(enemy)) {
                addLog("Element advantage: damage increased.");
            }

            if (card.getBlock() > 0) {
                addLog("Gained " + card.getBlock() + " Block.");
            }

            if (card.getHeal() > 0) {
                addLog("Healed " + card.getHeal() + " HP.");
            }

            if (!enemy.isAlive()) {
                addLog(enemy.getName() + " was defeated.");
                handleEnemyDefeated();
            } else if (!hasPlayableCard()) {
                addLog("No playable cards left. Click End Turn to recover Energy.");
            }

            refreshView();
        }

        private void runEnemyTurn() {
            Enemy enemy = currentEnemy();

            if (!player.isAlive() || !enemy.isAlive()) {
                return;
            }

            int damage = enemy.getIntentDamage();
            addLog("Enemy turn: " + enemy.getName() + " uses " + enemy.getIntentDescription() + ".");
            player.takeDamage(damage);
            enemy.finishTurn();

            if (!player.isAlive()) {
                addLog("Game Over.");
                addLog("Defeated enemies: " + currentEnemyIndex + "/" + enemies.size() + ".");
                refreshView();
                return;
            }

            startPlayerTurn(true);
            refreshView();
        }

        private void startPlayerTurn(boolean advanceTurn) {
            if (advanceTurn) {
                turnNumber++;
            }

            discardPile.addAll(hand);
            hand.clear();
            player.startTurn();
            drawCards(4);
            addLog("Player turn starts. Energy restored to " + player.getMaxEnergy() + ".");
        }

        private void drawCards(int amount) {
            for (int i = 0; i < amount; i++) {
                if (drawPile.isEmpty()) {
                    if (discardPile.isEmpty()) {
                        return;
                    }

                    drawPile.addAll(discardPile);
                    discardPile.clear();
                    Collections.shuffle(drawPile);
                    addLog("Discard pile reshuffled into deck.");
                }

                hand.add(drawPile.remove(0));
            }
        }

        private boolean hasPlayableCard() {
            for (Card card : hand) {
                if (player.canSpendEnergy(card.getCost())) {
                    return true;
                }
            }

            return false;
        }

        private void handleEnemyDefeated() {
            currentEnemyIndex++;

            if (currentEnemyIndex >= enemies.size()) {
                addLog("Victory! All enemies were defeated.");
                addLog("Cards played: " + playedCards + ".");
                addLog("Final HP: " + player.getHealth() + "/" + player.getMaxHealth() + ".");
                hand.clear();
                return;
            }

            showRewardChoices();
        }

        private void showRewardChoices() {
            rewardPanel.removeAll();

            for (Card reward : createRewardChoices()) {
                JButton rewardButton = new JButton(buildCardText(reward));
                rewardButton.addActionListener(event -> chooseReward(reward));
                styleButton(rewardButton, colorForElement(reward.getElement()));
                rewardPanel.add(rewardButton);
            }

            rewardPanel.revalidate();
            rewardPanel.repaint();
            addLog("Choose one reward card before the next battle.");
        }

        private void chooseReward(Card reward) {
            discardPile.add(reward);
            selectedRewards++;
            rewardPanel.removeAll();
            rewardPanel.revalidate();
            rewardPanel.repaint();

            startPlayerTurn(true);
            addLog(reward.getName() + " was added to your deck.");
            addLog("Next enemy: " + currentEnemy().getName() + ".");
            refreshView();
        }

        private void restartGame() {
            dispose();
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        }

        private String buildCardText(Card card) {
            return "<html><div style='text-align:center;'>"
                    + "<b>" + card.getName() + "</b><br>"
                    + card.getElement().getDisplayName()
                    + "<br>Cost " + card.getCost()
                    + "<br>Damage " + card.getDamage()
                    + "<br>Block " + card.getBlock()
                    + "<br>Heal " + card.getHeal()
                    + "</div></html>";
        }

        private void stylePanel(JPanel panel, String title) {
            panel.setBackground(PANEL);
            panel.setForeground(TEXT);
            panel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ACCENT),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)),
                    title,
                    0,
                    0,
                    SECTION_FONT,
                    TEXT));
        }

        private void styleScrollPane(JScrollPane scrollPane, String title) {
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(ACCENT),
                    title,
                    0,
                    0,
                    SECTION_FONT,
                    TEXT));
        }

        private void styleStatusLabel(JLabel label) {
            label.setForeground(MUTED_TEXT);
            label.setFont(BODY_FONT);
        }

        private void styleButton(JButton button, Color color) {
            button.setBackground(color);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setFont(CARD_FONT);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.darker()),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        }

        private Color colorForElement(Element element) {
            if (element == Element.FIRE) {
                return FIRE;
            }

            if (element == Element.WATER) {
                return WATER;
            }

            if (element == Element.THUNDER) {
                return THUNDER;
            }

            return DANGER;
        }

        private void addLog(String message) {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }

        private Enemy currentEnemy() {
            return enemies.get(Math.min(currentEnemyIndex, enemies.size() - 1));
        }

        private List<Card> createStarterDeck() {
            List<Card> cards = new ArrayList<>();
            cards.add(new Card("Fire Strike", Element.FIRE, 1, 6, 0, 0));
            cards.add(new Card("Fire Strike", Element.FIRE, 1, 6, 0, 0));
            cards.add(new Card("Water Shield", Element.WATER, 1, 0, 6, 0));
            cards.add(new Card("Water Shield", Element.WATER, 1, 0, 6, 0));
            cards.add(new Card("Thunder Bolt", Element.THUNDER, 2, 10, 0, 0));
            cards.add(new Card("River Blade", Element.WATER, 1, 4, 2, 0));
            cards.add(new Card("Quick Spark", Element.THUNDER, 0, 3, 0, 0));
            cards.add(new Card("Healing Rain", Element.WATER, 1, 0, 2, 5));
            Collections.shuffle(cards);
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
                rewards.add(new Card("Healing Rain", Element.WATER, 1, 0, 2, 5));
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
