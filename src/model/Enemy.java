package model;

public class Enemy {
    private final String name;
    private final Element element;
    private final int maxHealth;
    private final int attackDamage;
    private int health;
    private int turnCount;

    public Enemy(String name, Element element, int maxHealth, int attackDamage) {
        this.name = name;
        this.element = element;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.health = maxHealth;
        this.turnCount = 1;
    }

    public void takeDamage(int damage) {
        health = health - damage;

        if (health < 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    // 描述敌人下一回合的行动，方便玩家提前规划
    public String getIntentDescription() {
        if (name.equals("Fire Spirit") && turnCount % 3 == 0) {
            return "Flame Burst for " + getIntentDamage() + " damage";
        }

        if (name.equals("Thunder Beast") && turnCount % 2 == 0) {
            return "Charged Strike for " + getIntentDamage() + " damage";
        }

        return "Attack for " + getIntentDamage() + " damage";
    }

    // 根据敌人的回合数计算本回合实际伤害
    public int getIntentDamage() {
        if (name.equals("Fire Spirit") && turnCount % 3 == 0) {
            return attackDamage + 4;
        }

        if (name.equals("Thunder Beast") && turnCount % 2 == 0) {
            return attackDamage + 3;
        }

        if (name.equals("Thunder Beast")) {
            return attackDamage - 1;
        }

        return attackDamage;
    }

    public void finishTurn() {
        turnCount++;
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackDamage() {
        return attackDamage;
    }
}
