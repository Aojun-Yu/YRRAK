public class Player {
    // 玩家的当前生命值、能量和格挡值
    private int health;
    private int energy;
    private int block;

    // 创建玩家并设置初始状态
    public Player(int playerHealth, int playerEnergy) {
        health = playerHealth;
        energy = playerEnergy;
        block = 0;
    }

    // 让格挡优先吸收伤害，剩余伤害再扣除生命值
    public void takeDamage(int damage) {
        int blockedDamage = Math.min(block, damage);
        block = block - blockedDamage;

        int remainingDamage = damage - blockedDamage;
        health = health - remainingDamage;

        if (health < 0) {
            health = 0;
        }
    }

    // 获取玩家生命值
    public int getHealth() {
        return health;
    }

    // 获取玩家当前能量
    public int getEnergy() {
        return energy;
    }

    // 增加玩家的格挡值
    public void gainBlock(int amount) {
        block = block + amount;
    }

    // 获取玩家当前格挡值
    public int getBlock() {
        return block;
    }

    // 判断玩家是否仍然存活
    public boolean isAlive() {
        return health > 0;
    }

    // 消耗能量
    public void spendEnergy(int amount) {
        energy = energy - amount;
    }

    // 将玩家能量恢复到指定数值
    public void restoreEnergy(int amount) {
        energy = amount;
    }
}
