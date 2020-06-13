package com.liner.views.irbottomnavigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.liner.utils.ViewUtils;
import com.liner.views.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpaceNavigationView extends RelativeLayout {

    private static final String TAG = "SpaceNavigationView";

    private static final String CURRENT_SELECTED_ITEM_BUNDLE_KEY = "currentItem";

    private static final String BADGES_ITEM_BUNDLE_KEY = "badgeItem";

    private static final String CHANGED_ICON_AND_TEXT_BUNDLE_KEY = "changedIconAndText";

    private static final String CENTRE_BUTTON_ICON_KEY = "centreButtonIconKey";

    private static final String CENTRE_BUTTON_COLOR_KEY = "centreButtonColorKey";

    private static final String SPACE_BACKGROUND_COLOR_KEY = "backgroundColorKey";

    private static final String BADGE_FULL_TEXT_KEY = "badgeFullTextKey";

    private static final String VISIBILITY = "visibilty";

    private static final int NOT_DEFINED = -777;

    private static final int MAX_SPACE_ITEM_SIZE = 4;

    private static final int MIN_SPACE_ITEM_SIZE = 2;
    private final int spaceNavigationHeight = (int) getResources().getDimension(R.dimen.space_navigation_height);
    private final int mainContentHeight = (int) getResources().getDimension(R.dimen.main_content_height);
    private final int centreContentWight = (int) getResources().getDimension(R.dimen.centre_content_width);
    private final int centreButtonSize = (int) getResources().getDimension(R.dimen.space_centre_button_default_size);
    private List<SpaceItem> spaceItems = new ArrayList<>();
    private List<View> spaceItemList = new ArrayList<>();
    private List<RelativeLayout> badgeList = new ArrayList<>();
    private HashMap<Integer, Object> badgeSaveInstanceHashMap = new HashMap<>();
    private HashMap<Integer, SpaceItem> changedItemAndIconHashMap = new HashMap<>();
    private SpaceOnClickListener spaceOnClickListener;
    private SpaceOnLongClickListener spaceOnLongClickListener;
    private Bundle savedInstanceState;
    private CentreButton centreButton;
    private RelativeLayout centreBackgroundView;
    private LinearLayout leftContent, rightContent;
    private BezierView centreContent;
    private Typeface customFont;
    private Context context;
    private int spaceItemIconSize = NOT_DEFINED;

    private int spaceItemIconOnlySize = NOT_DEFINED;

    private int spaceItemTextSize = NOT_DEFINED;

    private int spaceBackgroundColor = NOT_DEFINED;

    private int centreButtonColor = NOT_DEFINED;

    private int activeCentreButtonIconColor = NOT_DEFINED;

    private int inActiveCentreButtonIconColor = NOT_DEFINED;

    private int activeCentreButtonBackgroundColor = NOT_DEFINED;

    private int centreButtonIcon = NOT_DEFINED;

    private int activeSpaceItemColor = NOT_DEFINED;

    private int inActiveSpaceItemColor = NOT_DEFINED;

    private int centreButtonRippleColor = NOT_DEFINED;

    private int currentSelectedItem = 0;

    private int contentWidth;

    private boolean isCentreButtonSelectable = false;

    private boolean isCentrePartLinear = false;

    private boolean isTextOnlyMode = false;

    private boolean isIconOnlyMode = false;

    private boolean isCustomFont = false;

    private boolean isCentreButtonIconColorFilterEnabled = true;

    private boolean shouldShowBadgeWithNinePlus = true;


    public SpaceNavigationView(Context context) {
        this(context, null);
    }

    public SpaceNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpaceNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        if (attrs != null) {
            Resources resources = getResources();

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpaceNavigationView);
            spaceItemIconSize = typedArray.getDimensionPixelSize(R.styleable.SpaceNavigationView_space_item_icon_size, resources.getDimensionPixelSize(R.dimen.space_item_icon_default_size));
            spaceItemIconOnlySize = typedArray.getDimensionPixelSize(R.styleable.SpaceNavigationView_space_item_icon_only_size, resources.getDimensionPixelSize(R.dimen.space_item_icon_only_size));
            spaceItemTextSize = typedArray.getDimensionPixelSize(R.styleable.SpaceNavigationView_space_item_text_size, resources.getDimensionPixelSize(R.dimen.space_item_text_default_size));
            spaceItemIconOnlySize = typedArray.getDimensionPixelSize(R.styleable.SpaceNavigationView_space_item_icon_only_size, resources.getDimensionPixelSize(R.dimen.space_item_icon_only_size));
            spaceBackgroundColor = typedArray.getColor(R.styleable.SpaceNavigationView_space_background_color, resources.getColor(R.color.space_default_color));
            centreButtonColor = typedArray.getColor(R.styleable.SpaceNavigationView_centre_button_color, resources.getColor(R.color.centre_button_color));
            activeSpaceItemColor = typedArray.getColor(R.styleable.SpaceNavigationView_active_item_color, resources.getColor(R.color.space_white));
            inActiveSpaceItemColor = typedArray.getColor(R.styleable.SpaceNavigationView_inactive_item_color, resources.getColor(R.color.default_inactive_item_color));
            centreButtonIcon = typedArray.getResourceId(R.styleable.SpaceNavigationView_centre_button_icon, R.drawable.near_me);
            isCentrePartLinear = typedArray.getBoolean(R.styleable.SpaceNavigationView_centre_part_linear, false);
            activeCentreButtonIconColor = typedArray.getColor(R.styleable.SpaceNavigationView_active_centre_button_icon_color, resources.getColor(R.color.space_white));
            inActiveCentreButtonIconColor = typedArray.getColor(R.styleable.SpaceNavigationView_inactive_centre_button_icon_color, resources.getColor(R.color.default_inactive_item_color));
            activeCentreButtonBackgroundColor = typedArray.getColor(R.styleable.SpaceNavigationView_active_centre_button_background_color, resources.getColor(R.color.centre_button_color));

            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (spaceBackgroundColor == NOT_DEFINED)
            spaceBackgroundColor = ContextCompat.getColor(context, R.color.space_default_color);

        if (centreButtonColor == NOT_DEFINED)
            centreButtonColor = ContextCompat.getColor(context, R.color.centre_button_color);

        if (centreButtonIcon == NOT_DEFINED)
            centreButtonIcon = R.drawable.near_me;

        if (activeSpaceItemColor == NOT_DEFINED)
            activeSpaceItemColor = ContextCompat.getColor(context, R.color.space_white);

        if (inActiveSpaceItemColor == NOT_DEFINED)
            inActiveSpaceItemColor = ContextCompat.getColor(context, R.color.default_inactive_item_color);

        if (spaceItemTextSize == NOT_DEFINED)
            spaceItemTextSize = (int) getResources().getDimension(R.dimen.space_item_text_default_size);

        if (spaceItemIconSize == NOT_DEFINED)
            spaceItemIconSize = (int) getResources().getDimension(R.dimen.space_item_icon_default_size);

        if (spaceItemIconOnlySize == NOT_DEFINED)
            spaceItemIconOnlySize = (int) getResources().getDimension(R.dimen.space_item_icon_only_size);

        if (centreButtonRippleColor == NOT_DEFINED)
            centreButtonRippleColor = ContextCompat.getColor(context, R.color.colorBackgroundHighlightWhite);

        if (activeCentreButtonIconColor == NOT_DEFINED)
            activeCentreButtonIconColor = ContextCompat.getColor(context, R.color.space_white);

        if (inActiveCentreButtonIconColor == NOT_DEFINED)
            inActiveCentreButtonIconColor = ContextCompat.getColor(context, R.color.default_inactive_item_color);


        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = spaceNavigationHeight;
        setBackgroundColor(ContextCompat.getColor(context, R.color.space_transparent));
        setLayoutParams(params);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);


        restoreCurrentItem();


        if (spaceItems.size() < MIN_SPACE_ITEM_SIZE && !isInEditMode()) {
            throw new NullPointerException("Your space item count must be greater than 1 ," +
                    " your current items count isa : " + spaceItems.size());
        }

        if (spaceItems.size() > MAX_SPACE_ITEM_SIZE && !isInEditMode()) {
            throw new IndexOutOfBoundsException("Your items count maximum can be 4," +
                    " your current items count is : " + spaceItems.size());
        }


        contentWidth = (width - spaceNavigationHeight) / 2;


        removeAllViews();


        initAndAddViewsToMainView();


        postRequestLayout();


        restoreTranslation();
    }


    private void initAndAddViewsToMainView() {

        RelativeLayout mainContent = new RelativeLayout(context);
        centreBackgroundView = new RelativeLayout(context);

        leftContent = new LinearLayout(context);
        leftContent.setGravity(Gravity.CENTER);
        rightContent = new LinearLayout(context);
        rightContent.setGravity(Gravity.CENTER);
        centreContent = buildBezierView();
        centreButton = new CentreButton(context);
        centreButton.setBackgroundTintList(ColorStateList.valueOf(centreButtonColor));
        centreButton.setImageResource(centreButtonIcon);
        if (isCentreButtonIconColorFilterEnabled || isCentreButtonSelectable)
            centreButton.getDrawable().setColorFilter(inActiveCentreButtonIconColor, PorterDuff.Mode.SRC_IN);

        centreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spaceOnClickListener != null)
                    spaceOnClickListener.onCentreButtonClick();
                if (isCentreButtonSelectable)
                    updateSpaceItems(-1);
            }
        });
        centreButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (spaceOnLongClickListener != null)
                    spaceOnLongClickListener.onCentreButtonLongClick();

                return true;
            }
        });


        LayoutParams fabParams = new LayoutParams(centreButtonSize, centreButtonSize);
        fabParams.addRule(RelativeLayout.CENTER_IN_PARENT);


        LayoutParams mainContentParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mainContentHeight);
        mainContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        LayoutParams centreContentParams = new LayoutParams(centreContentWight, spaceNavigationHeight);
        centreContentParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        centreContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        LayoutParams centreBackgroundViewParams = new LayoutParams(centreContentWight, mainContentHeight);
        centreBackgroundViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        centreBackgroundViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        LayoutParams leftContentParams = new LayoutParams(contentWidth, mainContentHeight);
        leftContentParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftContentParams.addRule(LinearLayout.HORIZONTAL);
        leftContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        LayoutParams rightContentParams = new LayoutParams(contentWidth, mainContentHeight);
        rightContentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightContentParams.addRule(LinearLayout.HORIZONTAL);
        rightContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        setBackgroundColors();


        centreContent.addView(centreButton, fabParams);


        addView(leftContent, leftContentParams);
        addView(rightContent, rightContentParams);


        addView(centreBackgroundView, centreBackgroundViewParams);
        addView(centreContent, centreContentParams);
        addView(mainContent, mainContentParams);


        restoreChangedIconsAndTexts();


        addSpaceItems(leftContent, rightContent);
    }


    private void addSpaceItems(LinearLayout leftContent, LinearLayout rightContent) {


        if (leftContent.getChildCount() > 0 || rightContent.getChildCount() > 0) {
            leftContent.removeAllViews();
            rightContent.removeAllViews();
        }
        spaceItemList.clear();
        badgeList.clear();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (SpaceItem item : spaceItems) {
            final int index = spaceItems.indexOf(item);
            View container = inflater.inflate(R.layout.space_item_view, null, false);
            ImageView itemIcon = container.findViewById(R.id.space_icon);
            TextView itemText = container.findViewById(R.id.space_text);
            RelativeLayout badgeContainer = container.findViewById(R.id.badge_container);
            if (item.isShowIcon()) {
                if (item.getIcon() != -1) {
                    itemIcon.setVisibility(VISIBLE);
                    itemIcon.setImageResource(item.getIcon());
                }
            } else {
                itemIcon.setVisibility(GONE);
            }
            if (item.isShowText()) {
                if (isCustomFont)
                    itemText.setTypeface(customFont);
                itemText.setTextSize(TypedValue.COMPLEX_UNIT_PX, spaceItemTextSize);
                if (!(item.getText().length() <= 0) || item.getText() != null) {
                    itemText.setVisibility(VISIBLE);
                    itemText.setText(item.getText());
                }
            } else {
                itemText.setVisibility(GONE);
            }
            badgeContainer.setVisibility((item.isShowBadge())? VISIBLE:GONE);
            switch (item.getAlign()) {
                case LEFT:
                    RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.addRule(CENTER_IN_PARENT);
                    leftContent.addView(container, params);
                    break;
                case RIGHT:
                    RelativeLayout.LayoutParams params2 = new LayoutParams(contentWidth/2, ViewGroup.LayoutParams.MATCH_PARENT);
                    params2.addRule(CENTER_IN_PARENT);
                    rightContent.addView(container, params2);
                    break;
            }
            spaceItemList.add(container);
            badgeList.add(badgeContainer);
            if (index == currentSelectedItem) {
                itemText.setTextColor(activeSpaceItemColor);
                Utils.changeImageViewTint(itemIcon, activeSpaceItemColor);
            } else {
                itemText.setTextColor(inActiveSpaceItemColor);
                Utils.changeImageViewTint(itemIcon, inActiveSpaceItemColor);
            }
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateSpaceItems(index);
                }
            });
            container.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (spaceOnLongClickListener != null)
                        spaceOnLongClickListener.onItemLongClick(index, spaceItems.get(index).getText());
                    return true;
                }
            });
        }
        restoreBadges();
    }


    private void updateSpaceItems(final int selectedIndex) {


        if (currentSelectedItem == selectedIndex) {
            if (spaceOnClickListener != null && selectedIndex >= 0)
                spaceOnClickListener.onItemReselected(selectedIndex, spaceItems.get(selectedIndex).getText());

            return;
        }

        if (isCentreButtonSelectable) {

            if (selectedIndex == -1) {
                if (centreButton != null) {
                    centreButton.getDrawable().setColorFilter(activeCentreButtonIconColor, PorterDuff.Mode.SRC_IN);

                    if (activeCentreButtonBackgroundColor != NOT_DEFINED) {
                        centreButton.setBackgroundTintList(ColorStateList.valueOf(activeCentreButtonBackgroundColor));
                    }
                }
            }
            if (currentSelectedItem == -1) {
                if (centreButton != null) {
                    centreButton.getDrawable().setColorFilter(inActiveCentreButtonIconColor, PorterDuff.Mode.SRC_IN);

                    if (activeCentreButtonBackgroundColor != NOT_DEFINED) {
                        centreButton.setBackgroundTintList(ColorStateList.valueOf(centreButtonColor));
                    }
                }
            }
        }

        for (int i = 0; i < spaceItemList.size(); i++) {
            if (i == selectedIndex) {
                RelativeLayout textAndIconContainer = (RelativeLayout) spaceItemList.get(selectedIndex);
                ImageView spaceItemIcon = textAndIconContainer.findViewById(R.id.space_icon);
                TextView spaceItemText = textAndIconContainer.findViewById(R.id.space_text);
                spaceItemText.setTextColor(activeSpaceItemColor);
                Utils.changeImageViewTint(spaceItemIcon, activeSpaceItemColor);
            } else if (i == currentSelectedItem) {
                RelativeLayout textAndIconContainer = (RelativeLayout) spaceItemList.get(i);
                ImageView spaceItemIcon = textAndIconContainer.findViewById(R.id.space_icon);
                TextView spaceItemText = textAndIconContainer.findViewById(R.id.space_text);
                spaceItemText.setTextColor(inActiveSpaceItemColor);
                Utils.changeImageViewTint(spaceItemIcon, inActiveSpaceItemColor);
            }
        }

        if (spaceOnClickListener != null && selectedIndex >= 0)
            spaceOnClickListener.onItemClick(selectedIndex, spaceItems.get(selectedIndex).getText());

        currentSelectedItem = selectedIndex;
    }

    private void setBackgroundColors() {
        rightContent.setBackgroundColor(spaceBackgroundColor);
        centreBackgroundView.setBackgroundColor(spaceBackgroundColor);
        leftContent.setBackgroundColor(spaceBackgroundColor);
    }

    private void postRequestLayout() {
        SpaceNavigationView.this.getHandler().post(new Runnable() {
            @Override
            public void run() {
                SpaceNavigationView.this.requestLayout();
            }
        });
    }


    private void restoreCurrentItem() {
        Bundle restoredBundle = savedInstanceState;
        if (restoredBundle != null) {
            if (restoredBundle.containsKey(CURRENT_SELECTED_ITEM_BUNDLE_KEY))
                currentSelectedItem = restoredBundle.getInt(CURRENT_SELECTED_ITEM_BUNDLE_KEY, 0);
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreBadges() {
        Bundle restoredBundle = savedInstanceState;

        if (restoredBundle != null) {
            if (restoredBundle.containsKey(BADGE_FULL_TEXT_KEY)) {
                shouldShowBadgeWithNinePlus = restoredBundle.getBoolean(BADGE_FULL_TEXT_KEY);
            }

            if (restoredBundle.containsKey(BADGES_ITEM_BUNDLE_KEY)) {
                badgeSaveInstanceHashMap = (HashMap<Integer, Object>) savedInstanceState.getSerializable(BADGES_ITEM_BUNDLE_KEY);
                if (badgeSaveInstanceHashMap != null) {
                    for (Integer integer : badgeSaveInstanceHashMap.keySet()) {
                        BadgeHelper.forceShowBadge(
                                badgeList.get(integer),
                                (BadgeItem) badgeSaveInstanceHashMap.get(integer),
                                shouldShowBadgeWithNinePlus);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreChangedIconsAndTexts() {
        Bundle restoredBundle = savedInstanceState;
        if (restoredBundle != null) {
            if (restoredBundle.containsKey(CHANGED_ICON_AND_TEXT_BUNDLE_KEY)) {
                changedItemAndIconHashMap = (HashMap<Integer, SpaceItem>) restoredBundle.getSerializable(CHANGED_ICON_AND_TEXT_BUNDLE_KEY);
                if (changedItemAndIconHashMap != null) {
                    SpaceItem spaceItem;
                    for (int i = 0; i < changedItemAndIconHashMap.size(); i++) {
                        spaceItem = changedItemAndIconHashMap.get(i);
                        spaceItems.get(i).setIcon(spaceItem.getIcon());
                        spaceItems.get(i).setText(spaceItem.getText());
                    }
                }
            }

            if (restoredBundle.containsKey(CENTRE_BUTTON_ICON_KEY)) {
                centreButtonIcon = restoredBundle.getInt(CENTRE_BUTTON_ICON_KEY);
                centreButton.setImageResource(centreButtonIcon);
            }

            if (restoredBundle.containsKey(SPACE_BACKGROUND_COLOR_KEY)) {
                int backgroundColor = restoredBundle.getInt(SPACE_BACKGROUND_COLOR_KEY);
                changeSpaceBackgroundColor(backgroundColor);
            }
        }
    }

    private BezierView buildBezierView() {
        BezierView bezierView = new BezierView(context, spaceBackgroundColor);
        bezierView.build(centreContentWight, spaceNavigationHeight - mainContentHeight, isCentrePartLinear);
        return bezierView;
    }

    private void throwArrayIndexOutOfBoundsException(int itemIndex) {
        throw new ArrayIndexOutOfBoundsException("Your item index can't be 0 or greater than space item size," +
                " your items size is " + spaceItems.size() + ", your current index is :" + itemIndex);
    }


    public void initWithSaveInstanceState(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SELECTED_ITEM_BUNDLE_KEY, currentSelectedItem);
        outState.putInt(CENTRE_BUTTON_ICON_KEY, centreButtonIcon);
        outState.putInt(SPACE_BACKGROUND_COLOR_KEY, spaceBackgroundColor);
        outState.putBoolean(BADGE_FULL_TEXT_KEY, shouldShowBadgeWithNinePlus);
        outState.putFloat(VISIBILITY, this.getTranslationY());

        if (badgeSaveInstanceHashMap.size() > 0)
            outState.putSerializable(BADGES_ITEM_BUNDLE_KEY, badgeSaveInstanceHashMap);
        if (changedItemAndIconHashMap.size() > 0)
            outState.putSerializable(CHANGED_ICON_AND_TEXT_BUNDLE_KEY, changedItemAndIconHashMap);
    }

    public void setCentreButtonColor(@ColorInt int centreButtonColor) {
        this.centreButtonColor = centreButtonColor;
    }

    public void setSpaceBackgroundColor(@ColorInt int spaceBackgroundColor) {
        this.spaceBackgroundColor = spaceBackgroundColor;
    }

    public void setCentreButtonIcon(int centreButtonIcon) {
        this.centreButtonIcon = centreButtonIcon;
    }


    public void setActiveCentreButtonBackgroundColor(@ColorInt int activeCentreButtonBackgroundColor) {
        this.activeCentreButtonBackgroundColor = activeCentreButtonBackgroundColor;
    }

    public void setActiveSpaceItemColor(@ColorInt int activeSpaceItemColor) {
        this.activeSpaceItemColor = activeSpaceItemColor;
    }

    public void setInActiveSpaceItemColor(@ColorInt int inActiveSpaceItemColor) {
        this.inActiveSpaceItemColor = inActiveSpaceItemColor;
    }

    public void setSpaceItemIconSize(int spaceItemIconSize) {
        this.spaceItemIconSize = spaceItemIconSize;
    }


    public void setSpaceItemIconSizeInOnlyIconMode(int spaceItemIconOnlySize) {
        this.spaceItemIconOnlySize = spaceItemIconOnlySize;
    }


    public void setSpaceItemTextSize(int spaceItemTextSize) {
        this.spaceItemTextSize = spaceItemTextSize;
    }


    public void setCentreButtonRippleColor(int centreButtonRippleColor) {
        this.centreButtonRippleColor = centreButtonRippleColor;
    }


    public void showTextOnly() {
        isTextOnlyMode = true;
    }


    public void showIconOnly() {
        isIconOnlyMode = true;
    }


    public void setCentreButtonSelectable(boolean isSelectable) {
        this.isCentreButtonSelectable = isSelectable;
    }

    public void addSpaceItem(SpaceItem spaceItem) {
        spaceItems.add(spaceItem);
    }

    public void setCentreButtonSelected() {
        if (!isCentreButtonSelectable)
            throw new ArrayIndexOutOfBoundsException("Please be more careful, you must set the centre button selectable");
        else
            updateSpaceItems(-1);
    }

    public void setSpaceOnClickListener(SpaceOnClickListener spaceOnClickListener) {
        this.spaceOnClickListener = spaceOnClickListener;
    }

    public void setSpaceOnLongClickListener(SpaceOnLongClickListener spaceOnLongClickListener) {
        this.spaceOnLongClickListener = spaceOnLongClickListener;
    }

    public void changeCurrentItem(int indexToChange) {
        if (indexToChange < -1 || indexToChange > spaceItems.size())
            throw new ArrayIndexOutOfBoundsException("Please be more careful, we do't have such item : " + indexToChange);
        else {
            updateSpaceItems(indexToChange);
        }
    }


    public void showBadgeAtIndex(int itemIndex, int badgeText, @ColorInt int badgeColor) {
        if (itemIndex < 0 || itemIndex > spaceItems.size()) {
            throwArrayIndexOutOfBoundsException(itemIndex);
        } else {
            RelativeLayout badgeView = badgeList.get(itemIndex);
            badgeView.setBackground(BadgeHelper.makeShapeDrawable(badgeColor));
            BadgeItem badgeItem = new BadgeItem(itemIndex, badgeText, badgeColor);
            BadgeHelper.showBadge(badgeView, badgeItem, shouldShowBadgeWithNinePlus);
            badgeSaveInstanceHashMap.put(itemIndex, badgeItem);
        }
    }


    @SuppressWarnings("unchecked")
    private void restoreTranslation() {
        Bundle restoredBundle = savedInstanceState;

        if (restoredBundle != null) {
            if (restoredBundle.containsKey(VISIBILITY)) {
                this.setTranslationY(restoredBundle.getFloat(VISIBILITY));
            }

        }
    }

    @Deprecated
    public void hideBudgeAtIndex(final int index) {
        if (badgeList.get(index).getVisibility() == GONE) {
            Log.d(TAG, "Badge at index: " + index + " already hidden");
        } else {
            BadgeHelper.hideBadge(badgeList.get(index));
            badgeSaveInstanceHashMap.remove(index);
        }
    }

    public void hideBadgeAtIndex(final int index) {
        if (badgeList.get(index).getVisibility() == GONE) {
            Log.d(TAG, "Badge at index: " + index + " already hidden");
        } else {
            BadgeHelper.hideBadge(badgeList.get(index));
            badgeSaveInstanceHashMap.remove(index);
        }
    }

    @Deprecated
    public void hideAllBudges() {
        for (RelativeLayout badge : badgeList) {
            if (badge.getVisibility() == VISIBLE)
                BadgeHelper.hideBadge(badge);
        }
        badgeSaveInstanceHashMap.clear();
    }

    public void hideAllBadges() {
        for (RelativeLayout badge : badgeList) {
            if (badge.getVisibility() == VISIBLE)
                BadgeHelper.hideBadge(badge);
        }
        badgeSaveInstanceHashMap.clear();
    }

    public void changeBadgeTextAtIndex(int badgeIndex, int badgeText) {
        if (badgeSaveInstanceHashMap.get(badgeIndex) != null &&
                (((BadgeItem) badgeSaveInstanceHashMap.get(badgeIndex)).getIntBadgeText() != badgeText)) {
            BadgeItem currentBadgeItem = (BadgeItem) badgeSaveInstanceHashMap.get(badgeIndex);
            BadgeItem badgeItemForSave = new BadgeItem(badgeIndex, badgeText, currentBadgeItem.getBadgeColor());
            BadgeHelper.forceShowBadge(
                    badgeList.get(badgeIndex),
                    badgeItemForSave,
                    shouldShowBadgeWithNinePlus);
            badgeSaveInstanceHashMap.put(badgeIndex, badgeItemForSave);
        }
    }

    public void setFont(Typeface customFont) {
        isCustomFont = true;
        this.customFont = customFont;
    }

    public void setCentreButtonIconColorFilterEnabled(boolean enabled) {
        isCentreButtonIconColorFilterEnabled = enabled;
    }

    public void changeCenterButtonIcon(int icon) {
        if (centreButton == null) {
            Log.e(TAG, "You should call setCentreButtonIcon() instead, " +
                    "changeCenterButtonIcon works if space navigation already set up");
        } else {
            centreButton.setImageResource(icon);
            centreButtonIcon = icon;
        }
    }

    public void changeItemIconAtPosition(int itemIndex, int newIcon) {
        if (itemIndex < 0 || itemIndex > spaceItems.size()) {
            throwArrayIndexOutOfBoundsException(itemIndex);
        } else {
            SpaceItem spaceItem = spaceItems.get(itemIndex);
            RelativeLayout textAndIconContainer = (RelativeLayout) spaceItemList.get(itemIndex);
            ImageView spaceItemIcon = textAndIconContainer.findViewById(R.id.space_icon);
            spaceItemIcon.setImageResource(newIcon);
            spaceItem.setIcon(newIcon);
            changedItemAndIconHashMap.put(itemIndex, spaceItem);
        }
    }

    public void changeItemTextAtPosition(int itemIndex, String newText) {
        if (itemIndex < 0 || itemIndex > spaceItems.size()) {
            throwArrayIndexOutOfBoundsException(itemIndex);
        } else {
            SpaceItem spaceItem = spaceItems.get(itemIndex);
            RelativeLayout textAndIconContainer = (RelativeLayout) spaceItemList.get(itemIndex);
            TextView spaceItemIcon = textAndIconContainer.findViewById(R.id.space_text);
            spaceItemIcon.setText(newText);
            spaceItem.setText(newText);
            changedItemAndIconHashMap.put(itemIndex, spaceItem);
        }
    }

    public void changeSpaceBackgroundColor(@ColorInt int color) {
        if (color == spaceBackgroundColor) {
            Log.d(TAG, "changeSpaceBackgroundColor: color already changed");
            return;
        }

        spaceBackgroundColor = color;
        setBackgroundColors();
        centreContent.changeBackgroundColor(color);
    }

    public void shouldShowFullBadgeText(boolean shouldShowBadgeWithNinePlus) {
        this.shouldShowBadgeWithNinePlus = shouldShowBadgeWithNinePlus;
    }

    public void setActiveCentreButtonIconColor(@ColorInt int color) {
        activeCentreButtonIconColor = color;
    }


    public void setInActiveCentreButtonIconColor(@ColorInt int color) {
        inActiveCentreButtonIconColor = color;
    }
}
