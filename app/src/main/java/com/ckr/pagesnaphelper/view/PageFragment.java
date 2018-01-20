package com.ckr.pagesnaphelper.view;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.MainAdapter;
import com.ckr.pagesnaphelper.model.Item;
import com.ckr.pagesnaphelper.widget.PageRecyclerView;
import com.ckr.pagesnaphelper.widget.PageView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends BaseFragment implements PageRecyclerView.OnPageChangeListener {
	private static final String TAG = "PageFragment";
	private static final String LAYOUT = "layoutId";
	private static final String LAYOUT_ITEM = "itemLayoutId";
	@BindView(R.id.pageView)
	PageView pageView;
	private MainAdapter mainAdapter;
	private ArrayList<Item> items;
	private final static int CAPACITY = 21;
	private int layoutId;
	private int itemLayoutId;

	public static PageFragment newInstance(@LayoutRes int layoutId, @LayoutRes int itemLayoutId) {
		Bundle args = new Bundle();
		args.putInt(LAYOUT, layoutId);
		args.putInt(LAYOUT_ITEM, itemLayoutId);
		PageFragment fragment = new PageFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Bundle arguments = getArguments();
		if (arguments != null) {
			layoutId = arguments.getInt(LAYOUT, R.layout.fragment_one);
			itemLayoutId = arguments.getInt(LAYOUT_ITEM, R.layout.item_picture);
		}
	}

	@Override
	protected int getContentLayoutId() {
		return layoutId;
	}

	@Override
	protected void init() {
		initData();
		initView();
	}

	private void initView() {
		pageView.addOnPageChangeListener(this);
		mainAdapter = new MainAdapter(getContext(),itemLayoutId);
		pageView.setAdapter(mainAdapter);
		pageView.updateAll(items);
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
