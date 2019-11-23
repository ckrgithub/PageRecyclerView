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
import android.support.v7.widget.PageRecyclerView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ckr.pageview.R;
import com.ckr.pageview.adapter.BasePageAdapter;
import com.ckr.pageview.adapter.OnIndicatorListener;
import com.ckr.pageview.adapter.OnPageDataListener;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.ckr.pageview.utils.PageLog.Logd;
import static com.ckr.pageview.utils.PageLog.Logi;
import static com.ckr.pageview.utils.PageLog.Logv;

/**
 * Created by PC大佬 on 2018/1/16.
 * <p>
 * this is a puzzle here ,line 233
 */
public class PageView extends RelativeLayout implements PageRecyclerView.OnPageChangeListener, OnIndicatorListener {
	private static final String TAG = "PageView";
	private static final int INTERVAL = 3000;
	private static final int SUB_INTERVAL = 100;
	private static final int MAX_SCROLL_DURATION = 600;
	private int selectedIndicatorColor = Color.RED;
	private int unselectedIndicatorColor = Color.BLACK;
	private int selectedIndicatorDiameter = 15;
	private int unselectedIndicatorDiameter = 15;
	//指示器间的间隔
	private int indicatorMargin = 15;
	private Drawable selectedIndicatorDrawable = null;
	private Drawable unselectedIndicatorDrawable = null;
	//recyclerView的背景色
	private Drawable pageBackground = null;
	//指示器容器的背景色
	private Drawable indicatorContainerBackground = null;
	//是否隐藏指示器
	private boolean hideIndicator = false;
	//指示器容器
	private View indicatorContainer;
	private int indicatorContainerHeight = 90;
	private int indicatorContainerWidth = 90;
	//一组指示器的父View
	private LinearLayout indicatorGroup;
	private int indicatorGroupAlignment = 0x11;
	private int indicatorGroupMarginLeft;
	private int indicatorGroupMarginTop;
	private int indicatorGroupMarginRight;
	private int indicatorGroupMarginBottom;
	//可移动的指示器
	private View moveIndicator;
	private int orientation = OnPageDataListener.HORIZONTAL;
	private int pageRow = OnPageDataListener.ONE;
	private int pageColumn = OnPageDataListener.ONE;
	//布局标记，如：线性布局或网格布局
	private int layoutFlag = OnPageDataListener.LINEAR;
	private boolean isLooping = false;
	private boolean autoPlay = false;
	private int interval = INTERVAL;
	//item宽高自适应
	private boolean autoSize = false;
	//是否允许触摸滑动
	private boolean enableTouchScroll = true;
	//无限轮播下，自动调整滚动下标
	private boolean isAdjustPosition;
	//无限轮播时，自动调整下标后，播放的时间间隔
	private int subInterval = SUB_INTERVAL;
	private PageRecyclerView recyclerView;
	private BasePageAdapter mAdapter;
	//指示器布局是否遮住PageRecyclerView
	private boolean overlapStyle = false;
	private boolean clipToPadding = true;
	private int pagePadding;
	//上一页
	private int lastPage = -1;
	//上一次的页数
	private int lastPageCount;
	private PageRecyclerView.OnPageChangeListener mOnPageChangeListener;
	private OnIndicatorListener mOnIndicatorListener;
	//当页数变化后，指示器是停留在第一页还是最后一页
	private boolean isScrollToBeginPage = false;
	private PageHandler mHandler;
	private boolean isStartLooping = true;
	private boolean isLoopingPause = false;
	//当页数小于等于该值时，隐藏掉指示器
	private int thresholdToHideIndicator = 0;
	private int maxScrollDuration = MAX_SCROLL_DURATION;
	private int minScrollDuration = 0;

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
		if (isAutoLooping()) {
			mHandler = new PageHandler(new WeakReference<PageView>(this));
		}
	}

	private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageView, defStyleAttr, 0);
		selectedIndicatorColor = typedArray.getColor(R.styleable.PageView_selected_indicator_color, selectedIndicatorColor);
		unselectedIndicatorColor = typedArray.getColor(R.styleable.PageView_unselected_indicator_color, unselectedIndicatorColor);
		selectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_selected_indicator_diameter, selectedIndicatorDiameter);
		unselectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_unselected_indicator_diameter, unselectedIndicatorDiameter);
		indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_margin, indicatorMargin);
		if (typedArray.hasValue(R.styleable.PageView_selected_indicator_drawable)) {
			selectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_selected_indicator_drawable);
		}
		if (typedArray.hasValue(R.styleable.PageView_unselected_indicator_drawable)) {
			unselectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_unselected_indicator_drawable);
		}
		if (typedArray.hasValue(R.styleable.PageView_page_background)) {
			pageBackground = typedArray.getDrawable(R.styleable.PageView_page_background);
		}
		if (typedArray.hasValue(R.styleable.PageView_indicator_container_background)) {
			indicatorContainerBackground = typedArray.getDrawable(R.styleable.PageView_indicator_container_background);
		}
		hideIndicator = typedArray.getBoolean(R.styleable.PageView_hide_indicator, hideIndicator);
		indicatorContainerHeight = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_container_height, indicatorContainerHeight);
		indicatorContainerWidth = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_container_width, indicatorContainerWidth);
		orientation = typedArray.getInteger(R.styleable.PageView_orientation, orientation);
		pageRow = typedArray.getInteger(R.styleable.PageView_page_row, pageRow);
		pageColumn = typedArray.getInteger(R.styleable.PageView_page_column, pageColumn);
		layoutFlag = typedArray.getInteger(R.styleable.PageView_layout_flag, layoutFlag);
		isLooping = typedArray.getBoolean(R.styleable.PageView_loop, isLooping) && onlyOne();
		autoPlay = typedArray.getBoolean(R.styleable.PageView_autoplay, autoPlay);
		autoSize = typedArray.getBoolean(R.styleable.PageView_autosize, autoSize);
		enableTouchScroll = typedArray.getBoolean(R.styleable.PageView_enable_touch_scroll, enableTouchScroll);
		interval = Math.abs(typedArray.getInt(R.styleable.PageView_loop_interval, interval));
		subInterval = Math.abs(typedArray.getInt(R.styleable.PageView_sub_loop_interval, subInterval));
		overlapStyle = typedArray.getBoolean(R.styleable.PageView_overlap_layout, overlapStyle);
		clipToPadding = typedArray.getBoolean(R.styleable.PageView_clipToPadding, clipToPadding);
		pagePadding = typedArray.getDimensionPixelSize(R.styleable.PageView_pagePadding, pagePadding);
		indicatorGroupAlignment = typedArray.getInteger(R.styleable.PageView_indicator_group_alignment, indicatorGroupAlignment);
		indicatorGroupMarginLeft = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginLeft, indicatorGroupMarginLeft);
		indicatorGroupMarginTop = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginTop, indicatorGroupMarginTop);
		indicatorGroupMarginRight = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginRight, indicatorGroupMarginRight);
		indicatorGroupMarginBottom = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginBottom, indicatorGroupMarginBottom);
		maxScrollDuration = Math.abs(typedArray.getInt(R.styleable.PageView_max_scroll_duration, maxScrollDuration));
		minScrollDuration = Math.abs(typedArray.getInt(R.styleable.PageView_min_scroll_duration, minScrollDuration));
		typedArray.recycle();
	}

	private boolean onlyOne() {
		return pageColumn * pageRow == 1;
	}

	private void initView() {
		View inflate;
		if (orientation == OnPageDataListener.HORIZONTAL) {
			inflate = View.inflate(getContext(), R.layout.horizontal_page_view, null);
		} else {
			inflate = View.inflate(getContext(), R.layout.vertical_page_view, null);
		}
		//<editor-fold desc="init recyclerView">
		recyclerView = (PageRecyclerView) inflate.findViewById(R.id.recyclerView);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();
		if (orientation == OnPageDataListener.HORIZONTAL) {
			if (!hideIndicator && !overlapStyle) {
				params.bottomMargin = indicatorContainerHeight;
			}
			if (isLooping) {
				recyclerView.setPadding(pagePadding, 0, pagePadding, 0);
			}
		} else if (orientation == OnPageDataListener.VERTICAL) {
			if (!hideIndicator && !overlapStyle) {
				params.leftMargin = indicatorContainerWidth;
			}
//			recyclerView.setPadding(0, pagePadding, 0, pagePadding);
		}
		recyclerView.setLayoutParams(params);
		recyclerView.setClipToPadding(clipToPadding);
		recyclerView.setOrientation(orientation);
		recyclerView.setLooping(isLooping);
		recyclerView.setMaxScrollDuration(maxScrollDuration);
		recyclerView.setMinScrollDuration(minScrollDuration);
		recyclerView.setEnableTouchScroll(enableTouchScroll);
		recyclerView.addOnPageChangeListener(this);
		if (pageBackground != null) {
			if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
				recyclerView.setBackground(pageBackground);
			} else {
				recyclerView.setBackgroundDrawable(pageBackground);
			}
			pageBackground = null;
		}
		//</editor-fold>
		indicatorContainer = inflate.findViewById(R.id.indicatorContainer);
		if (hideIndicator) {
			indicatorContainer.setVisibility(GONE);
		} else {
			//<editor-fold desc="init indicatorContainer"
			params = (FrameLayout.LayoutParams) indicatorContainer.getLayoutParams();
			if (orientation == OnPageDataListener.HORIZONTAL) {
				params.height = indicatorContainerHeight;
			} else if (orientation == OnPageDataListener.VERTICAL) {
				params.width = indicatorContainerWidth;
			}
			indicatorContainer.setLayoutParams(params);
			if (indicatorContainerBackground != null) {
				if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
					indicatorContainer.setBackground(indicatorContainerBackground);
				} else {
					indicatorContainer.setBackgroundDrawable(indicatorContainerBackground);
				}
				indicatorContainerBackground = null;
			}
			//</editor-fold>
			layoutIndicatorGroup(inflate);
			//<editor-fold desc="init moveIndicator"
			moveIndicator = inflate.findViewById(R.id.moveIndicator);
			LayoutParams layoutParams = (LayoutParams) moveIndicator.getLayoutParams();
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
				selectedIndicatorDrawable = null;
			}
			//</editor-fold>
		}
		addView(inflate);
	}

	private void layoutIndicatorGroup(View inflate) {
		indicatorGroup = (LinearLayout) inflate.findViewById(R.id.indicatorGroup);
		View view = inflate.findViewById(R.id.relativeLayout);
		LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
		layoutParams.setMargins(indicatorGroupMarginLeft, indicatorGroupMarginTop, indicatorGroupMarginRight, indicatorGroupMarginBottom);
		final int verticalGravity = indicatorGroupAlignment & Gravity.VERTICAL_GRAVITY_MASK;
		int horizontalGravity = indicatorGroupAlignment & Gravity.HORIZONTAL_GRAVITY_MASK;
		Logv(TAG, "initView: indicatorGroupAlignment:" + indicatorGroupAlignment
				+ ",verticalGravity:" + verticalGravity + ",horizontalGravity:" + horizontalGravity);
		switch (horizontalGravity) {
			case Gravity.CENTER_HORIZONTAL:
				layoutParams.addRule(CENTER_HORIZONTAL);
				break;
			case Gravity.RIGHT:
//				layoutParams.addRule(ALIGN_PARENT_RIGHT);.tell me why it doesn't work ?
				layoutParams.addRule(ALIGN_PARENT_END);
				break;
			case Gravity.LEFT:
			default:
//				layoutParams.addRule(ALIGN_PARENT_LEFT);
				layoutParams.addRule(ALIGN_PARENT_START);
				break;
		}
		switch (verticalGravity) {
			case Gravity.CENTER_VERTICAL:
				layoutParams.addRule(CENTER_VERTICAL);
				break;
			case Gravity.BOTTOM:
				layoutParams.addRule(ALIGN_PARENT_BOTTOM);
				break;
			case Gravity.TOP:
			default:
				layoutParams.addRule(ALIGN_PARENT_TOP);
				break;
		}
		view.setLayoutParams(layoutParams);
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

	public int getLoopingInterval() {
		return interval;
	}

	public PageHandler getHandler() {
		return mHandler;
	}

	public void pauseLooping() {
		if (mHandler != null) {
			mHandler.removeMessages(PageHandler.MSG_START_LOOPING);
			mHandler.removeMessages(PageHandler.MSG_STOP_LOOPING);
		}
	}

	public void resumeLooping() {
		if (isAutoLooping() && mHandler != null) {
			mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, interval);
		}
	}

	public final boolean isAutoLooping() {
		return autoPlay && isLooping;
	}

	public void stopLooping() {
		Logi(TAG, "stopLooping: ");
		if (mHandler != null) {
			mHandler.removeMessages(PageHandler.MSG_START_LOOPING);
			mHandler.removeMessages(PageHandler.MSG_STOP_LOOPING);
			autoPlay = false;
		}
	}

	public void restartLooping() {
		Logi(TAG, "restartLooping: ");
		if (mHandler != null) {
			autoPlay = true;
			if (isAutoLooping()) {
				mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, interval);
			}
		}
	}

	public void release() {
		stopLooping();
		mHandler = null;
		mAdapter = null;
		mOnPageChangeListener = null;
		mOnIndicatorListener = null;
	}

	public void addOnPageChangeListener(PageRecyclerView.OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	public void addOnIndicatorListener(OnIndicatorListener listener) {
		mOnIndicatorListener = listener;
	}

	public void addPageTransformer(PageRecyclerView.PageTransformer pageTransformer) {
		recyclerView.addPageTransformer(pageTransformer);
	}

	public void setAdapter(@NonNull BasePageAdapter adapter) {
		mAdapter = adapter;
		mAdapter.setLayoutFlag(layoutFlag).setOrientation(orientation)
				.setLooping(isLooping).setAutosize(autoSize)
				.setColumn(pageColumn).setRow(pageRow)
				.setOnIndicatorListener(this);
		if (layoutFlag == OnPageDataListener.LINEAR) {
			recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), orientation == OnPageDataListener.HORIZONTAL ? pageRow : pageColumn, orientation, false));
		}
		if (adapter.isAutoSize()) {
			mAdapter.notifySizeChanged(recyclerView.getSize());
		}
		recyclerView.setAdapter(mAdapter);
		if (!hideIndicator) {
			switchIndicatorContainer();
		}
	}

	public void switchIndicatorContainer() {
		List data = mAdapter.getRawData();
		int pageCount = mAdapter.getPageCount();
		if (data == null || data.size() == 0 || pageCount <= thresholdToHideIndicator) {
			if (indicatorContainer.getVisibility() != INVISIBLE) {
				indicatorContainer.setVisibility(INVISIBLE);
			}
		} else {
			if (indicatorContainer.getVisibility() != VISIBLE) {
				indicatorContainer.setVisibility(VISIBLE);
			}
		}
	}

	public void hideIndicatorContainer(int threshold) {
		this.thresholdToHideIndicator = threshold;
	}

	public int getPageCount() {
		if (mAdapter != null) {
			return mAdapter.getPageCount();
		}
		return 0;
	}

	/**
	 * 更新数据源
	 *
	 * @param list
	 */
	public void updateAll(@NonNull List list) {
		if (null == mAdapter) {
			return;
		}
		if (isAutoLooping()) {
			//标记已暂停轮询状态
			isLoopingPause = true;
			pauseLooping();
		}
		Logd(TAG, "updateAll: isLoopingPause:" + isLoopingPause);
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
		switchIndicatorContainer();
		if (isAutoLooping()) {
			Logd(TAG, "updateIndicator: isLoopingPause:" + isLoopingPause);
			if (isLoopingPause) {
				//解除已暂停轮询状态
				isLoopingPause = false;
			} else {
				pauseLooping();
			}
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
			for (int i = childCount - 1; i >= pageCount; i--) {
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
		Logd(TAG, "createIndicator,position:" + position);
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
			if (moveIndicator.getVisibility() != GONE) {
				moveIndicator.setVisibility(View.GONE);
			}
		} else {
			if (isLooping) {
				if (orientation == OnPageDataListener.HORIZONTAL) {
					int width = recyclerView.getWidth();
					if (width == 0) {
						return;
					}
				} else {
					int height = recyclerView.getHeight();
					if (height == 0) {
						return;
					}
				}
			}
			if (moveIndicator.getVisibility() != VISIBLE) {
				moveIndicator.setVisibility(View.VISIBLE);
			}
			int lastPage = recyclerView.getCurrentPage();
			if (isLooping) {
				if (lastPageCount != 0) {
					lastPage %= lastPageCount;
				}
			}
			Logd(TAG, "updateMoveIndicator: lastPageCount:" + lastPageCount + ",lastPage:" + lastPage + ",pageCount:" + pageCount);
			//页数改变后，指示器自适应
			if (pageCount < lastPageCount && lastPage >= pageCount) {
				if (isScrollToBeginPage) {
					if (isLooping) {
						if (lastPageCount != 0) {
							int currentPage = recyclerView.getCurrentPage();
							int mod = currentPage % pageCount;
							int quotient = currentPage / pageCount + (mod == 0 ? 0 : Math.abs(pageCount - mod) <= 1 ? 0 : 1);
							int targetPage = quotient * pageCount;
							setCurrentItem(targetPage, false);
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
								resumeLooping();
							} else if (targetPage == currentPage) {
								resumeLooping();
							}
						}
					} else {
						scrollToBeginPage();
					}
				} else {
					if (isLooping) {
						if (lastPageCount != 0) {
							int currentPage = recyclerView.getCurrentPage();
							int mod = currentPage % pageCount;
							int quotient = currentPage / pageCount + (mod == 0 ? 0 : Math.abs(pageCount - mod) <= 1 ? 0 : 1);
							int targetPage = quotient * pageCount - 1;
							setCurrentItem(targetPage, false);
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
								resumeLooping();
							} else if (targetPage == currentPage) {
								resumeLooping();
							}
						}
					} else {
						scrollToEndPage(pageCount - 1);
					}
				}
			} else {
				if (isLooping) {
					int currentPage = recyclerView.getCurrentPage();
					int targetPage = lastPageCount == 0 ? currentPage : currentPage / pageCount * pageCount + lastPage;
					setCurrentItem(targetPage, false);
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
						resumeLooping();
					} else if (targetPage == currentPage) {
						resumeLooping();
					}
				} else {
					moveIndicator(lastPage, moveIndicator);
				}
			}
		}
	}

	public void setScrollToBeginPage(boolean scrollToBeginPage) {
		isScrollToBeginPage = scrollToBeginPage;
	}

	public int getCurrentItem() {
		return recyclerView == null ? 0 : recyclerView.getCurrentPage();
	}

	public void setCurrentItem(@IntRange(from = 0) int page) {
		setCurrentItem(page, true);
	}

	public void setCurrentItem(@IntRange(from = 0) int page, boolean smoothScroll) {
		Logd(TAG, "setCurrentItem: page:" + page);
		this.isAdjustPosition = false;
		int targetPage = page;
		if (isAutoLooping()) {
			List rawData = mAdapter.getRawData();
			if (rawData != null) {
				int size = rawData.size();
				if (size > 0) {
					int itemCount = mAdapter.getItemCount();
					if (itemCount >= BasePageAdapter.MIN_ITEM_COUNT) {
						if (page >= itemCount - 1) {
							isAdjustPosition = true;
							targetPage = (page - 1) % size;
						}
					}
				}
			}
		}
		if (isAdjustPosition) {
			recyclerView.scrollToPage(targetPage, false);
		} else {
			recyclerView.scrollToPage(targetPage, smoothScroll);
		}
		moveIndicator(targetPage, moveIndicator);
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
		if (isLooping) {
			if (pageCount != 0) {
				page %= pageCount;
			}
		}
		if (lastPage == page && lastPageCount == pageCount) {
			return;
		}
		lastPage = page;
		lastPageCount = pageCount;
		int margin = 0;
		final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
		if (orientation == OnPageDataListener.HORIZONTAL) {
			layoutParams.topMargin = indicatorContainerHeight / 2 - unselectedIndicatorDiameter / 2;
		} else if (orientation == OnPageDataListener.VERTICAL) {
			layoutParams.leftMargin = indicatorContainerWidth / 2 - unselectedIndicatorDiameter / 2;
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
		Logd(TAG, "moveIndicator: margin:" + margin);
		view.post(new Runnable() {
			@Override
			public void run() {
				view.setLayoutParams(layoutParams);
				if (view.getVisibility() != VISIBLE) {
					view.setVisibility(VISIBLE);
				}
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
		Logd(TAG, "onPageScrollStateChanged: position:" + position);
		if (isAutoLooping()) {
			if (isStartLooping) {
				isStartLooping = false;
				if (mHandler != null) {
					mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, interval);
				}
			}
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		Logd(TAG, "onPageScrollStateChanged: state:" + state);
		if (isAutoLooping()) {
			switch (state) {
				case RecyclerView.SCROLL_STATE_DRAGGING:
					if (mHandler != null) {
						mHandler.sendEmptyMessage(PageHandler.MSG_STOP_LOOPING);
					}
					break;
				case RecyclerView.SCROLL_STATE_IDLE:
					if (mHandler != null) {
						int delayMillis = isAdjustPosition ? subInterval : interval;
						Logd(TAG, "onPageScrollStateChanged: delayMillis=" + delayMillis);
						mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, delayMillis);
					}
					break;
			}
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}
}
