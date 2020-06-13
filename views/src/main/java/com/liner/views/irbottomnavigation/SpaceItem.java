package com.liner.views.irbottomnavigation;
import java.io.Serializable;

public class SpaceItem implements Serializable {
    private String text;
    private int icon;
    private boolean showIcon;
    private boolean showText;
    private boolean showBadge;
    private Align align;

    public SpaceItem(int icon, Align align) {
        this("", icon, true, false, true, align);
    }

    public SpaceItem(String text, int icon, Align align) {
        this(text, icon, true, true, true, align);
    }

    public SpaceItem(String text, Align align) {
        this(text, -1, false, true, true, align);
    }

    public SpaceItem(int icon, boolean showIcon, boolean showText, boolean showBadge, Align align) {
        this("", icon, showIcon, showText, showBadge, align);
    }

    public SpaceItem(String text, int icon, boolean showIcon, boolean showText, boolean showBadge, Align align) {
        this.text = text;
        this.icon = icon;
        this.showIcon = showIcon;
        this.showText = showText;
        this.showBadge = showBadge;
        this.align = align;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public boolean isShowBadge() {
        return showBadge;
    }

    public void setShowBadge(boolean showBadge) {
        this.showBadge = showBadge;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public enum Align {
        LEFT,
        RIGHT
    }
}
