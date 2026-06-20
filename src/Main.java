public class Main {
    public static void main(String[] args) {
        // 创建一个敌人对象
        Enemy enemy = new Enemy("Training Dummy", 20, 5);
        // 显示敌人的名字和生命值
        System.out.println(enemy.getName());
        System.out.println(enemy.getHealth());

        Card attackCard = new Card("Overload Spark", 3, 0, 1);
        Card defenseCard = new Card("Static Barrier", 0, 4, 1);

        Player player = new Player(30, 3);

        playCard(attackCard, player, enemy);
        playCard(defenseCard, player, enemy);

        // 敌人的回合：攻击玩家
        if (enemy.isAlive()) {
            player.takeDamage(enemy.getAttackDamage());

            System.out.println(enemy.getName() + " attacks for " + enemy.getAttackDamage() + " damage.");
        } else {
            System.out.println(enemy.getName() + " has been defeated.");
        }

        if (player.isAlive()) {
            System.out.println("Player survived the attack.");
        } else {
            System.out.println("Player has been defeated.");
        }

        // 模拟下一个玩家回合
        player.restoreEnergy(3);
        System.out.println("A new turn begins.");

        System.out.println("Player health: " + player.getHealth());
        System.out.println("Player block: " + player.getBlock());
        System.out.println("Player energy: " + player.getEnergy());
        System.out.println("Enemy health: " + enemy.getHealth());
    }

    // 检查能量并执行卡牌的伤害和格挡效果
    public static void playCard(Card card, Player player, Enemy enemy) {
        if (player.getEnergy() >= card.getCost()) {
            player.spendEnergy(card.getCost());
            enemy.takeDamage(card.getDamage());
            player.gainBlock(card.getBlock());

            System.out.println("Card played: " + card.getName());
        } else {
            System.out.println("Not enough energy!");
        }
    }
}
