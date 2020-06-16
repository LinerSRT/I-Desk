package com.liner.i_desk;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.liner.i_desk.Adapters.RequestCheksAdapter;
import com.liner.i_desk.Firebase.DatabaseListener;
import com.liner.i_desk.Firebase.FireActivity;
import com.liner.i_desk.Firebase.Firebase;
import com.liner.i_desk.Firebase.RequestObject;
import com.liner.i_desk.Fragments.Request.RequestActionsFragment;
import com.liner.i_desk.Fragments.Request.RequestFilesFragment;
import com.liner.i_desk.Fragments.Request.RequestMessagesFragment;
import com.liner.i_desk.Fragments.UserProfileFragment;
import com.liner.utils.ColorUtils;
import com.liner.utils.FragmentAdapter;
import com.liner.utils.Time;
import com.liner.views.ExpandLayout;
import com.liner.views.ExtendedViewPager;
import com.liner.views.YSTextView;

public class ActivityRequestDetail extends FireActivity {
    private DatabaseListener databaseListener;
    private ProgressBar deadlineProgress;
    private YSTextView activityBar;
    private YSTextView requestDetailTitle;
    private YSTextView requestDetailText;
    private YSTextView requestDetailDeviceText;
    private YSTextView requestDetailType;
    private YSTextView requestDetailPriority;
    private YSTextView requestDetailStatus;
    private YSTextView requestDetailCreatedTime;
    private YSTextView requestDetailDeadlineTime;
    private YSTextView requestClosedDetail;
    private ExpandLayout actionBarLayout;
    private ImageButton expandActionBar;


    private RequestObject requestObject;
    private ExtendedViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private TabLayout tabLayout;

    private RequestFilesFragment requestFilesFragment;
    private RequestMessagesFragment requestMessagesFragment;
    private UserProfileFragment userProfileFragment;
    private RequestActionsFragment requestActionsFragment;
    private RecyclerView requestChecksRecycler;
    private RequestCheksAdapter requestCheksAdapter;

