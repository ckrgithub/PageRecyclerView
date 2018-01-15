package com.ckr.pagesnaphelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ckr.pagesnaphelper.adapter.MyFragmentPagerAdpater;
import com.ckr.pagesnaphelper.view.MainFragment;
import com.ckr.pagesnaphelper.view.OneFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindColor(R.color.color1)
    int color1;
    @BindColor(R.color.color2)
    int color2;
    @BindColor(R.color.color3)
    int color3;
    private Unbinder unbinder;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, MainFragment.newInstance(), MainFragment.class.getName())
                    .commit();
        } else {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(MainFragment.class.getName()))
                    .commit();
        }
        fragmentList = new ArrayList<>();
        fragmentList.add(OneFragment.newInstance("One", color1));
        fragmentList.add(OneFragment.newInstance("Two", color2));
        fragmentList.add(OneFragment.newInstance("Three", color3));
        viewPager.setAdapter(new MyFragmentPagerAdpater(getSupportFragmentManager(), fragmentList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
