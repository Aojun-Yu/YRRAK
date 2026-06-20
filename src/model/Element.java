package model;

public enum Element {
    FIRE("Fire"),
    WATER("Water"),
    THUNDER("Thunder"),
    NONE("None");

    private final String displayName;

    Element(String displayName) {
        this.displayName = displayName;
    }

    // 判断当前元素是否克制目标元素
    public boolean isStrongAgainst(Element target) {
        return (this == FIRE && target == THUNDER)
                || (this == THUNDER && target == WATER)
                || (this == WATER && target == FIRE);
    }

    public String getDisplayName() {
        return displayName;
    }
}
