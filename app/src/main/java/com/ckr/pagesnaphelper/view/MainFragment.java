package com.ckr.pagesnaphelper.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.MainAdapter;
import com.ckr.pagesnaphelper.model.Item;
import com.ckr.pagesnaphelper.widget.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements PageRecyclerView.OnPageChangeListener {
	public static final String TAG = "MainFragment";
	@BindView(R.id.pagerRecyclerView)
	PageRecyclerView pagerRecyclerView;
	private final static int ROW = 2;
	private final static int COLUMN = 4;
	private int pages;
	private final static int ORIENTATION = GridLayoutManager.HORIZONTAL;
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
		pagerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), ROW, ORIENTATION, false));
		pagerRecyclerView.setColumn(COLUMN);
		mainAdapter = new MainAdapter(getContext(), items);
		pagerRecyclerView.setAdapter(mainAdapter);
		pagerRecyclerView.addOnPageChangeListener(this);
	}

	private void initData() {
		items = new ArrayList<>(CAPACITY);
		for (int i = 0; i < CAPACITY; i++) {
			Item item = new Item();
			item.setName("item  " + i);
			item.setPosition(i);
			items.add(item);
		}
		dividePage(items);
	}

	private void dividePage(List<Item> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		Log.i(TAG, "dividePage-->size:" + list.size());
		pages = (int) Math.ceil(list.size() / (double) (COLUMN * ROW));            //多少页
		Log.i(TAG, "dividePage-->pages:" + pages);
		for (int i = list.size(); i < pages * COLUMN * ROW; i++) {
			list.add(null);
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
