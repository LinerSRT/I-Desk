package com.liner.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class FragmentAdapter extends FragmentPagerAdapter {
    private List<FragmentTab> fragments;
    private Context context;

    public FragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragments = new ArrayList<>();
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position).getFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = context.getResources().getDrawable(fragments.get(position).getFragmentIcon());
        image.setBounds(0, 0, image.getIntrinsicWidth()/2, image.getIntrinsicHeight()/2);
        SpannableString spannableString = new SpannableString("   " + fragments.get(position).getFragmentTitle());
        spannableString.setSpan(new ImageSpan(image, ImageSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    public void addTab(FragmentTab fragmentTab) {
        fragments.add(fragmentTab);
    }

    public static class FragmentTab {
        private Fragment fragment;
        private String fragmentTitle;
        private int fragmentIcon;

        public FragmentTab(Fragment fragment, String fragmentTitle, int fragmentIcon) {
            this.fragment = fragment;
            this.fragmentTitle = fragmentTitle;
            this.fragmentIcon = fragmentIcon;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public String getFragmentTitle() {
            return fragmentTitle;
        }

        public void setFragmentTitle(String fragmentTitle) {
            this.fragmentTitle = fragmentTitle;
        }

        public int getFragmentIcon() {
            return fragmentIcon;
        }

        public void setFragmentIcon(int fragmentIcon) {
            this.fragmentIcon = fragmentIcon;
        }
    }
}
