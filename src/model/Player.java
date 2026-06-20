package model;

public class Player {
    // 玩家核心状态
    private final int maxHealth;
    private final int maxEnergy;
    private int health;
    private int energy;
    private int block;

    public Player(int maxHealth, int maxEnergy) {
        this.maxHealth = maxHealth;
        this.maxEnergy = maxEnergy;
        this.health = maxHealth;
        this.energy = maxEnergy;
        this.block = 0;
    }

    // 新回合开始时恢复能量，并清空上一回合剩余护甲
    public void startTurn() {
        energy = maxEnergy;
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

    public boolean canSpendEnergy(int amount) {
        return energy >= amount;
    }

    public void spendEnergy(int amount) {
        energy = energy - amount;
    }

    public void gainBlock(int amount) {
        block = block + amount;
    }

    // 恢复生命值，但不能超过最大生命值
    public void heal(int amount) {
        health = health + amount;

        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getEnergy() {
        return energy;
    }

    public int getBlock() {
        return block;
    }
}
