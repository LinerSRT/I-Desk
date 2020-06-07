package com.liner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.liner.utils.ColorUtils;
import com.liner.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RequestCheckListView extends LinearLayout implements View.OnClickListener {
    private Context context;
    private List<RequestListItem> requestListItems = new ArrayList<>();
    private onItemClickListener onItemClickListener;

    public RequestCheckListView(Context context) {
        super(context);
        init(context);
    }

    public RequestCheckListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        configureLayout(this, VERTICAL);
    }


    @Override
    public void onClick(final View view) {
        if (onItemClickListener != null) {
            for (int i = 0; i < requestListItems.size(); i++) {
                if (view instanceof ImageView) {
                    if (view == requestListItems.get(i).getImageView()) {
                        final int finalI = i;
                        onItemClickListener.onCloseClick(finalI);
                    }
                } else if (view instanceof TextView) {
                    if (view == requestListItems.get(i).getTextView()) {
                        onItemClickListener.onItemClick(i, requestListItems.get(i));
                    }
                } else if (view instanceof ViewGroup) {
                    if (view == requestListItems.get(i).getItemLayout()) {
                        onItemClickListener.onItemClick(i, requestListItems.get(i));
                    }
                }
            }
        }
    }


    private TextView createElement(String text) {
        TextView textView = new TextView(context);
        textView.setId(new Random().nextInt(Integer.MAX_VALUE));
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setMaxLines(2);
        textView.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.checklist_icon), null, null, null);
        textView.setCompoundDrawablePadding(ViewUtils.pxToDp(context.getResources().getDimensionPixelSize(R.dimen.contentMarginHalf)));
        textView.setText(text);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(ViewUtils.pxToSp(context.getResources().getDimensionPixelSize(R.dimen.primaryTextSize)));
        textView.setTextColor(ColorUtils.getThemeColor(context, R.attr.textColor));
        textView.setOnClickListener(this);
        return textView;
    }

    private ImageView createElement() {
        ImageView imageView = new ImageView(context);
        imageView.setId(new Random().nextInt(Integer.MAX_VALUE));
        imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setOnClickListener(this);
        imageView.setImageResource(R.drawable.close);
        return imageView;
    }

    private void configureLayout(LinearLayout linearLayout, int orientation) {
        linearLayout.setOrientation(orientation);
        linearLayout.setGravity(Gravity.START);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOnClickListener(this);
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        linearLayout.setPadding(
                ViewUtils.dpToPx(8),
                ViewUtils.dpToPx(8),
                ViewUtils.dpToPx(8),
                ViewUtils.dpToPx(8)
        );
        linearLayout.setMinimumHeight(0);
    }


    public void addItem(final String itemName) {
        LinearLayout itemLayout = new LinearLayout(context);
        configureLayout(itemLayout, HORIZONTAL);
        itemLayout.setBackground(context.getDrawable(R.drawable.round_corner_background_light));
        itemLayout.setElevation(ViewUtils.dpToPx(2));
        itemLayout.setId(new Random().nextInt(Integer.MAX_VALUE));
        TextView textView = createElement(itemName);
        ImageView imageView = createElement();
        itemLayout.addView(textView);
        View black = new View(context);
        black.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        itemLayout.addView(black);
        itemLayout.addView(imageView);
        itemLayout.setOnClickListener(this);
        ViewUtils.setMargins(itemLayout, 0, 0, 0, ViewUtils.dpToPx(16));
        requestListItems.add(new RequestListItem(itemLayout, textView, imageView));
        updateViews();
    }

    public void updateViews() {
        removeAllViews();
        for (RequestListItem item : requestListItems) {
            addView(item.getItemLayout());
        }
        requestLayout();
    }

    public List<RequestListItem> getRequestListItems() {
        return requestListItems;
    }

    public void setOnItemClickListener(RequestCheckListView.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int position, RequestListItem requestListIte);

        void onCloseClick(int position);
    }

    public class RequestListItem {
        private LinearLayout itemLayout;
        private TextView textView;
        private ImageView imageView;

        public RequestListItem() {
        }

        public RequestListItem(LinearLayout itemLayout, TextView textView, ImageView imageView) {
            this.itemLayout = itemLayout;
            this.textView = textView;
            this.imageView = imageView;
        }

        public LinearLayout getItemLayout() {
            return itemLayout;
        }

        public void setItemLayout(LinearLayout itemLayout) {
            this.itemLayout = itemLayout;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}
