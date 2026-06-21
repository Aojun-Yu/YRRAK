package battle;

import model.Enemy;
import model.Player;

import java.util.List;

public class GameState {
    private final Player player;
    private final List<Enemy> enemies;
    private final DeckManager deckManager;
    private int currentEnemyIndex;
    private int selectedRewards;
    private int playedCards;
    private int turnNumber;

    public GameState(boolean shuffleStarterDeck) {
        player = new Player(30, 3);
        enemies = GameContent.createEnemies();
        deckManager = new DeckManager(GameContent.createStarterDeck(), shuffleStarterDeck);
        currentEnemyIndex = 0;
        selectedRewards = 0;
        playedCards = 0;
        turnNumber = 1;
    }

    public Player getPlayer() {
        return player;
    }

    public DeckManager getDeckManager() {
        return deckManager;
    }

    public Enemy currentEnemy() {
        return enemies.get(Math.min(currentEnemyIndex, enemies.size() - 1));
    }

    public void advanceEnemy() {
        currentEnemyIndex++;
    }

    public void advanceTurn() {
        turnNumber++;
    }

    public void recordPlayedCard() {
        playedCards++;
    }

    public void recordSelectedReward() {
        selectedRewards++;
    }

    public boolean isVictory() {
        return currentEnemyIndex >= enemies.size();
    }

    public boolean isBattleActive() {
        return player.isAlive() && !isVictory() && currentEnemy().isAlive();
    }

    public int getEnemyCount() {
        return enemies.size();
    }

    public int getCurrentEnemyIndex() {
        return currentEnemyIndex;
    }

    public int getSelectedRewards() {
        return selectedRewards;
    }

    public int getPlayedCards() {
        return playedCards;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}
