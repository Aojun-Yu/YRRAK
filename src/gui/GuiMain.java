package gui;

import battle.DeckManager;
import battle.GameContent;
import battle.GameState;
import model.Card;
import model.Element;
import model.Enemy;
import model.Player;

import javax.swing.*;
import java.awt.*;

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
        private static final int WINDOW_WIDTH = 980;
        private static final int WINDOW_HEIGHT = 680;
        private static final int CARDS_PER_TURN = 4;
        private static final int HAND_COLUMNS = 4;
        private static final int REWARD_COLUMNS = 3;

        private final GameState state;
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

        GameFrame() {
            state = new GameState(true);

            titleLabel = new JLabel();
            playerStatusLabel = new JLabel();
            enemyStatusLabel = new JLabel();
            intentLabel = new JLabel();
            deckStatusLabel = new JLabel();
            logArea = new JTextArea(12, 48);
            handPanel = new JPanel(new GridLayout(0, HAND_COLUMNS, 10, 10));
            rewardPanel = new JPanel(new GridLayout(0, REWARD_COLUMNS, 10, 10));
            endTurnButton = new JButton("End Turn");
            restartButton = new JButton("Restart");

            setTitle("YRRAK - Element Card Battle");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            getContentPane().setBackground(BACKGROUND);

            buildLayout();
            addLog("Goal: defeat 3 enemies.");
            addLog("Rule: click End Turn to let the enemy act, then your Energy returns to 3.");
            addLog("Current enemy: " + state.currentEnemy().getName() + ".");
            startPlayerTurn(false);
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
            endTurnButton.setToolTipText("Let the enemy act, then draw a new hand.");
            restartButton.setToolTipText("Start a fresh run.");
            rewardPanel.setVisible(false);

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
            Enemy enemy = state.currentEnemy();
            boolean hasRewardChoices = rewardPanel.getComponentCount() > 0;
            boolean gameActive = state.isBattleActive();

            updateStatusLabels(enemy);
            rebuildHandPanel(gameActive, hasRewardChoices);
            rewardPanel.setVisible(hasRewardChoices);
            endTurnButton.setEnabled(gameActive && !hasRewardChoices);
            endTurnButton.setToolTipText(buildEndTurnTooltip(gameActive, hasRewardChoices));

            handPanel.revalidate();
            handPanel.repaint();
            rewardPanel.revalidate();
            rewardPanel.repaint();
        }

        private void updateStatusLabels(Enemy enemy) {
            Player player = state.getPlayer();
            DeckManager deckManager = state.getDeckManager();

            if (state.isVictory()) {
                titleLabel.setText("YRRAK - Victory");
                enemyStatusLabel.setText("Enemy  All enemies defeated");
                intentLabel.setText("Enemy Intent  None");
            } else if (!player.isAlive()) {
                titleLabel.setText("YRRAK - Game Over");
                enemyStatusLabel.setText("Enemy  " + enemy.getName()
                        + "    HP " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                        + "    Element " + enemy.getElement().getDisplayName());
                intentLabel.setText("Enemy Intent  Run ended");
            } else {
                titleLabel.setText("YRRAK Battle " + (state.getCurrentEnemyIndex() + 1)
                        + "/" + state.getEnemyCount() + " - Turn " + state.getTurnNumber());
                enemyStatusLabel.setText("Enemy  " + enemy.getName()
                        + "    HP " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                        + "    Element " + enemy.getElement().getDisplayName());
                intentLabel.setText("Enemy Intent  " + enemy.getIntentDescription());
            }

            playerStatusLabel.setText("Player  HP " + player.getHealth() + "/" + player.getMaxHealth()
                    + "    Block " + player.getBlock()
                    + "    Energy " + player.getEnergy() + "/" + player.getMaxEnergy());
            deckStatusLabel.setText("Deck " + deckManager.getDrawPileSize()
                    + "    Hand " + deckManager.getHandSize()
                    + "    Discard " + deckManager.getDiscardPileSize()
                    + "    Rewards " + state.getSelectedRewards()
                    + "    Cards Played " + state.getPlayedCards());
        }

        private void rebuildHandPanel(boolean gameActive, boolean hasRewardChoices) {
            handPanel.removeAll();

            for (Card card : state.getDeckManager().getHand()) {
                handPanel.add(createHandButton(card, gameActive, hasRewardChoices));
            }
        }

        private JButton createHandButton(Card card, boolean gameActive, boolean hasRewardChoices) {
            Player player = state.getPlayer();
            boolean hasEnergy = player.canSpendEnergy(card.getCost());
            boolean enabled = gameActive && !hasRewardChoices && hasEnergy;
            JButton cardButton = createCardButton(card, enabled);
            cardButton.addActionListener(event -> playCard(card));

            if (!gameActive) {
                cardButton.setToolTipText("This battle is over.");
            } else if (hasRewardChoices) {
                cardButton.setToolTipText("Choose a reward before playing more cards.");
            } else if (!hasEnergy) {
                cardButton.setToolTipText("Need " + card.getCost() + " Energy. You have " + player.getEnergy() + ".");
            }

            return cardButton;
        }

        private String buildEndTurnTooltip(boolean gameActive, boolean hasRewardChoices) {
            if (!gameActive) {
                return "Start a new run or close the window.";
            }

            if (hasRewardChoices) {
                return "Choose a reward before ending the turn.";
            }

            return "Let the enemy act, then draw a new hand.";
        }

        private void playCard(Card card) {
            Player player = state.getPlayer();
            Enemy enemy = state.currentEnemy();
            DeckManager deckManager = state.getDeckManager();

            if (!player.canSpendEnergy(card.getCost())) {
                addLog("Not enough Energy for " + card.getName() + ".");
                return;
            }

            player.spendEnergy(card.getCost());
            deckManager.moveFromHandToDiscard(card);
            state.recordPlayedCard();

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
            Player player = state.getPlayer();
            Enemy enemy = state.currentEnemy();

            if (!player.isAlive() || !enemy.isAlive()) {
                return;
            }

            int damage = enemy.getIntentDamage();
            addLog("Enemy turn: " + enemy.getName() + " uses " + enemy.getIntentDescription() + ".");
            player.takeDamage(damage);
            enemy.finishTurn();

            if (!player.isAlive()) {
                addLog("Game Over.");
                addLog("Defeated enemies: " + state.getCurrentEnemyIndex() + "/" + state.getEnemyCount() + ".");
                refreshView();
                return;
            }

            startPlayerTurn(true);
            refreshView();
        }

        private void startPlayerTurn(boolean advanceTurn) {
            if (advanceTurn) {
                state.advanceTurn();
            }

            Player player = state.getPlayer();
            DeckManager deckManager = state.getDeckManager();
            player.startTurn();
            deckManager.startTurn(CARDS_PER_TURN);

            if (deckManager.wasDiscardRecycledLastDraw()) {
                addLog("Discard pile reshuffled into deck.");
            }

            addLog("Player turn starts. Energy restored to " + player.getMaxEnergy() + ".");
        }

        private boolean hasPlayableCard() {
            return state.getDeckManager().hasPlayableCard(state.getPlayer());
        }

        private void handleEnemyDefeated() {
            state.advanceEnemy();

            if (state.isVictory()) {
                addLog("Victory! All enemies were defeated.");
                addLog("Cards played: " + state.getPlayedCards() + ".");
                addLog("Final HP: " + state.getPlayer().getHealth() + "/" + state.getPlayer().getMaxHealth() + ".");
                state.getDeckManager().discardHand();
                return;
            }

            showRewardChoices();
        }

        private void showRewardChoices() {
            rewardPanel.removeAll();

            for (Card reward : GameContent.createRewardChoices(state.getSelectedRewards())) {
                rewardPanel.add(createRewardButton(reward));
            }

            rewardPanel.setVisible(true);
            rewardPanel.revalidate();
            rewardPanel.repaint();
            addLog("Choose one reward card before the next battle.");
        }

        private void chooseReward(Card reward) {
            state.getDeckManager().addToDiscard(reward);
            state.recordSelectedReward();
            rewardPanel.removeAll();
            rewardPanel.setVisible(false);
            rewardPanel.revalidate();
            rewardPanel.repaint();

            addLog(reward.getName() + " was added to your deck.");
            addLog("Next enemy: " + state.currentEnemy().getName() + ".");
            startPlayerTurn(true);
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

        private JButton createRewardButton(Card card) {
            JButton rewardButton = createCardButton(card, true);
            rewardButton.addActionListener(event -> chooseReward(card));
            rewardButton.setToolTipText("Add " + card.getName() + " to your deck.");
            return rewardButton;
        }

        private JButton createCardButton(Card card, boolean enabled) {
            JButton button = new JButton(buildCardText(card));
            button.setEnabled(enabled);
            button.setToolTipText(buildCardSummary(card));
            button.setPreferredSize(new Dimension(150, 112));
            styleButton(button, colorForElement(card.getElement()));
            return button;
        }

        private String buildCardSummary(Card card) {
            return card.getName()
                    + " | " + card.getElement().getDisplayName()
                    + " | Cost " + card.getCost()
                    + " | Damage " + card.getDamage()
                    + " | Block " + card.getBlock()
                    + " | Heal " + card.getHeal();
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

    }
}
