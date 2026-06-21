package battle;

import model.Card;
import model.Element;
import model.Enemy;

import java.util.ArrayList;
import java.util.List;

public final class GameContent {
    private GameContent() {
    }

    public static List<Card> createStarterDeck() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card("Fire Strike", Element.FIRE, 1, 6, 0, 0));
        cards.add(new Card("Water Shield", Element.WATER, 1, 0, 5, 0));
        cards.add(new Card("Thunder Bolt", Element.THUNDER, 2, 10, 0, 0));
        cards.add(new Card("River Blade", Element.WATER, 1, 4, 2, 0));
        cards.add(new Card("Storm Guard", Element.THUNDER, 2, 5, 5, 0));
        cards.add(new Card("Ember Guard", Element.FIRE, 1, 3, 3, 0));
        cards.add(new Card("Rain Lance", Element.WATER, 2, 8, 0, 0));
        cards.add(new Card("Spark Step", Element.THUNDER, 1, 4, 1, 0));
        return cards;
    }

    public static List<Enemy> createEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy("Training Dummy", Element.NONE, 18, 4));
        enemies.add(new Enemy("Fire Spirit", Element.FIRE, 20, 5));
        enemies.add(new Enemy("Thunder Beast", Element.THUNDER, 24, 6));
        return enemies;
    }

    public static List<Card> createRewardChoices(int selectedRewards) {
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
