package battle;

import model.Card;
import model.Enemy;
import model.Player;

import java.util.ArrayList;
import java.util.List;

public final class BattleActions {
    private BattleActions() {
    }

    public static CardPlayResult playCard(GameState state, Card card, Enemy enemy) {
        Player player = state.getPlayer();
        List<String> logLines = new ArrayList<>();

        if (!player.canSpendEnergy(card.getCost())) {
            logLines.add("Not enough Energy for " + card.getName() + ".");
            return new CardPlayResult(
                    false,
                    card.getName(),
                    card.getCost(),
                    0,
                    false,
                    0,
                    0,
                    logLines);
        }

        player.spendEnergy(card.getCost());
        state.getDeckManager().moveFromHandToDiscard(card);
        state.recordPlayedCard();

        int finalDamage = card.calculateDamageAgainst(enemy);
        boolean elementAdvantage = card.hasAdvantageAgainst(enemy);
        enemy.takeDamage(finalDamage);
        player.gainBlock(card.getBlock());
        player.heal(card.getHeal());

        logLines.add("Played " + card.getName() + " for " + card.getCost() + " Energy.");

        if (finalDamage > 0) {
            logLines.add("Dealt " + finalDamage + " damage.");
        }

        if (elementAdvantage) {
            logLines.add("Element advantage: damage increased.");
        }

        if (card.getBlock() > 0) {
            logLines.add("Gained " + card.getBlock() + " Block.");
        }

        if (card.getHeal() > 0) {
            logLines.add("Healed " + card.getHeal() + " HP.");
        }

        return new CardPlayResult(
                true,
                card.getName(),
                card.getCost(),
                finalDamage,
                elementAdvantage,
                card.getBlock(),
                card.getHeal(),
                logLines);
    }
}
