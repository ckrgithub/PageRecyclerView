package com.ckr.pagesnaphelper.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.ckr.pagesnaphelper.adapter.OnPageDataListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by PC大佬 on 2018/1/14.
 */
public class PageRecyclerView extends RecyclerView {
	public static final String TAG = "PageRecyclerView";
	private static final int MAX_SCROLL_ON_FLING_DURATION = 100; // 最大滚动毫秒
	private final float MILLISECONDS_PER_PX;//每px滚动的毫秒
	private static final float MILLISECONDS_PER_INCH = 25f;//每英寸滚动的毫秒
	private int mWidth;
	private int column;
	private Method smoothScrollBy = null;
	private Field mViewFlingerField = null;
	protected final AccelerateDecelerateInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
	private int mScrollX;//x轴滚动距离
	private int mDeltaX;//拖动时x轴滚动偏移量
	private int mScrollState;
	private int mCurrentPage;
	private boolean isSliding;//是否是滑动
	private boolean forwardDirection;//滑动方向
	private OnPageChangeListener listener;
	private DecimalFormat decimalFormat;
	private OnPageDataListener onPageDataListener;

	public PageRecyclerView(Context context) {
		this(context, null);
	}

	public PageRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
		init();
	}

	private float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
		return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
	}

	private void init() {
		decimalFormat = new DecimalFormat("0.00");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		getScrollerByReflection();
		setOnFlingListener(new OnPageFlingListener());
		addOnLayoutChangeListener(new OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				removeOnLayoutChangeListener(this);
				mWidth = getWidth();
				Log.d(TAG, "onLayoutChange: width:" + mWidth);
			}
		});
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (adapter instanceof OnPageDataListener) {
			onPageDataListener = (OnPageDataListener) adapter;
			column = onPageDataListener.getPageColumn();
		}
		super.setAdapter(adapter);
	}

	/**
	 * {@link ViewFlinger}
	 */
	private void getScrollerByReflection() {
		Class<?> c = null;
		Class<?> ViewFlingerClass = null;
		try {
			c = Class.forName("android.support.v7.widget.RecyclerView");
			mViewFlingerField = c.getDeclaredField("mViewFlinger");
			mViewFlingerField.setAccessible(true);
			ViewFlingerClass = Class.forName(mViewFlingerField.getType().getName());
			smoothScrollBy = ViewFlingerClass.getDeclaredMethod("smoothScrollBy",
					int.class, int.class, int.class, Interpolator.class);
			smoothScrollBy.setAccessible(true);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private void smoothScrollBy(int dx, int dy, int duration) {
		if (dx == 0) {
			return;
		}
		try {
			Log.e(TAG, "smoothScrollBy,dx:" + dx + ",duration" + duration);
			smoothScrollBy.invoke(mViewFlingerField.get(this), dx, dy, duration, mInterpolator);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@link LinearSmoothScroller}
	 *
	 * @param dx
	 * @return
	 */
	private int calculateTimeForDeceleration(int dx) {
		int timeForScrolling = (int) Math.ceil(calculateTimeForScrolling(dx) / .3356);
		Log.d(TAG, "calculateTimeForDeceleration: timeForScrolling:" + timeForScrolling);
		return timeForScrolling;
	}

	/**
	 * {@link LinearSmoothScroller}
	 *
	 * @param dx
	 * @return
	 */
	protected int calculateTimeForScrolling(int dx) {
		int timeForScrolling = (int) Math.ceil(Math.abs(dx) * MILLISECONDS_PER_PX);
		Log.d(TAG, "calculateTimeForScrolling: timeForScrolling:" + timeForScrolling + ",MILLISECONDS_PER_PX:" + MILLISECONDS_PER_PX + ",dx:" + dx);
		return Math.min(MAX_SCROLL_ON_FLING_DURATION, timeForScrolling);
	}

	@Override
	public void onScrollStateChanged(int state) {
		Log.d(TAG, "onScrollStateChanged,mScrollState:" + state);
		this.mScrollState = state;
		if (listener != null) {
			listener.onPageScrollStateChanged(state);
		}
		switch (state) {
			case SCROLL_STATE_IDLE://0
				idleHandle();
				mDeltaX = 0;
				isSliding = false;
				break;
			case SCROLL_STATE_DRAGGING://1
				isInterrupt = false;
				break;
			case SCROLL_STATE_SETTLING://2
				isSliding = true;
				break;
		}
	}

	private void idleHandle() {
		if (mWidth != 0) {
			if (isSliding) {//放手后的滑动
				int t = mScrollX / mWidth;
				int deltaX = mScrollX - t * mWidth;
				if (!forwardDirection) {//向右滑
					deltaX = deltaX - mWidth;
				} else {//向左滑
				}
				Log.d(TAG, "isSliding=true,deltaX:" + deltaX + ",mScrollX:" + mScrollX);
				move(deltaX);
			} else {//用手拖动
				Log.d(TAG, "isSliding=false,mDeltaX:" + mDeltaX);
				move(mDeltaX);
			}
		}
	}

	private void move(int deltaX) {
		Log.d(TAG, "move,deltaX:" + deltaX + ",mCurrentPage:" + mCurrentPage);
		if (Math.abs(deltaX) == 0 || Math.abs(deltaX) == mWidth) {
			return;
		}
		int itemWidth = mWidth / column;
		if (deltaX >= itemWidth) {//下一页
			int moveX = mWidth - deltaX;
			Log.d(TAG, "move,moveX:" + moveX);
			smoothScrollBy(moveX, 0, calculateTimeForDeceleration(Math.abs(moveX)));
		} else if (deltaX <= -itemWidth) {//上一页
			int moveX = -(mWidth + deltaX);
			Log.d(TAG, "move,moveX:" + moveX);
			smoothScrollBy(moveX, 0, calculateTimeForDeceleration(Math.abs(moveX)));
		} else {//回弹
			Log.d(TAG, "move,deltaX:" + deltaX);
			smoothScrollBy(-deltaX, 0, calculateTimeForDeceleration(Math.abs(deltaX)));
		}
	}

	@Override
	public void onScrolled(int dx, int dy) {
		mScrollX += dx;
		if (mScrollState == SCROLL_STATE_DRAGGING) {
			mDeltaX += dx;
		}
		if (dx < 0) {
			forwardDirection = false;
		} else {
			forwardDirection = true;
		}
		Log.d(TAG, "onScrolled: mCurrentPage:" + mCurrentPage + ",mDeltaX:" + mDeltaX);
		if (mWidth == 0) {
			return;
		}
		if (dx < 0 && mScrollX % mWidth != 0) {
			int targetPage = mScrollX / mWidth + 1;
			limitScrollPage(targetPage);
			mCurrentPage = targetPage;
		} else {
			int targetPage = mScrollX / mWidth;
			limitScrollPage(targetPage);
			mCurrentPage = targetPage;
		}
		Log.d(TAG, "onScrolled,mScrollX:" + mScrollX + ",dx:" + dx + ",mCurrentPage:" + mCurrentPage);
		if (listener != null) {
			int positionOffsetPixels = mScrollX % mWidth;
			float positionOffset = Float.parseFloat(decimalFormat.format(mScrollX % mWidth / (double) mWidth));
			listener.onPageScrolled(mCurrentPage, positionOffset, positionOffsetPixels);
			if (positionOffsetPixels == 0) {
				listener.onPageSelected(mCurrentPage);
			}
		}
	}

	private void limitScrollPage(int page) {
		if (Math.abs(mCurrentPage - page) == 1 && isSliding) {//在滑动时，限制滑动一页
			stopScroll();
			int t = mScrollX / mWidth;
			int deltaX = mScrollX - t * mWidth;
			if (!forwardDirection) {//向右滑
				deltaX = mWidth - deltaX;
			} else {//向左滑
				deltaX = -deltaX;
			}
			Log.e(TAG, "limitScrollPage: deltaX:" + deltaX);
			scrollBy(deltaX, 0);
		}
	}

	@Override
	public boolean fling(int velocityX, int velocityY) {
		return super.fling(velocityX * 100 / 100, velocityY);
	}

	private class OnPageFlingListener extends OnFlingListener {
		/**
		 * {@link android.support.v7.widget.PagerSnapHelper}
		 *
		 * @param velocityX
		 * @param velocityY
		 * @return
		 */
		@Override
		public boolean onFling(int velocityX, int velocityY) {
			LayoutManager layoutManager = getLayoutManager();
			if (layoutManager == null) {
				return false;
			}
			Adapter adapter = getAdapter();
			if (adapter == null) {
				return false;
			}
			int minFlingVelocity = getMinFlingVelocity();
			boolean fling = (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
					&& snapFromFling(layoutManager, velocityX, velocityY);
			return fling;
		}
	}

	private boolean snapFromFling(@NonNull LayoutManager layoutManager, int velocityX,
								  int velocityY) {
		final int itemCount = layoutManager.getItemCount();
		if (itemCount == 0) {
			return false;
		}
		Log.d(TAG, "snapFromFling,mScrollState:" + this.mScrollState + ",isInterrupt:" + isInterrupt);
		if (SCROLL_STATE_DRAGGING == this.mScrollState) {
//			onScrollStateChanged(SCROLL_STATE_IDLE);
			/*this.mScrollState = SCROLL_STATE_IDLE;
			if (listener != null) {
				listener.onPageScrollStateChanged(SCROLL_STATE_IDLE);
			}*/
			Log.e(TAG, "snapFromFling: isSliding:" + isSliding + ",page:" + mCurrentPage + ",scrollX:"
					+ mScrollX + ",direction:" + forwardDirection+",deltaX:"+mDeltaX);
//			idleHandle();
			int moveX = 0;
			if (forwardDirection) {
				int targetPage = mScrollX / mWidth + 1;
				moveX = targetPage * mWidth - mScrollX;
			} else {
//				int targetPage = mScrollX / mWidth;
				moveX = mDeltaX;
			}
			if (Math.abs(moveX) != 0 && Math.abs(moveX) != mWidth) {
				smoothScrollBy(moveX, 0, calculateTimeForDeceleration(Math.abs(moveX)));
			}
			mDeltaX = 0;
//			isSliding = false;
			return true;
		} else {
			return false;
		}
		/*if (SCROLL_STATE_IDLE != this.mScrollState) {
			if (isInterrupt) {
				return false;
			} else {

				isInterrupt = true;
			}
		} else {
			isInterrupt = false;
		}
		Log.d(TAG, "snapFromFling: isInterrupt:"+isInterrupt);
		return isInterrupt;*/
	}

	boolean isInterrupt;

	public void addOnPageChangeListener(OnPageChangeListener listener) {
		this.listener = listener;
	}

	public interface OnPageChangeListener {
		void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		void onPageSelected(int position);

		void onPageScrollStateChanged(int state);
	}
}
