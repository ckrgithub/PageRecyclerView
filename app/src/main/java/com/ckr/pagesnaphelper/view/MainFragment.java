package com.ckr.pagesnaphelper.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.MainAdapter;
import com.ckr.pagesnaphelper.adapter.OnPageDataListener;
import com.ckr.pagesnaphelper.model.Item;
import com.ckr.pagesnaphelper.widget.PageRecyclerView;
import com.ckr.pagesnaphelper.widget.PageView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements PageRecyclerView.OnPageChangeListener {
	public static final String TAG = "MainFragment";
	@BindView(R.id.pageView)
	PageView pageView;
	private final static int ROW = OnPageDataListener.TWO;
	private final static int COLUMN = OnPageDataListener.FOUR;
	private final static int ORIENTATION = OrientationHelper.HORIZONTAL;
	private MainAdapter mainAdapter;
	private ArrayList<Item> items;
	private final static int CAPACITY = 20;

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
		initData();
		initView();
	}

	private void initView() {
		pageView.addOnPageChangeListener(this);
		mainAdapter = new MainAdapter(getContext(), ORIENTATION, ROW, COLUMN);
		pageView.setAdapter(mainAdapter);
		pageView.updatePage(items);
	}

	private void initData() {
		items = new ArrayList<>(CAPACITY);
		for (int i = 0; i < CAPACITY; i++) {
			Item item = new Item();
			item.setName("item  " + i);
			item.setPosition(i);
			items.add(item);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		Log.d(TAG, "onPageScrolled() called with: position = [" + position + "], positionOffset = [" + positionOffset + "], positionOffsetPixels = [" + positionOffsetPixels + "]");
	}

	@Override
	public void onPageSelected(int position) {
		Log.d(TAG, "onPageSelected() called with: position = [" + position + "]");
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		Log.d(TAG, "onPageScrollStateChanged() called with: state = [" + state + "]");
	}
}
