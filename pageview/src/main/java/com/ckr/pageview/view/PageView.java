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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ckr.pageview.R;
import com.ckr.pageview.adapter.BasePageAdapter;
import com.ckr.pageview.adapter.OnIndicatorListener;
import com.ckr.pageview.adapter.OnPageDataListener;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.ckr.pageview.utils.PageLog.Logd;
import static com.ckr.pageview.utils.PageLog.Loge;

/**
 * Created by PC大佬 on 2018/1/16.
 */
public class PageView extends RelativeLayout implements PageRecyclerView.OnPageChangeListener, OnIndicatorListener {
	private static final String TAG = "PageView";
	private static final int INTERVAL = 3000;
	private int selectedIndicatorColor = Color.RED;
	private int unselectedIndicatorColor = Color.BLACK;
	private int selectedIndicatorDiameter = 15;
	private int unselectedIndicatorDiameter = 15;
	private int indicatorMargin = 15;
	private Drawable selectedIndicatorDrawable = null;
	private Drawable unselectedIndicatorDrawable = null;
	private Drawable pageBackground = null;
	private boolean hideIndicator = false;
	private int indicatorGroupHeight = 90;
	private int indicatorGroupWidth = 90;
	private int orientation = OnPageDataListener.HORIZONTAL;
	private int pageRow = OnPageDataListener.ONE;
	private int pageColumn = OnPageDataListener.ONE;
	private int layoutFlag = OnPageDataListener.LINEAR;
	private boolean isLooping = false;
	private int interval;
	private LinearLayout indicatorGroup;
	private View moveIndicator;//可移动的指示器
	private PageRecyclerView recyclerView;
	private BasePageAdapter mAdapter;
	private int lastPage;//上一页
	private int lastPageCount;//上一次的页数
	private PageRecyclerView.OnPageChangeListener mOnPageChangeListener;
	private OnIndicatorListener mOnIndicatorListener;
	private boolean isScrollToBeginPage = false;
	private PageHandler mHandler;
	private boolean firstEnter = true;

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
		if (isLooping) {
			mHandler = new PageHandler(new WeakReference<PageView>(this));
		}
	}

	private void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageView, defStyleAttr, 0);
		selectedIndicatorColor = typedArray.getColor(R.styleable.PageView_selected_indicator_color, selectedIndicatorColor);
		unselectedIndicatorColor = typedArray.getColor(R.styleable.PageView_unselected_indicator_color, unselectedIndicatorColor);
		selectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_selected_indicator_diameter, selectedIndicatorDiameter);
		unselectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_unselected_indicator_diameter, unselectedIndicatorDiameter);
		indicatorGroupHeight = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_height, indicatorGroupHeight);
		indicatorGroupWidth = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_width, indicatorGroupWidth);
		indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_margin, indicatorMargin);
		hideIndicator = typedArray.getBoolean(R.styleable.PageView_hide_indicator, hideIndicator);
		if (typedArray.hasValue(R.styleable.PageView_selected_indicator_drawable)) {
			selectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_selected_indicator_drawable);
		}
		if (typedArray.hasValue(R.styleable.PageView_unselected_indicator_drawable)) {
			unselectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_unselected_indicator_drawable);
		}
		if (typedArray.hasValue(R.styleable.PageView_page_background)) {
			pageBackground = typedArray.getDrawable(R.styleable.PageView_page_background);
		}
		orientation = typedArray.getInteger(R.styleable.PageView_orientation, orientation);
		pageRow = typedArray.getInteger(R.styleable.PageView_page_row, pageRow);
		pageColumn = typedArray.getInteger(R.styleable.PageView_page_column, pageColumn);
		layoutFlag = typedArray.getInteger(R.styleable.PageView_layout_flag, layoutFlag);
		isLooping = typedArray.getBoolean(R.styleable.PageView_endless_loop, isLooping) && pageColumn * pageRow == 1;
		interval = Math.abs(typedArray.getInt(R.styleable.PageView_loop_interval, INTERVAL));
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
			layoutParams.width = indicatorGroupWidth;
		}
		view.setLayoutParams(layoutParams);
		recyclerView = (PageRecyclerView) inflate.findViewById(R.id.recyclerView);
		recyclerView.setOrientation(orientation);
		recyclerView.setLooping(isLooping);
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
		if (pageBackground != null) {
			if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
				recyclerView.setBackground(pageBackground);
			} else {
				recyclerView.setBackgroundDrawable(pageBackground);
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
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, interval);
		}
	}

	public void stopLooping() {
		Loge(TAG, "stopLooping: ");
		if (mHandler != null) {
			mHandler.removeMessages(PageHandler.MSG_START_LOOPING);
			mHandler.removeMessages(PageHandler.MSG_STOP_LOOPING);
			isLooping = false;
		}
	}

	public void restartLooping() {
		Log.d(TAG, "restartLooping: ");
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, interval);
			isLooping = true;
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

	public void setAdapter(@NonNull BasePageAdapter adapter) {
		mAdapter = adapter;
		mAdapter.setLayoutFlag(layoutFlag).setOrientation(orientation).setLooping(isLooping)
				.setColumn(pageColumn).setRow(pageRow)
				.setOnIndicatorListener(this);
		if (layoutFlag == OnPageDataListener.LINEAR) {
			recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), orientation == OnPageDataListener.HORIZONTAL ? pageRow : pageColumn, orientation, false));
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
		if (isLooping) {
			pauseLooping();
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
			moveIndicator.setVisibility(View.GONE);               //隐藏移动的指示点
		} else {
			moveIndicator.setVisibility(View.VISIBLE);
			int lastPage = recyclerView.getCurrentPage();
			if (isLooping) {
				if (lastPageCount != 0) {
					lastPage %= lastPageCount;
				}
			}
			if (pageCount < lastPageCount && lastPage >= pageCount) {//2,3,1
				if (isScrollToBeginPage) {
					if (isLooping) {
						if (lastPageCount != 0) {
							int currentPage = recyclerView.getCurrentPage();
							int mod = currentPage % pageCount;
							int quotient = currentPage / pageCount + (mod == 0 ? 0 : Math.abs(pageCount - mod) <= 1 ? 0 : 1);
							int targetPage = quotient * pageCount;
							setCurrentItem(targetPage, false);
							if (currentPage == targetPage) {
								if (targetPage == currentPage) {
									resumeLooping();
								}
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
							if (currentPage == targetPage) {
								if (targetPage == currentPage) {
									resumeLooping();
								}
							}
						}
					} else {
						scrollToEndPage(pageCount - 1);
					}
				}
			} else {
				if (isLooping) {
					int currentPage = recyclerView.getCurrentPage();
					int targetPage = currentPage / pageCount * pageCount + lastPage;
					setCurrentItem(targetPage, false);
					if (targetPage == currentPage) {
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
			layoutParams.topMargin = indicatorGroupHeight / 2 - unselectedIndicatorDiameter / 2;
		} else if (orientation == OnPageDataListener.VERTICAL) {
			layoutParams.leftMargin = indicatorGroupWidth / 2 - unselectedIndicatorDiameter / 2;
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
		if (isLooping) {
			if (firstEnter) {
				firstEnter = false;
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
		if (isLooping) {
			switch (state) {
				case RecyclerView.SCROLL_STATE_DRAGGING://1
					if (mHandler != null) {
						mHandler.sendEmptyMessage(PageHandler.MSG_STOP_LOOPING);
					}
					break;
				case RecyclerView.SCROLL_STATE_IDLE://0
					if (mHandler != null) {
						mHandler.sendEmptyMessageDelayed(PageHandler.MSG_START_LOOPING, interval);
					}
					break;
			}
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}
}
