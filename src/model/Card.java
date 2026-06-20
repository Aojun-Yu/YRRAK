package model;

public class Card {
    // 卡牌基础数据：名称、元素、费用、伤害、格挡和治疗
    private final String name;
    private final Element element;
    private final int cost;
    private final int damage;
    private final int block;
    private final int heal;

    public Card(String name, Element element, int cost, int damage, int block, int heal) {
        this.name = name;
        this.element = element;
        this.cost = cost;
        this.damage = damage;
        this.block = block;
        this.heal = heal;
    }

    // 根据元素克制关系计算最终伤害
    public int calculateDamageAgainst(Enemy enemy) {
        if (damage <= 0) {
            return 0;
        }

        if (element.isStrongAgainst(enemy.getElement())) {
            return damage + damage / 2;
        }

        return damage;
    }

    public boolean hasAdvantageAgainst(Enemy enemy) {
        return damage > 0 && element.isStrongAgainst(enemy.getElement());
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public int getCost() {
        return cost;
    }

    public int getDamage() {
        return damage;
    }

    public int getBlock() {
        return block;
    }

    public int getHeal() {
        return heal;
    }
}
