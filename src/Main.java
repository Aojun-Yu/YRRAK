import battle.BattleManager;
import util.ConsoleGameIO;

public class Main {
    public static void main(String[] args) {
        BattleManager battleManager = new BattleManager(new ConsoleGameIO());
        battleManager.runGame();
    }
}
