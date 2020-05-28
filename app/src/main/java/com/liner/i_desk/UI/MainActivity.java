package com.liner.i_desk.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.Fragments.MainFragment;
import com.liner.i_desk.UI.Fragments.SettingsFragment;
import com.liner.i_desk.Utils.FragmentsClassesPagerAdapter;
import com.liner.i_desk.Utils.Views.ExtendedViewPager;

import java.util.ArrayList;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends FragmentActivity {
    private ExtendedViewPager extendedViewPager;
    private SmoothBottomBar bottomBar;
    public static User user;
    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extendedViewPager = findViewById(R.id.mainPager);
        bottomBar = findViewById(R.id.bottomBar);
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseHelper.getUserModel(new FirebaseHelper.IFirebaseHelperListener() {
            @Override
            public void onSuccess(Object result) {
                user = (User) result;
                initPages();
            }

            @Override
            public void onFail(String reason) {
                user = null;
                initPages();
            }
        });

    }

    public static User getUser() {
        return user;
    }

    public static DatabaseReference getUsersDatabase() {
        return usersDatabase;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    private void initPages() {
        ArrayList<Class<? extends Fragment>> mainPages = new ArrayList<>();
        mainPages.add(MainFragment.class);
        mainPages.add(SettingsFragment.class);
        FragmentsClassesPagerAdapter adapter = new FragmentsClassesPagerAdapter(getSupportFragmentManager(), this, mainPages);
        extendedViewPager.setAdapter(adapter);
        extendedViewPager.setCurrentItem(0);
        extendedViewPager.setOffscreenPageLimit(2);
        extendedViewPager.setPagingEnabled(false);
        bottomBar.setActiveItem(extendedViewPager.getCurrentItem());
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                extendedViewPager.setCurrentItem(i);
                return true;
            }
        });
        extendedViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                bottomBar.setActiveItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
