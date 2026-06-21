package gui;

import battle.DeckManager;
import battle.BattleActions;
import battle.CardPlayResult;
import battle.GameContent;
import battle.GameState;
import model.Card;
import model.Element;
import model.Enemy;
import model.Player;

import javax.swing.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

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
        private static final Color ACCENT = new Color(112, 210, 255);
        private static final Color DANGER = new Color(255, 110, 110);
        private static final Color FIRE = new Color(150, 60, 36);
        private static final Color WATER = new Color(35, 92, 150);
        private static final Color THUNDER = new Color(150, 118, 24);
        private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
        private static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, 14);
        private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);
        private static final Font CARD_FONT = new Font("SansSerif", Font.BOLD, 12);
        private static final int WINDOW_WIDTH = 980;
        private static final int WINDOW_HEIGHT = 680;
        private static final int CARDS_PER_TURN = 4;
        private static final int HAND_COLUMNS = 4;
        private static final int REWARD_COLUMNS = 3;
        private static final int ICON_SIZE = 28;
        private static final int PORTRAIT_SIZE = 132;

        private final GameState state;
        private final BackgroundPanel rootPanel;
        private final JLabel titleLabel;
        private final JLabel playerArtLabel;
        private final JLabel enemyArtLabel;
        private final JLabel playerStatusLabel;
        private final JLabel enemyStatusLabel;
        private final JLabel intentLabel;
        private final JLabel deckStatusLabel;
        private final JTextArea logArea;
        private final JPanel handPanel;
        private final JPanel rewardPanel;
        private final JButton endTurnButton;
        private final JButton restartButton;
        private final SoundPlayer soundPlayer;

        GameFrame() {
            state = new GameState(true);
            soundPlayer = new SoundPlayer();
            rootPanel = new BackgroundPanel("assets/ui/background.png");

            titleLabel = new JLabel();
            playerArtLabel = new JLabel();
            enemyArtLabel = new JLabel();
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
            setContentPane(rootPanel);

            buildLayout();
            soundPlayer.startMusic();
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    soundPlayer.stopMusic();
                }
            });
            addLog("Goal: defeat 3 enemies.");
            addLog("Rule: click End Turn to let the enemy act, then your Energy returns to 3.");
            addLog("Current enemy: " + state.currentEnemy().getName() + ".");
            startPlayerTurn(false);
            refreshView();
            pack();
            setLocationRelativeTo(null);
        }

        private void buildLayout() {
            rootPanel.setLayout(new BorderLayout(14, 14));
            rootPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

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

            JPanel artPanel = new JPanel(new GridLayout(1, 2, 12, 12));
            stylePanel(artPanel, "Characters");
            styleArtLabel(playerArtLabel);
            styleArtLabel(enemyArtLabel);
            artPanel.add(playerArtLabel);
            artPanel.add(enemyArtLabel);

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
            JPanel topPanel = new JPanel(new GridLayout(1, 2, 12, 12));
            topPanel.setBackground(BACKGROUND);
            topPanel.add(statusPanel);
            topPanel.add(artPanel);
            centerPanel.add(topPanel, BorderLayout.NORTH);
            centerPanel.add(logScrollPane, BorderLayout.CENTER);

            stylePanel(handPanel, "Hand - click a card to play it");
            stylePanel(rewardPanel, "Reward - choose one after winning a battle");

            endTurnButton.addActionListener(event -> runEnemyTurn());
            restartButton.addActionListener(event -> restartGame());
            styleButton(endTurnButton, ACCENT);
            styleButton(restartButton, PANEL);
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
            endTurnButton.setEnabled(true);
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
            updateCharacterArt(enemy);
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
            boolean playable = gameActive && !hasRewardChoices && hasEnergy;
            JButton cardButton = createCardButton(card, playable);
            cardButton.addActionListener(event -> {
                if (!gameActive) {
                    addLog("This battle is over.");
                    soundPlayer.playError();
                    return;
                }

                if (hasRewardChoices) {
                    addLog("Choose a reward before playing more cards.");
                    soundPlayer.playError();
                    return;
                }

                if (!hasEnergy) {
                    addLog("Not enough Energy for " + card.getName() + ".");
                    soundPlayer.playError();
                    return;
                }

                playCard(card);
            });

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
            Enemy enemy = state.currentEnemy();
            CardPlayResult result = BattleActions.playCard(state, card, enemy);

            for (String line : result.getLogLines()) {
                addLog(line);
            }

            if (!result.isSuccess()) {
                soundPlayer.playError();
                refreshView();
                return;
            }

            soundPlayer.playCard(card.getElement());

            if (result.hasElementAdvantage()) {
                soundPlayer.playAdvantage();
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
                soundPlayer.playError();
                return;
            }

            int damage = enemy.getIntentDamage();
            addLog("Enemy turn: " + enemy.getName() + " uses " + enemy.getIntentDescription() + ".");
            soundPlayer.playEnemyAttack();
            player.takeDamage(damage);
            enemy.finishTurn();

            if (!player.isAlive()) {
                addLog("Game Over.");
                addLog("Defeated enemies: " + state.getCurrentEnemyIndex() + "/" + state.getEnemyCount() + ".");
                soundPlayer.playGameOver();
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
                soundPlayer.playVictory();
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
            soundPlayer.playReward();
            startPlayerTurn(true);
            refreshView();
        }

        private void restartGame() {
            soundPlayer.stopMusic();
            dispose();
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        }

        private String buildCardText(Card card) {
            return "<html><div style='text-align:center; color:white;'>"
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
            JButton button = new CardButton(buildCardText(card), colorForElement(card.getElement()));
            button.setIcon(loadElementIcon(card.getElement()));
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setEnabled(true);
            if (button instanceof CardButton) {
                ((CardButton) button).setPlayable(enabled);
            }
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
            panel.setOpaque(true);
            panel.setBackground(new Color(PANEL.getRed(), PANEL.getGreen(), PANEL.getBlue(), 225));
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
            label.setForeground(Color.WHITE);
            label.setFont(BODY_FONT);
        }

        private void styleArtLabel(JLabel label) {
            label.setForeground(TEXT);
            label.setFont(BODY_FONT);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setOpaque(true);
            label.setBackground(PANEL_DARK);
            label.setBorder(BorderFactory.createLineBorder(ACCENT.darker()));
        }

        private void styleButton(JButton button, Color color) {
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setOpaque(!(button instanceof CardButton));
            button.setContentAreaFilled(!(button instanceof CardButton));
            button.setFont(CARD_FONT);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.brighter(), 2),
                    BorderFactory.createEmptyBorder(12, 10, 12, 10)));
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

        private void updateCharacterArt(Enemy enemy) {
            playerArtLabel.setText("Player");
            playerArtLabel.setIcon(loadImageOrPlaceholder(
                    "assets/characters/player.png",
                    PORTRAIT_SIZE,
                    PORTRAIT_SIZE,
                    "P",
                    ACCENT));

            enemyArtLabel.setText(enemy.getName());
            enemyArtLabel.setIcon(loadImageOrPlaceholder(
                    "assets/characters/" + assetNameForEnemy(enemy) + ".png",
                    PORTRAIT_SIZE,
                    PORTRAIT_SIZE,
                    initials(enemy.getName()),
                    colorForElement(enemy.getElement())));
        }

        private ImageIcon loadElementIcon(Element element) {
            String name = element.getDisplayName().toLowerCase();
            return loadImageOrPlaceholder(
                    "assets/icons/" + name + ".png",
                    ICON_SIZE,
                    ICON_SIZE,
                    elementInitial(element),
                    colorForElement(element));
        }

        private ImageIcon loadImageOrPlaceholder(String path, int width, int height, String label, Color color) {
            File file = new File(path);

            if (file.isFile()) {
                ImageIcon icon = new ImageIcon(path);
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }

            return new ImageIcon(createPlaceholderImage(width, height, label, color));
        }

        private Image createPlaceholderImage(int width, int height, String label, Color color) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(PANEL_DARK);
            graphics.fillRoundRect(0, 0, width, height, 18, 18);
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(3));
            graphics.drawRoundRect(2, 2, width - 5, height - 5, 18, 18);
            graphics.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, height / 4)));
            FontMetrics metrics = graphics.getFontMetrics();
            int textX = (width - metrics.stringWidth(label)) / 2;
            int textY = (height - metrics.getHeight()) / 2 + metrics.getAscent();
            graphics.drawString(label, textX, textY);
            graphics.dispose();
            return image;
        }

        private String assetNameForEnemy(Enemy enemy) {
            return enemy.getName().toLowerCase().replace(" ", "_");
        }

        private String initials(String text) {
            String[] words = text.split(" ");
            StringBuilder builder = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    builder.append(word.charAt(0));
                }
            }

            return builder.toString();
        }

        private String elementInitial(Element element) {
            if (element == Element.NONE) {
                return "-";
            }

            return element.getDisplayName().substring(0, 1);
        }

    }

    private static class CardButton extends JButton {
        private final Color elementColor;
        private final Image frameImage;
        private boolean playable;

        CardButton(String text, Color elementColor) {
            super(text);
            this.elementColor = elementColor;
            this.playable = true;
            File frameFile = new File("assets/ui/card_frame.png");
            if (frameFile.isFile()) {
                frameImage = new ImageIcon("assets/ui/card_frame.png").getImage();
            } else {
                frameImage = null;
            }
        }

        public void setPlayable(boolean playable) {
            this.playable = playable;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            Color top = new Color(elementColor.getRed(), elementColor.getGreen(), elementColor.getBlue(), 210);
            Color bottom = new Color(12, 15, 27, 240);
            GradientPaint paint = new GradientPaint(0, 0, top, 0, height, bottom);
            graphics2D.setPaint(paint);
            graphics2D.fillRoundRect(2, 2, width - 4, height - 4, 18, 18);

            if (frameImage != null) {
                graphics2D.drawImage(frameImage, 0, 0, width, height, this);
            }

            graphics2D.setColor(elementColor.brighter());
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.drawRoundRect(3, 3, width - 7, height - 7, 18, 18);

            if (!playable) {
                graphics2D.setColor(new Color(0, 0, 0, 95));
                graphics2D.fillRoundRect(2, 2, width - 4, height - 4, 18, 18);
            }

            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class BackgroundPanel extends JPanel {
        private final Image image;

        BackgroundPanel(String path) {
            File file = new File(path);

            if (file.isFile()) {
                image = new ImageIcon(path).getImage();
            } else {
                image = null;
                setBackground(new Color(12, 15, 27));
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (image != null) {
                graphics.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private static class SoundPlayer {
        private static final float SAMPLE_RATE = 44100f;
        private volatile boolean musicPlaying;
        private Thread musicThread;

        public void startMusic() {
            if (musicPlaying) {
                return;
            }

            musicPlaying = true;
            musicThread = new Thread(this::runMusicLoop);
            musicThread.setDaemon(true);
            musicThread.start();
        }

        public void stopMusic() {
            musicPlaying = false;
        }

        public void playCard(Element element) {
            if (element == Element.FIRE) {
                playSequence(new Tone(392, 55, 0.22), new Tone(523, 70, 0.20));
            } else if (element == Element.WATER) {
                playSequence(new Tone(330, 70, 0.20), new Tone(440, 80, 0.18));
            } else if (element == Element.THUNDER) {
                playSequence(new Tone(659, 45, 0.18), new Tone(784, 55, 0.16));
            } else {
                playSequence(new Tone(440, 70, 0.18));
            }
        }

        public void playAdvantage() {
            playSequence(new Tone(740, 45, 0.16), new Tone(988, 70, 0.16));
        }

        public void playEnemyAttack() {
            playSequence(new Tone(180, 90, 0.22), new Tone(120, 80, 0.18));
        }

        public void playReward() {
            playSequence(new Tone(523, 70, 0.18), new Tone(659, 70, 0.18), new Tone(784, 100, 0.18));
        }

        public void playVictory() {
            playSequence(new Tone(523, 90, 0.18), new Tone(659, 90, 0.18), new Tone(784, 90, 0.18),
                    new Tone(1046, 160, 0.16));
        }

        public void playGameOver() {
            playSequence(new Tone(220, 120, 0.20), new Tone(165, 140, 0.18), new Tone(110, 180, 0.16));
        }

        public void playError() {
            playSequence(new Tone(120, 80, 0.14));
        }

        private void playSequence(Tone... tones) {
            Thread soundThread = new Thread(() -> {
                try {
                    AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
                    SourceDataLine line = AudioSystem.getSourceDataLine(format);
                    line.open(format);
                    line.start();

                    for (Tone tone : tones) {
                        writeTone(line, tone);
                    }

                    line.drain();
                    line.close();
                } catch (Exception exception) {
                    // 音频设备不可用时直接忽略，避免影响游戏运行。
                }
            });

            soundThread.setDaemon(true);
            soundThread.start();
        }

        private void runMusicLoop() {
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                Tone[] melody = {
                        new Tone(220, 420, 0.055),
                        new Tone(262, 420, 0.050),
                        new Tone(330, 420, 0.045),
                        new Tone(294, 420, 0.045),
                        new Tone(196, 420, 0.050),
                        new Tone(247, 420, 0.045),
                        new Tone(330, 420, 0.045),
                        new Tone(392, 620, 0.040)
                };

                while (musicPlaying) {
                    for (Tone tone : melody) {
                        if (!musicPlaying) {
                            break;
                        }

                        writeTone(line, tone);
                        writeSilence(line, 80);
                    }
                }

                line.drain();
                line.close();
            } catch (Exception exception) {
                musicPlaying = false;
                // 音频设备不可用时直接忽略，避免影响游戏运行。
            }
        }

        private void writeTone(SourceDataLine line, Tone tone) {
            int sampleCount = (int) (SAMPLE_RATE * tone.durationMs / 1000);
            byte[] data = new byte[sampleCount];

            for (int i = 0; i < sampleCount; i++) {
                double angle = 2.0 * Math.PI * i * tone.frequency / SAMPLE_RATE;
                double fade = calculateFade(i, sampleCount);
                data[i] = (byte) (Math.sin(angle) * 127 * tone.volume * fade);
            }

            line.write(data, 0, data.length);
        }

        private void writeSilence(SourceDataLine line, int durationMs) {
            int sampleCount = (int) (SAMPLE_RATE * durationMs / 1000);
            byte[] data = new byte[sampleCount];
            line.write(data, 0, data.length);
        }

        private double calculateFade(int index, int sampleCount) {
            int fadeSamples = Math.min(800, sampleCount / 3);

            if (fadeSamples <= 0) {
                return 1.0;
            }

            if (index < fadeSamples) {
                return index / (double) fadeSamples;
            }

            if (index > sampleCount - fadeSamples) {
                return (sampleCount - index) / (double) fadeSamples;
            }

            return 1.0;
        }
    }

    private static class Tone {
        private final int frequency;
        private final int durationMs;
        private final double volume;

        Tone(int frequency, int durationMs, double volume) {
            this.frequency = frequency;
            this.durationMs = durationMs;
            this.volume = volume;
        }
    }
}
