package battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardPlayResult {
    private final boolean success;
    private final String cardName;
    private final int energyCost;
    private final int damageDealt;
    private final boolean elementAdvantage;
    private final int blockGained;
    private final int healthHealed;
    private final List<String> logLines;

    public CardPlayResult(
            boolean success,
            String cardName,
            int energyCost,
            int damageDealt,
            boolean elementAdvantage,
            int blockGained,
            int healthHealed,
            List<String> logLines) {
        this.success = success;
        this.cardName = cardName;
        this.energyCost = energyCost;
        this.damageDealt = damageDealt;
        this.elementAdvantage = elementAdvantage;
        this.blockGained = blockGained;
        this.healthHealed = healthHealed;
        this.logLines = Collections.unmodifiableList(new ArrayList<>(logLines));
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCardName() {
        return cardName;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public boolean hasElementAdvantage() {
        return elementAdvantage;
    }

    public int getBlockGained() {
        return blockGained;
    }

    public int getHealthHealed() {
        return healthHealed;
    }

    public List<String> getLogLines() {
        return logLines;
    }
}
