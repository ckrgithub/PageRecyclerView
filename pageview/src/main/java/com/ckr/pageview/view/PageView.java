package com.ckr.pageview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ckr.pageview.R;
import com.ckr.pageview.adapter.BasePageAdapter;
import com.ckr.pageview.adapter.OnIndicatorListener;
import com.ckr.pageview.adapter.OnPageDataListener;

import java.util.List;

/**
 * Created by PC大佬 on 2018/1/16.
 */
public class PageView extends RelativeLayout implements PageRecyclerView.OnPageChangeListener, OnIndicatorListener {
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
	private int orientation = OnPageDataListener.HORIZONTAL;
	private int pageRow = OnPageDataListener.ONE;
	private int pageColumn = OnPageDataListener.ONE;
	private int layoutFlag = OnPageDataListener.LINEAR;
	private LinearLayout indicatorGroup;
	private View moveIndicator;//可移动的指示器
	private PageRecyclerView recyclerView;
	private BasePageAdapter mAdapter;
	private int lastPage;//上一页
	private int lastPages;//上一次的页数
	private PageRecyclerView.OnPageChangeListener mOnPageChangeListener;
	private OnIndicatorListener mOnIndicatorListener;
	private boolean isScrollToBeginPage = false;

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
		layoutFlag = typedArray.getInteger(R.styleable.PageView_layout_flag, layoutFlag);
		typedArray.recycle();
	}

	private void initView() {
		View inflate;
		if (orientation == 0) {
			inflate = View.inflate(getContext(), R.layout.horizontal_page_view, null);
		} else {
			inflate = View.inflate(getContext(), R.layout.vertical_page_view, null);
		}
		View view = inflate.findViewById(R.id.relativeLayout);
		LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
		if (orientation == OnPageDataListener.HORIZONTAL) {
			layoutParams.height = indicatorGroupHeight;
		} else if (orientation == OnPageDataListener.VERTICAL) {
			layoutParams.width = indicatorGroupHeight;
		}
		view.setLayoutParams(layoutParams);
		recyclerView = (PageRecyclerView) inflate.findViewById(R.id.recyclerView);
		recyclerView.setOrientation(orientation);
		recyclerView.addOnPageChangeListener(this);
		if (hideIndicator) {
			view.setVisibility(GONE);
		} else {
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

	public void addOnPageChangeListener(PageRecyclerView.OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	public void addOnIndicatorListener(OnIndicatorListener listener) {
		mOnIndicatorListener = listener;
	}

	public void setAdapter(@NonNull BasePageAdapter adapter) {
		mAdapter = adapter;
		mAdapter.setLayoutFlag(layoutFlag).setOrientation(orientation).setOnIndicatorListener(this);
		if (layoutFlag == OnPageDataListener.LINEAR) {
			mAdapter.setColumn(OnPageDataListener.ONE).setRow(OnPageDataListener.ONE);
			recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
		} else {
			mAdapter.setColumn(pageColumn).setRow(pageRow);
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), orientation == 0 ? pageRow : pageColumn, orientation, false));
		}
		recyclerView.setAdapter(mAdapter);
	}

	/**
	 * 更新数据源
	 *
	 * @param list
	 */
	public void updateAll(List list) {
		if (null == mAdapter) {
			return;
		}
		mAdapter.updateAll(list);
	}

	/**
	 * 更新指示器
	 */
	@Override
	public void updateIndicator() {
		if (mOnIndicatorListener != null) {
			mOnIndicatorListener.updateIndicator();
		}
		if (hideIndicator) {
			return;
		}
		addIndicator();
		updateMoveIndicator();
	}

	/**
	 * 添加指示器
	 */
	private void addIndicator() {
		int childCount = indicatorGroup.getChildCount();
		int pageCount = mAdapter.getPageCount();
		if (childCount > pageCount) {
			for (int i = pageCount; i < childCount; i++) {
				indicatorGroup.removeViewAt(i);
			}
		} else if (childCount < pageCount) {
			for (int i = childCount; i < pageCount; i++) {
				createIndicator(indicatorGroup, i);                //添加指示点
			}
		}
	}

	/**
	 * 创建指示器
	 *
	 * @param indicatorGroup
	 * @param position
	 */
	private void createIndicator(LinearLayout indicatorGroup, int position) {
		Log.i(TAG, "createIndicator,position:" + position);
		View view = new View(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(unselectedIndicatorDiameter, unselectedIndicatorDiameter);
		if (orientation == OnPageDataListener.HORIZONTAL) {
			layoutParams.rightMargin = indicatorMargin;
			if (position == 0) {
				layoutParams.leftMargin = indicatorMargin;
			}
		} else if (orientation == OnPageDataListener.VERTICAL) {
			layoutParams.bottomMargin = indicatorMargin;
			if (position == 0) {
				layoutParams.topMargin = indicatorMargin;
			}
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
		indicatorGroup.addView(view);
	}

	/**
	 * 更新可移动的指示器
	 */
	private void updateMoveIndicator() {
		int pageCount = mAdapter.getPageCount();
		if (pageCount == 0) {
			moveIndicator.setVisibility(View.GONE);               //隐藏移动的指示点
		} else {
			moveIndicator.setVisibility(View.VISIBLE);
			int lastPage = recyclerView.getCurrentPage();
			if (pageCount < lastPages && lastPage >= pageCount) {//2,3,1
				if (isScrollToBeginPage) {
					scrollToBeginPage();
				} else {
					scrollToEndPage(pageCount - 1);
				}
			} else {
				moveIndicator(lastPage, moveIndicator);
			}
		}
	}

	public void setScrollToBeginPage(boolean scrollToBeginPage) {
		isScrollToBeginPage = scrollToBeginPage;
	}

	public void setCurrentItem(@IntRange(from = 0) int page) {
		setCurrentItem(page, true);
	}

	public void setCurrentItem(@IntRange(from = 0) int page, boolean smoothScroll) {
		recyclerView.scrollToPage(page, smoothScroll);
		moveIndicator(page, moveIndicator);
	}

	private void scrollToBeginPage() {
		recyclerView.scrollToBeginPage();
		moveIndicator(0, moveIndicator);
	}

	private void scrollToEndPage(@IntRange(from = 0) int page) {
		recyclerView.scrollToEndPage(page);
		moveIndicator(page, moveIndicator);
	}

	/**
	 * 移动指示器
	 *
	 * @param page
	 * @param view
	 */
	private void moveIndicator(int page, final View view) {
		if (hideIndicator) {
			return;
		}
		int pageCount = mAdapter.getPageCount();
		if (lastPage == page && lastPages == pageCount) {
			return;
		}
		lastPage = page;
		lastPages = pageCount;
		int margin = 0;
		final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
		if (orientation == OnPageDataListener.HORIZONTAL) {
			layoutParams.topMargin = indicatorGroupHeight / 2 - unselectedIndicatorDiameter / 2;
		} else if (orientation == OnPageDataListener.VERTICAL) {
			layoutParams.leftMargin = indicatorGroupHeight / 2 - unselectedIndicatorDiameter / 2;
		}
		if (page == 0) {
			margin = indicatorMargin - (selectedIndicatorDiameter - unselectedIndicatorDiameter) / 2;
		} else {
			margin = page * (unselectedIndicatorDiameter + indicatorMargin) + indicatorMargin - (selectedIndicatorDiameter - unselectedIndicatorDiameter) / 2;
		}
		if (orientation == OnPageDataListener.HORIZONTAL) {
			layoutParams.leftMargin = margin;
		} else if (orientation == OnPageDataListener.VERTICAL) {
			layoutParams.topMargin = margin;
		}
		Log.d(TAG, "moveIndicator: margin:" + margin);
		view.post(new Runnable() {
			@Override
			public void run() {
				view.setLayoutParams(layoutParams);
			}
		});
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		moveIndicator(position, moveIndicator);
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}
}
