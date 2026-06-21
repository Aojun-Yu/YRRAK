package battle;

import model.Card;
import model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    private final List<Card> drawPile;
    private final List<Card> hand;
    private final List<Card> discardPile;
    private final boolean shuffleOnRecycle;
    private boolean discardRecycledLastDraw;

    public DeckManager(List<Card> starterDeck, boolean shuffleDeck) {
        drawPile = new ArrayList<>(starterDeck);
        hand = new ArrayList<>();
        discardPile = new ArrayList<>();
        shuffleOnRecycle = shuffleDeck;

        if (shuffleDeck) {
            Collections.shuffle(drawPile);
        }
    }

    public void startTurn(int cardsToDraw) {
        discardHand();
        drawCards(cardsToDraw);
    }

    public int drawCards(int amount) {
        discardRecycledLastDraw = false;
        int drawnCards = 0;

        for (int i = 0; i < amount; i++) {
            if (drawPile.isEmpty()) {
                recycleDiscardPile();
            }

            if (drawPile.isEmpty()) {
                return drawnCards;
            }

            hand.add(drawPile.remove(0));
            drawnCards++;
        }

        return drawnCards;
    }

    public void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    public void moveFromHandToDiscard(Card card) {
        if (hand.remove(card)) {
            discardPile.add(card);
        }
    }

    public void addToDiscard(Card card) {
        discardPile.add(card);
    }

    public boolean hasPlayableCard(Player player) {
        for (Card card : hand) {
            if (player.canSpendEnergy(card.getCost())) {
                return true;
            }
        }

        return false;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public int getDrawPileSize() {
        return drawPile.size();
    }

    public int getHandSize() {
        return hand.size();
    }

    public int getDiscardPileSize() {
        return discardPile.size();
    }

    public boolean wasDiscardRecycledLastDraw() {
        return discardRecycledLastDraw;
    }

    private void recycleDiscardPile() {
        if (discardPile.isEmpty()) {
            return;
        }

        drawPile.addAll(discardPile);
        discardPile.clear();
        if (shuffleOnRecycle) {
            Collections.shuffle(drawPile);
        }
        discardRecycledLastDraw = true;
    }
}
