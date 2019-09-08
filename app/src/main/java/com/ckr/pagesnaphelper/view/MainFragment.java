package com.ckr.pagesnaphelper.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.MyFragmentPagerAdapter;
import com.ckr.pageview.transform.BaseTransformer;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

import static com.ckr.pagesnaphelper.R.id.editText;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
	private static final String TAG = "MainFragment";
	private static final String ARGS_PAGE = "mCurrentPage";
	@BindView(R.id.myViewPager)
	ViewPager myViewPager;
	@BindView(R.id.tabLayout)
	TabLayout tabLayout;
	@BindView(editText)
	EditText addText;
	@BindView(R.id.editText2)
	EditText jumpText;
	@BindArray(R.array.tab_title)
	String[] TITLES;
	private FragmentManager fragmentManager;
	private ArrayList<BaseFragment> fragmentList;
	private int mCurrentPage;
	private Bundle saveState;
	private BaseTransformer baseTransformer;

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
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_horizontal_linear, R.layout.item_horizontal_linear));
				} else if (i == 1) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_horizontal_grid, R.layout.item_horizontal_grid));
				} else if (i == 2) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_horizontal_grid2, R.layout.item_horizontal_grid2));
				} else if (i == 3) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_vertical_linear, R.layout.item_vertical_linear));
				} else if (i == 4) {
					fragmentList.add(PageFragment.newInstance(R.layout.fragment_vertical_grid, R.layout.item_vertical_grid));
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
		myViewPager.addOnPageChangeListener(this);
		myViewPager.setAdapter(new MyFragmentPagerAdapter(fragmentManager, fragmentList, TITLES));
		tabLayout.setupWithViewPager(myViewPager);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		Bundle bundle = restoreState();
		if (bundle != null) {
			mCurrentPage = bundle.getInt(ARGS_PAGE, mCurrentPage);
		}
		myViewPager.setCurrentItem(mCurrentPage, false);
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
		saveState.putInt(ARGS_PAGE, mCurrentPage);
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


	@OnClick({R.id.add, R.id.jump})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.add:
				String text = addText.getText().toString().trim();
				int index = -1;
				if (!TextUtils.isEmpty(text)) {
					index = Integer.valueOf(text);
				}
				fragmentList.get(mCurrentPage).addData(index);
				break;
			case R.id.jump:
				text = jumpText.getText().toString().trim();
				int page = 0;
				if (!TextUtils.isEmpty(text)) {
					page = Integer.valueOf(text);
				}else {
					Toast.makeText(getActivity(),"内容不能为空",Toast.LENGTH_LONG).show();
				}
				fragmentList.get(mCurrentPage).jumpToPage(page);
				break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mCurrentPage = position;
		fragmentList.get(mCurrentPage).refreshFragment(this.baseTransformer);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void refreshFragment(BaseTransformer baseTransformer) {
		this.baseTransformer = baseTransformer;
		fragmentList.get(mCurrentPage).refreshFragment(this.baseTransformer);
	}
}

