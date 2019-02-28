package com.sc.fopa.penpalus.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import butterknife.ButterKnife;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.adapter.ViewPagerAdapter;
import com.sc.fopa.penpalus.fragment.MainHomeFragment;
import com.sc.fopa.penpalus.fragment.MoreFragment;
import com.sc.fopa.penpalus.fragment.RoomListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeActivity extends MainActivity {
    public static final int LIMIT_VIEW_PAGER = 2;
    private static final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.viewPagerMain)
    ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initNavigation();
        initViewPager();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Snackbar.make(getCurrentFocus(), "한번 더 누르시면 앱이 종료됩니다.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initNavigation() {

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.nav_dashboard:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.nav_more:
                        viewPager.setCurrentItem(2);
                        return true;
                }

                return false;
            }
        });
    }

    private void initViewPager() {
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(LIMIT_VIEW_PAGER);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getFragmentList());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int nav[] = {R.id.nav_home, R.id.nav_dashboard, R.id.nav_more};
                navigation.setSelectedItemId(nav[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<Fragment> getFragmentList() {
        List<Fragment> listFragment = new ArrayList<>();

        listFragment.add(new MainHomeFragment());
        listFragment.add(new RoomListFragment());
        listFragment.add(new MoreFragment());

        return listFragment;
    }
}
