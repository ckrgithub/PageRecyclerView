package com.ckr.pagesnaphelper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.adapter.BasePageAdapter;
import com.ckr.pagesnaphelper.adapter.OnPageDataListener;

import java.util.List;

/**
 * Created by PC大佬 on 2018/1/16.
 */
public class PageView extends RelativeLayout implements PageRecyclerView.OnPageChangeListener {
	private static final String TAG = "PageView";
	private int selectedIndicatorColor = Color.RED;
	private int unselectedIndicatorColor = Color.BLACK;
	private int selectedIndicatorDiameter = 15;
	private int unselectedIndicatorDiameter = 15;
	private int indicatorMargin = 15;
	private Drawable selectedIndicatorDrawable = null;
	private Drawable unselectedIndicatorDrawable = null;
	private boolean hideIndicator = false;
	private int indicatorGroupHeight = 90;
	private int orientation = OrientationHelper.HORIZONTAL;
	private int pageRow = OnPageDataListener.TWO;
	private int pageColumn = OnPageDataListener.FOUR;
	private LinearLayout indicatorGroup;
	private View moveIndicator;//可移动的指示器
	private PageRecyclerView recyclerView;
	private BasePageAdapter mAdapter;
	private int lastPage;//上一页
	private int lastPages;//上一次的页数
	private PageRecyclerView.OnPageChangeListener mListener;

	public PageView(Context context) {
		this(context, null);
	}

