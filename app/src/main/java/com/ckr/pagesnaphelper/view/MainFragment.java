package com.ckr.pagesnaphelper.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {
	public static final String TAG = "MainFragment";
	private static final String PAGE = "page";
	@BindView(R.id.viewPager)
	ViewPager viewPager;
	@BindView(R.id.myViewPager)
	ViewPager myViewPager;
	@BindView(R.id.tabLayout)
	TabLayout tabLayout;
	private FragmentManager fragmentManager;
	private ArrayList<BaseFragment> fragmentList;
	private static final String[] TITLES = {"One", "Two", "Three", "Four"};
	private int currentPage;
	private Bundle saveState;

	public static MainFragment newInstance() {
		Bundle args = new Bundle();
		MainFragment fragment = new MainFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.fragment_main;
	}

	@Override
	protected void init() {
		initFragment();
		initView();
		List<BaseFragment> fragments = new ArrayList<>(3);
		fragments.add(SubFragment.newInstance(R.color.color1));
		fragments.add(SubFragment.newInstance(R.color.color2));
		fragments.add(SubFragment.newInstance(R.color.color3));
		viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragments, null));
	}

	private void initFragment() {
		int length = TITLES.length;
		fragmentManager = getChildFragmentManager();
		fragmentList = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			String name = makeFragmentName(R.id.myViewPager, i);
			BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(name);
			if (fragment == null) {
				if (i == 0) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_one, R.layout.item_picture));
				} else if (i == 1) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_two, R.layout.item_picture_two));
				} else if (i == 2) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_three, R.layout.item_picture_three));
				} else if (i == 3) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_four, R.layout.item_picture_three));
				}
			} else {
				fragmentList.add(fragment);
			}
		}
	}

	private static String makeFragmentName(int viewId, long id) {
		return "android:switcher:" + viewId + ":" + id;
	}

	private void initView() {
		myViewPager.setAdapter(new MyFragmentPagerAdapter(fragmentManager, fragmentList, TITLES));
		tabLayout.setupWithViewPager(myViewPager);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		Bundle bundle = restoreState();
		if (bundle != null) {
			currentPage = bundle.getInt(PAGE, currentPage);
		}
		myViewPager.setCurrentItem(currentPage, false);
	}

	private Bundle restoreState() {
		Bundle arguments = getArguments();
		if (arguments == null) {
			return null;
		}
		Bundle bundle = arguments.getBundle(MainFragment.class.getName());
		return bundle;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		saveState = outState;
		saveState();
	}

	private void saveState() {
		if (saveState == null) {
			saveState = new Bundle();
		}
		saveState.putInt(PAGE, currentPage);
		Bundle arguments = getArguments();
		arguments.putBundle(MainFragment.class.getName(), saveState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (saveState == null) {
			saveState();
		}
	}
}

