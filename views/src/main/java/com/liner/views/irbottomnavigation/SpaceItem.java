package com.liner.views.irbottomnavigation;
import java.io.Serializable;

public class SpaceItem implements Serializable {

    private String itemName;

    private int itemIcon;

    public SpaceItem(String itemName, int itemIcon) {
        this.itemName = itemName;
        this.itemIcon = itemIcon;
    }

    String getItemName() {
        return itemName;
    }

    void setItemName(String itemName) {
        this.itemName = itemName;
    }

    int getItemIcon() {
        return itemIcon;
    }

    void setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
    }
}