	public PageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(context, attrs, defStyleAttr);
		initView();
	}

	private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageView, defStyleAttr, 0);
		selectedIndicatorColor = typedArray.getColor(R.styleable.PageView_selected_indicator_color, selectedIndicatorColor);
		unselectedIndicatorColor = typedArray.getColor(R.styleable.PageView_unselected_indicator_color, unselectedIndicatorColor);
		selectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_selected_indicator_diameter, selectedIndicatorDiameter);
		unselectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_unselected_indicator_diameter, unselectedIndicatorDiameter);
		indicatorGroupHeight = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_height, indicatorGroupHeight);
		indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_margin, indicatorMargin);
		hideIndicator = typedArray.getBoolean(R.styleable.PageView_hide_indicator, hideIndicator);
		if (typedArray.hasValue(R.styleable.PageView_selected_indicator_drawable)) {
			selectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_selected_indicator_drawable);
		}
		if (typedArray.hasValue(R.styleable.PageView_unselected_indicator_drawable)) {
			unselectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_unselected_indicator_drawable);
		}
		orientation = typedArray.getInteger(R.styleable.PageView_orientation, orientation);
		pageRow = typedArray.getInteger(R.styleable.PageView_page_row, pageRow);
		pageColumn = typedArray.getInteger(R.styleable.PageView_page_column, pageColumn);
		typedArray.recycle();
	}

	private void initView() {
		View inflate = View.inflate(getContext(), R.layout.layout_page_view, null);
		View view = inflate.findViewById(R.id.relativeLayout);
		if (hideIndicator) {
			view.setVisibility(GONE);
		} else {
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
			layoutParams.height = indicatorGroupHeight;
			view.setLayoutParams(layoutParams);
			recyclerView = (PageRecyclerView) inflate.findViewById(R.id.recyclerView);
			recyclerView.addOnPageChangeListener(this);
			indicatorGroup = (LinearLayout) inflate.findViewById(R.id.indicatorGroup);
			moveIndicator = inflate.findViewById(R.id.moveIndicator);
			layoutParams = (LayoutParams) moveIndicator.getLayoutParams();
			layoutParams.width = selectedIndicatorDiameter;
			layoutParams.height = selectedIndicatorDiameter;
			moveIndicator.setLayoutParams(layoutParams);
			if (selectedIndicatorDrawable == null) {
				drawViewBackground(moveIndicator, selectedIndicatorColor, selectedIndicatorDiameter
						, selectedIndicatorDiameter, selectedIndicatorDiameter, selectedIndicatorDiameter, 0, 0);
			} else {
				if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
					moveIndicator.setBackground(selectedIndicatorDrawable);
				} else {
					moveIndicator.setBackgroundDrawable(selectedIndicatorDrawable);
				}
			}
		}
		addView(inflate);
	}

	private void drawViewBackground(@NonNull View view, @ColorInt int color, float r0, float r1,
									float r2, float r3, int strokeColor, int strokeSize) {
		GradientDrawable d;
		d = new GradientDrawable();
		d.setColor(color);
		d.setCornerRadii(new float[]{r0, r0, r1, r1, r2, r2, r3, r3});
		if (strokeColor != Color.TRANSPARENT)
			d.setStroke(strokeSize, strokeColor);
		Canvas canvas = new Canvas();
		d.draw(canvas);
		view.setBackgroundDrawable(d);
	}


	public void setAdapter(@NonNull BasePageAdapter adapter) {
		mAdapter = adapter;
		mAdapter.setOrientation(orientation).setColumn(pageColumn).setRow(pageRow);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), pageRow, orientation, false));
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
		addIndicator();
		updateMoveIndicator();
	}

	private void addIndicator() {
		int childCount = indicatorGroup.getChildCount();
		int pageCount = mAdapter.getPageCount();
		if (childCount > pageCount) {
			for (int i = pageCount - 1; i < childCount - 1; i++) {
				indicatorGroup.removeViewAt(i);
			}
		} else if (childCount < pageCount) {
			for (int i = childCount; i < pageCount; i++) {
				createIndicator(indicatorGroup, i);                //添加指示点
			}
		}
	}

	private void createIndicator(LinearLayout indicatorGroup, int position) {
		Log.i(TAG, "createIndicator--->createIndicator,position:" + position);
		View view = new View(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(unselectedIndicatorDiameter, unselectedIndicatorDiameter);
		layoutParams.leftMargin = indicatorMargin;
		if (position == 0) {
			layoutParams.rightMargin = indicatorMargin;
		}
		view.setLayoutParams(layoutParams);
		if (unselectedIndicatorDrawable == null) {
			drawViewBackground(view, unselectedIndicatorColor, unselectedIndicatorDiameter
					, unselectedIndicatorDiameter, unselectedIndicatorDiameter, unselectedIndicatorDiameter, 0, 0);
		} else {
			if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
				view.setBackground(unselectedIndicatorDrawable);
			} else {
				view.setBackgroundDrawable(unselectedIndicatorDrawable);
			}
		}
		indicatorGroup.addView(view, 0);
	}

	private void updateMoveIndicator() {
		int pageCount = mAdapter.getPageCount();
		if (pageCount == 0) {
			moveIndicator.setVisibility(View.GONE);               //隐藏移动的指示点
		} else {
			moveIndicator.setVisibility(View.VISIBLE);
			int lastPage = recyclerView.getCurrentPage();
			if (pageCount < lastPages && lastPage >= pageCount) {//3,4,2/3
				resetRecycler();
			} else {
				moveIndicator(lastPage, moveIndicator);
			}
		}
	}

	private void resetRecycler() {
		recyclerView.setScrollX(0);
		moveIndicator(0, moveIndicator);
	}

	private void moveIndicator(int page, View view) {
		int pageCount = mAdapter.getPageCount();
		if (lastPage == page && lastPages == pageCount) {
			return;
		}
		lastPage = page;
		lastPages = pageCount;

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		layoutParams.topMargin = indicatorGroupHeight / 2 - unselectedIndicatorDiameter / 2;
		if (page == 0) {
			layoutParams.leftMargin = indicatorMargin - (selectedIndicatorDiameter - unselectedIndicatorDiameter) / 2;
		} else {
			layoutParams.leftMargin = page * (selectedIndicatorDiameter + indicatorMargin) + indicatorMargin - (selectedIndicatorDiameter - unselectedIndicatorDiameter) / 2;
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
		moveIndicator(position, moveIndicator);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}
}
