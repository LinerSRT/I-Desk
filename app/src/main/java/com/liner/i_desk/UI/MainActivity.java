package com.liner.i_desk.UI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.liner.i_desk.API.Data.User;
import com.liner.i_desk.API.FirebaseHelper;
import com.liner.i_desk.R;
import com.liner.i_desk.UI.Fragments.MainFragment;
import com.liner.i_desk.UI.Fragments.UserProfileFragment;
import com.liner.i_desk.Utils.Animations.PagesTransformer;
import com.liner.i_desk.Utils.FragmentsClassesPagerAdapter;
import com.liner.i_desk.Utils.Views.ExtendedViewPager;
import com.liner.i_desk.Utils.Views.FirebaseActivity;
import com.liner.i_desk.Utils.Views.FirebaseFragment;

import java.util.ArrayList;
import java.util.Objects;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends FirebaseActivity {
    public static ExtendedViewPager extendedViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extendedViewPager = findViewById(R.id.mainPager);




    }

    @Override
    public void onFirebaseChanged() {

    }

    @Override
    public void onUserObtained(User user) {
        initPages();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initPages() {
        ArrayList<Class<? extends FirebaseFragment>> mainPages = new ArrayList<>();
        mainPages.add(MainFragment.class);
        mainPages.add(UserProfileFragment.class);
        FragmentsClassesPagerAdapter adapter = new FragmentsClassesPagerAdapter(getSupportFragmentManager(), this, mainPages);
        extendedViewPager.setAdapter(adapter);
        extendedViewPager.setCurrentItem(0);
        extendedViewPager.setOffscreenPageLimit(2);
        extendedViewPager.setPagingEnabled(false);
    }


    @Override
    public void onBackPressed() {

    }
}
