package com.ckr.pagesnaphelper.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.BasePageAdapter;

import java.util.List;

/**
 * Created by PC大佬 on 2018/1/16.
 */
public class PageView extends RelativeLayout implements PageRecyclerView.OnPageChangeListener {
	private static final String TAG = "PageView";
	private Context context;
	private LinearLayout pointGroup;
	private View movePoint;//可移动的指示器
	private PageRecyclerView recyclerView;
	private BasePageAdapter mAdapter;
	private int lastPage;//上一页
	private int lastPages;//上一次的页数
	private int mDiameter;
	private int mHeight;
	private PageRecyclerView.OnPageChangeListener mListener;

	public PageView(Context context) {
		this(context, null);
	}

	public PageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		initView();
	}

	private void initView() {
		View inflate = View.inflate(context, R.layout.layout_page_view, null);
		recyclerView = (PageRecyclerView) inflate.findViewById(R.id.recyclerView);
		recyclerView.addOnPageChangeListener(this);
		pointGroup = (LinearLayout) inflate.findViewById(R.id.pointGroup);
		movePoint = inflate.findViewById(R.id.movePoint);
		movePoint.setBackgroundResource(R.drawable.shape_point_selected);
		mDiameter = (int) getResources().getDimension(R.dimen.size5);
		mHeight = (int) getResources().getDimension(R.dimen.size26);
		addView(inflate);
	}

	public void setAdapter(@NonNull BasePageAdapter adapter) {
		mAdapter = adapter;
		recyclerView.setLayoutManager(new GridLayoutManager(context, mAdapter.getPageRow(), mAdapter.getLayoutOrientation(), false));
		recyclerView.setAdapter(mAdapter);
	}

	public void addOnPageChangeListener(PageRecyclerView.OnPageChangeListener listener) {
		mListener = listener;
	}

	public void updatePage(List list) {
		if (null == mAdapter) {
			return;
		}
		mAdapter.updateAll(list);
		addPoint();
		updatePoint();
	}

	private void addPoint() {
		pointGroup.removeAllViews();                      //移出所有指示点
		int pageCount = mAdapter.getPageCount();
		for (int i = 0; i < pageCount; i++) {
			createPoint(pointGroup, i, pageCount);                //添加指示点
		}
	}

	private void createPoint(LinearLayout pointGroup, int position, int size) {
		Log.i(TAG, "createPoint--->createPoint,size:" + size + ",position:" + position);
		View view = new View(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mDiameter, mDiameter);
		layoutParams.leftMargin = mDiameter;
		if (position == size - 1) {
			layoutParams.rightMargin = mDiameter;
		}
		view.setLayoutParams(layoutParams);
		view.setBackgroundResource(R.drawable.shape_point_unselected);
		pointGroup.addView(view);
	}

	private void updatePoint() {
		int pageCount = mAdapter.getPageCount();
		if (pageCount == 0) {
			movePoint.setVisibility(View.GONE);               //隐藏移动的指示点
		} else {
			movePoint.setVisibility(View.VISIBLE);
			int lastPage = recyclerView.getCurrentPage();
			if (pageCount < lastPages && lastPage > pageCount - 1) {
				recyclerView.setScrollX(0);
				movePoint(0, movePoint);
			} else {
				if (pageCount > lastPage) {
					movePoint(lastPage, movePoint);
				} else {
					recyclerView.setScrollX(pageCount - 1);
					movePoint(pageCount - 1, movePoint);
				}
			}
		}
	}

	private void movePoint(int page, View view) {
		int pageCount = mAdapter.getPageCount();
		if (lastPage == page && lastPages == pageCount) {
			return;
		}
		lastPage = page;
		lastPages = pageCount;

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		layoutParams.topMargin = mHeight / 2 - mDiameter / 2;
		if (page == 0) {
			layoutParams.leftMargin = mDiameter;
		} else {
			layoutParams.leftMargin = page * (mDiameter) * 2 + mDiameter;
		}
		view.setLayoutParams(layoutParams);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (mListener != null) {
			mListener.onPageSelected(position);
		}
		movePoint(position, movePoint);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}
}
