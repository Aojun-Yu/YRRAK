public class Enemy {
    private String name;
    private int health;
    private int attackDamage;

    public Enemy(String enemyName, int enemyHealth, int enemyAttackDamage) {
        name = enemyName;
        health = enemyHealth;
        attackDamage = enemyAttackDamage;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    // 让敌人受到伤害
    public void takeDamage(int damage) {
        health = health - damage;
        if (health < 0) {
            health = 0;
        }
    }

    // 判断敌人是否仍然存活
    public boolean isAlive() {
        return health > 0;
    }
}
