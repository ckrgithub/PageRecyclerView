package com.ckr.pagesnaphelper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ckr.pagesnaphelper.view.BaseFragment;

import java.util.List;

/**
 * Created by PC大佬 on 2018/1/14.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	List<BaseFragment> fragmentList;
	private String[] titles;

	public MyFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList, String[] titles) {
		super(fm);
		this.fragmentList = fragmentList;
		this.titles = titles;
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
}
