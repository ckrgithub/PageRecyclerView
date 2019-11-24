package com.ckr.pagesnaphelper.view;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PageRecyclerView;
import android.util.Log;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.MainAdapter;
import com.ckr.pagesnaphelper.model.Item;
import com.ckr.pageview.transform.BaseTransformer;
import com.ckr.pageview.view.PageView;

import java.util.ArrayList;

import butterknife.BindView;

//import com.ckr.pageview.view.PageRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends BaseFragment implements PageRecyclerView.OnPageChangeListener {
	private static final String TAG = "PageFragment";
	private static final String ID_LAYOUT = "layoutId";
	private static final String ID_LAYOUT_ITEM = "itemLayoutId";
	@BindView(R.id.pageView)
	PageView pageView;
	private MainAdapter mainAdapter;
	private ArrayList<Item> items;
	private final static int CAPACITY = 24;
	private int layoutId;
	private int itemLayoutId;
	private int startCount = 100;
	private boolean isVisible = false;
	private boolean isLooping;

	public static PageFragment newInstance(@LayoutRes int layoutId, @LayoutRes int itemLayoutId) {
		Bundle args = new Bundle();
		args.putInt(ID_LAYOUT, layoutId);
		args.putInt(ID_LAYOUT_ITEM, itemLayoutId);
		PageFragment fragment = new PageFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Bundle arguments = getArguments();
		if (arguments != null) {
			layoutId = arguments.getInt(ID_LAYOUT, R.layout.fragment_horizontal_grid);
			itemLayoutId = arguments.getInt(ID_LAYOUT_ITEM, R.layout.item_horizontal_grid);
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
		//AppCompatActivity版本>=26.1.0才生效
		//pageView.registerLifeCycleObserver();
		//pageView.hideIndicatorContainer(2);
		mainAdapter = new MainAdapter(getContext(), itemLayoutId);
		pageView.setAdapter(mainAdapter);
		isLooping = mainAdapter.isLooping();
		if (isLooping) {
			Log.d(TAG, "initView: " + isVisible);
			pageView.updateAll(items.subList(0, 3));
//			pageView.setCurrentItem(1, false);
		} else {
			pageView.updateAll(items);
		}
	}

	private void initData() {
		items = new ArrayList<>(CAPACITY);
		Item item = new Item();
		try {
			for (int i = 0; i < CAPACITY; i++) {
				Item clone = (Item) item.clone();
				clone.setName("item  " + i);
				items.add(clone);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onVisible() {
		if (isLooping) {
			Log.d(TAG, "onVisible: " + isVisible);
		}
		isVisible = true;
		if (pageView != null) {
			pageView.restartLooping();
		}
	}

	@Override
	protected void onInvisible() {
		if (isLooping) {
			Log.d(TAG, "onInvisible: " + isVisible);
		}
		isVisible = false;
		if (pageView != null) {
			pageView.stopLooping();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isLooping) {
			Log.d(TAG, "onResume: " + isVisible);
		}
		if (pageView != null && isVisible) {
			pageView.restartLooping();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (isLooping) {
			Log.d(TAG, "onStop: " + isVisible);
		}
		if (pageView != null) {
			pageView.stopLooping();
		}
	}

	@Override
	protected void addData(int index) {
		if (mainAdapter.isLooping()) {
//			mainAdapter.updateAll(new ArrayList<Item>());
			pageView.updateAll(items.subList(0, index > CAPACITY ? CAPACITY : index));
//			pageView.setCurrentItem(MAX_VALUE / 2, false);
			return;
		}
		int itemCount = mainAdapter.getRawItemCount();
		Item item = new Item();
		item.setName("item  " + startCount);
		startCount++;
		if (index == -1 || index >= itemCount) {
			mainAdapter.updateItem(item);
		} else {
			mainAdapter.updateItem(index, item);
		}
	}

	@Override
	protected void jumpToPage(int page) {
		if (mainAdapter.isLooping()) {
			return;
		}
		int pageCount = mainAdapter.getPageCount();
		if (page > pageCount - 1) {
			page = pageCount - 1;
		}
		pageView.setCurrentItem(page, false);
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

	@Override
	public void refreshFragment(BaseTransformer baseTransformer) {
		if (mainAdapter == null) {
			return;
		}
		if (mainAdapter.getPageRow() * mainAdapter.getPageColumn() == 1) {
			pageView.addPageTransformer(baseTransformer);
		}
	}

	@Override
	public void onDestroyView() {
		if (pageView != null) {
			pageView.release();
		}
		super.onDestroyView();
	}
}
