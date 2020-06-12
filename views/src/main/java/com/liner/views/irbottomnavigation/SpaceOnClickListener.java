package com.liner.views.irbottomnavigation;

public interface SpaceOnClickListener {

    void onCentreButtonClick();

    void onItemClick(int itemIndex, String itemName);

    void onItemReselected(int itemIndex, String itemName);
}
