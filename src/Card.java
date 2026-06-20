public class Card {
    // 卡牌的名称、伤害、格挡值和能量费用
    private String name;
    private int damage;
    private int block;
    private int cost;

    // 创建卡牌并设置初始数据
    public Card(String cardName, int cardDamage, int cardBlock, int cardCost) {
        name = cardName;
        damage = cardDamage;
        block = cardBlock;
        cost = cardCost;
    }

    // 获取卡牌名称
    public String getName() {
        return name;
    }

    // 获取卡牌伤害
    public int getDamage() {
        return damage;
    }

    // 获取卡牌提供的格挡值
    public int getBlock() {
        return block;
    }

    // 获取卡牌费用
    public int getCost() {
        return cost;
    }
}
