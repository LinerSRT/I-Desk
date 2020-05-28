package com.liner.i_desk.Utils;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.liner.i_desk.Utils.Views.FirebaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentsClassesPagerAdapter extends FragmentPagerAdapter {
	public FragmentsClassesPagerAdapter(FragmentManager fragmentManager, Context context,
										ArrayList<Class<? extends FirebaseFragment>> pages) {
		super(fragmentManager);
		mPagesClasses = pages;
		mContext = context;
	}
	private List<Class<? extends FirebaseFragment>> mPagesClasses;
	private Context mContext;
	@Override
	public Fragment getItem(int posiiton) {
		return Fragment.instantiate(mContext, mPagesClasses.get(posiiton).getName());
	}
	@Override
	public int getCount() {
		return mPagesClasses.size();
	}

}