    private int activeItem = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        activityBar = findViewById(R.id.requestDetailBar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        requestDetailTitle = findViewById(R.id.requestDetailTitle);
        requestDetailText = findViewById(R.id.requestDetailText);
        requestDetailDeviceText = findViewById(R.id.requestDetailDeviceText);
        requestDetailType = findViewById(R.id.requestDetailType);
        requestDetailPriority = findViewById(R.id.requestDetailPriority);
        requestDetailStatus = findViewById(R.id.requestDetailStatus);
        requestDetailCreatedTime = findViewById(R.id.requestDetailCreatedTime);
        requestDetailDeadlineTime = findViewById(R.id.requestDetailDeadlineTime);
        requestClosedDetail = findViewById(R.id.requestClosedDetail);
        deadlineProgress = findViewById(R.id.deadLineProgress);
        actionBarLayout = findViewById(R.id.actionBarLayout);
        expandActionBar = findViewById(R.id.expandActionBar);
        requestChecksRecycler = findViewById(R.id.requestChecksRecycler);

        if (getIntent().getSerializableExtra("requestObject") != null) {

            expandActionBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionBarLayout.toggle();
                    animateExpandButton();
                }
            });
            requestObject = (RequestObject) getIntent().getSerializableExtra("requestObject");
            databaseListener = new DatabaseListener() {
                @Override
                public void onRequestChanged(RequestObject item, int position) {
                    super.onRequestChanged(requestObject, position);
                    if (item.getRequestID().equals(requestObject.getRequestID())) {
                        requestObject = item;

                        initPage(activeItem);
                    }
                }
            };
            initPage(1);
            activityBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            updateRequestUI();
            databaseListener.startListening();
        } else {
            finish();
        }
    }

    private void initPage(int startPage){
        fragmentAdapter = new FragmentAdapter(ActivityRequestDetail.this, getSupportFragmentManager());
        requestFilesFragment = new RequestFilesFragment(requestObject);
        requestMessagesFragment = new RequestMessagesFragment(requestObject);
        if(requestObject.getRequestCreatorID().equals(Firebase.getUserUID())){
            if(requestObject.getRequestAcceptorID() != null){
                userProfileFragment = new UserProfileFragment(requestObject.getRequestAcceptorID(), true);
            } else {
                userProfileFragment = new UserProfileFragment("NO_ACCEPTOR", true);
            }
        } else {
            userProfileFragment = new UserProfileFragment(requestObject.getRequestCreatorID(), true);
        }
        requestActionsFragment = new RequestActionsFragment(requestObject);
        fragmentAdapter.addTab(new FragmentAdapter.FragmentTab(requestFilesFragment, "Файлы", R.drawable.file_icon));
        fragmentAdapter.addTab(new FragmentAdapter.FragmentTab(requestMessagesFragment, "Сообщения", R.drawable.message_icon));
        fragmentAdapter.addTab(new FragmentAdapter.FragmentTab(userProfileFragment, "Профиль", R.drawable.user_icon));
        fragmentAdapter.addTab(new FragmentAdapter.FragmentTab(requestActionsFragment, "Действия", R.drawable.star_icon));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(startPage);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                activeItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activeItem = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                    case 1:
                    case 2:
                        if (actionBarLayout.isExpanded()) {
                            actionBarLayout.collapse();
                            animateExpandButton();
                        }
                        break;
                    case 3:
                        actionBarLayout.expand();
                        animateExpandButton();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        updateRequestUI();
    }

    private void updateRequestUI() {
        requestChecksRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestCheksAdapter = new RequestCheksAdapter(this, requestObject);
        requestChecksRecycler.setAdapter(requestCheksAdapter);
        requestClosedDetail.setVisibility((requestObject.getRequestStatus() == RequestObject.RequestStatus.CLOSED)?View.VISIBLE:View.GONE);
        requestDetailTitle.setText(requestObject.getRequestTitle());
        requestDetailText.setText(requestObject.getRequestText());
        requestDetailDeviceText.setText(requestObject.getRequestUserDeviceText());
        switch (requestObject.getRequestType()) {
            case SERVICE:
                requestDetailType.setText("Сервис");
                requestDetailType.getBackground().setColorFilter(getResources().getColor(R.color.service_request), PorterDuff.Mode.SRC_IN);
                break;
            case INCIDENT:
                requestDetailType.setText("Инцидент");
                requestDetailType.getBackground().setColorFilter(getResources().getColor(R.color.incident_request), PorterDuff.Mode.SRC_IN);
                break;
            case CONSULTATION:
                requestDetailType.setText("Консультация");
                requestDetailType.getBackground().setColorFilter(getResources().getColor(R.color.consultation_request), PorterDuff.Mode.SRC_IN);
                break;
        }
        switch (requestObject.getRequestPriority()) {
            case VERY_LOW:
                requestDetailPriority.setText("Оч. низкий приоритет");
                requestDetailPriority.getBackground().setColorFilter(getResources().getColor(R.color.very_low_priority), PorterDuff.Mode.SRC_IN);
                break;
            case LOW:
                requestDetailPriority.setText("Низкий приоритет");
                requestDetailPriority.getBackground().setColorFilter(getResources().getColor(R.color.low_priority), PorterDuff.Mode.SRC_IN);
                break;
            case MEDIUM:
                requestDetailPriority.setText("Нормальный приоритет");
                requestDetailPriority.getBackground().setColorFilter(getResources().getColor(R.color.medium_priority), PorterDuff.Mode.SRC_IN);
                break;
            case HIGH:
                requestDetailPriority.setText("Оч. высокий приоритет");
                requestDetailPriority.getBackground().setColorFilter(getResources().getColor(R.color.high_priority), PorterDuff.Mode.SRC_IN);
                break;
            case VERY_HIGH:
                requestDetailPriority.setText("Высокий приоритет");
                requestDetailPriority.getBackground().setColorFilter(getResources().getColor(R.color.very_high_priority), PorterDuff.Mode.SRC_IN);
                break;
        }
        switch (requestObject.getRequestStatus()) {
            case PROCESSING:
                requestDetailStatus.setText("В процессе");
                requestDetailStatus.getBackground().setColorFilter(getResources().getColor(R.color.request_status_processing), PorterDuff.Mode.SRC_IN);
                break;
            case PENDING:
                requestDetailStatus.setText("В обработке");
                requestDetailStatus.getBackground().setColorFilter(getResources().getColor(R.color.request_status_pending), PorterDuff.Mode.SRC_IN);
                break;
            case CLOSED:
                requestDetailStatus.setText("Закрыта");
                requestDetailStatus.getBackground().setColorFilter(getResources().getColor(R.color.request_status_closed), PorterDuff.Mode.SRC_IN);
                break;
        }
        requestDetailCreatedTime.setText(Time.getHumanReadableTime(requestObject.getRequestCreatedAt(), "dd MMM HH:mm"));
        requestDetailDeadlineTime.setText(Time.getHumanReadableTime(requestObject.getRequestDeadlineAt(), "dd MMM HH:mm"));
        deadlineProgress.setProgress((int) Time.getPercent(requestObject.getRequestCreatedAt(), requestObject.getRequestDeadlineAt()));
        deadlineProgress.getProgressDrawable().setColorFilter(ColorUtils.interpolateColor(getResources().getColor(R.color.primary), Color.RED, deadlineProgress.getProgress()), PorterDuff.Mode.SRC_IN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseListener.isListening()) {
            databaseListener.stopListening();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            requestFilesFragment.onActivityResult(requestCode, resultCode, data);
            requestMessagesFragment.onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void animateExpandButton() {
        ValueAnimator animation = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("Rotation", actionBarLayout.isExpanded() ? 180 : 0, actionBarLayout.isExpanded() ? 0 : 180));
        animation.setDuration(400);
        animation.setInterpolator(new OvershootInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expandActionBar.setRotation((Float) valueAnimator.getAnimatedValue("Rotation"));
            }
        });
        if (animation.isRunning())
            animation.cancel();
        animation.start();
    }
}