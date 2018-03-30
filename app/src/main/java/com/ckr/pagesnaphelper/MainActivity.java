package com.ckr.pagesnaphelper;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ckr.pagesnaphelper.view.MainFragment;
import com.ckr.pageview.utils.PageLog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PageLog.debug();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
