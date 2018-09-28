package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.ckr.pageview.adapter.OnPageDataListener;
import com.ckr.pageview.transform.DepthPageTransformer;
import com.ckr.pageview.transform.StackTransformer;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.ckr.pageview.utils.PageLog.Logd;
import static com.ckr.pageview.utils.PageLog.Loge;

/**
 * Created by PC大佬 on 2018/7/14.
 */

public class PageRecyclerView extends RecyclerView implements RecyclerView.ChildDrawingOrderCallback {
	private static final String TAG = "PageRecyclerView";
	private static final String ARGS_SCROLL_OFFSET = "mScrollOffset";
	private static final String ARGS_PAGE = "mCurrentPage";
	private static final String ARGS_SUPER = "super";
	private static final String ARGS_WIDTH = "mScrollWidth";
	private static final String ARGS_HEIGHT = "mScrollHeight";
	private static final String ARGS_FORWARD_DIRECTION = "forwardDirection";
	private static final String ARGS_SAVE_STATE = "isSaveState";
	private static final int MAX_SETTLE_DURATION = 600; // ms
	private static final int DEFAULT_VELOCITY = 4000;
	private int mVelocity = DEFAULT_VELOCITY;
	private int mScrollWidth;
	private int mScrollHeight;
	private int mOrientation;
	private int mScrollOffset;//滚动偏移量
	private int mDragOffset;//拖动时偏移量
	private int mScrollState;
	private int mCurrentPage;
	private int mLastPage;
	private boolean mFirstLayout = true;
	private boolean mIsLooping = false;
	private boolean isSliding;//是否是滑动
	private boolean forwardDirection;//滑动方向
	private DecimalFormat decimalFormat;
	private PageRecyclerView.OnPageChangeListener mOnPageChangeListener;
	private PageRecyclerView.PageTransformer mPageTransformer;
	private boolean isSaveState;

	public PageRecyclerView(Context context) {
		this(context, null);
	}

	public PageRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		decimalFormat = new DecimalFormat("0.00");
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		setOnFlingListener(new PageRecyclerView.OnPageFlingListener());
		addOnLayoutChangeListener(new OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				removeOnLayoutChangeListener(this);
				int paddingLeft = getPaddingLeft();
				int paddingRight = getPaddingRight();
				int paddingTop = getPaddingTop();
				int paddingBottom = getPaddingBottom();
				Logd(TAG, "onLayoutChange: paddingLeft:" + paddingLeft + ",paddingRight:" + paddingRight + ",paddingTop:" + paddingTop + ",paddingBottom:" + paddingBottom);
				mScrollWidth = getWidth() - paddingLeft - paddingRight;
				mScrollHeight = getHeight() - paddingTop - paddingBottom;
				Logd(TAG, "onLayoutChange: mScrollWidth:" + mScrollWidth + ",mScrollHeight:" + mScrollHeight
						+ ",mCurrentPage:" + mCurrentPage + ",mFirstLayout:" + mFirstLayout + ",isSaveState:" + isSaveState
						+ ",forwardDirection:" + forwardDirection);
				label:
				if (mFirstLayout) {
					if (mOrientation == OnPageDataListener.HORIZONTAL) {
						int mOffset = mScrollOffset;
						mScrollOffset = mCurrentPage * mScrollWidth;
						if (mScrollWidth == 0) {
							break label;
						}
						if (mIsLooping && !isSaveState) {
							PageRecyclerView.super.scrollToPosition(mCurrentPage);
						} else {
							int remainder = mOffset % mScrollWidth;
							Loge(TAG, "onLayoutChange: remainder:" + remainder);
							if (remainder != 0) {
								mScrollOffset = mOffset;
								int moveX = mScrollWidth - remainder;
								if (forwardDirection) {
									smoothScrollBy(moveX, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(moveX)));
								} else {
									smoothScrollBy(-remainder, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(remainder)));
								}
							}
						}
					} else {
						int mOffset = mScrollOffset;
						mScrollOffset = mCurrentPage * mScrollHeight;
						if (mScrollHeight == 0) {
							break label;
						}
						if (mIsLooping && !isSaveState) {
							PageRecyclerView.super.scrollToPosition(mCurrentPage);
						} else {
							int remainder = mOffset % mScrollHeight;
							if (remainder != 0) {
								mScrollOffset = mOffset;
								int moveY = mScrollHeight - remainder;
								if (forwardDirection) {
									smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(mVelocity, Math.abs(moveY)));
								} else {
									smoothScrollBy(0, -remainder, calculateTimeForVerticalScrolling(mVelocity, Math.abs(remainder)));
								}
							}
						}
					}
				}
				mFirstLayout = false;
				isSaveState = false;
				Logd(TAG, "onLayoutChange: mScrollOffset:" + mScrollOffset);
			}
		});
		setChildDrawingOrderCallback(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Logd(TAG, "onSizeChanged: w:" + w + ",h:" + h);
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		mScrollWidth = getWidth() - paddingLeft - paddingRight;
		mScrollHeight = getHeight() - paddingTop - paddingBottom;
		if (mIsLooping) {
			if (mOrientation == OnPageDataListener.HORIZONTAL) {
				mScrollOffset = mCurrentPage * mScrollWidth;
			} else {
				mScrollOffset = mCurrentPage * mScrollHeight;
			}
			post(new Runnable() {
				@Override
				public void run() {
					scrollToPosition(mCurrentPage);
				}
			});
		}
	}

	@Override
	public int onGetChildDrawingOrder(int childCount, int i) {
		Logd(TAG, "onGetChildDrawingOrder: childCount:" + childCount + ",i:" + i);
		if (mPageTransformer != null) {
			String name = mPageTransformer.getClass().getName();
			if (name == StackTransformer.class.getName()
					|| name == DepthPageTransformer.class.getName()) {//自定义Item绘制顺序
				if (childCount == 2) {
					return 0 == i ? childCount - 1 : 0;
				}
			}
		}
		return i;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setOrientation(@OnPageDataListener.LayoutOrientation int mOrientation) {
		this.mOrientation = mOrientation;
	}

	public void setLooping(boolean isLooping) {
		this.mIsLooping = isLooping;
	}

	public void setVelocity(@IntRange(from = 1) int mVelocity) {
		this.mVelocity = mVelocity;
	}

	private void smoothScrollBy(int dx, int dy, int duration) {
		super.mViewFlinger.smoothScrollBy(dx, dy, duration, sQuinticInterpolator);
	}

	@Override
	public void onScrollStateChanged(int state) {
		Logd(TAG, "onScrollStateChanged,mScrollState:" + state);
		this.mScrollState = state;
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
		switch (state) {
			case SCROLL_STATE_IDLE://0
				if (mOrientation == OnPageDataListener.HORIZONTAL) {
					if (mScrollWidth != 0) {
						if (isSliding) {//放手后的滑动
							int t = mScrollOffset / mScrollWidth;
							int deltaX = mScrollOffset - t * mScrollWidth;
							if (!forwardDirection) {//向前
								deltaX = deltaX - mScrollWidth;
							} else {//向后
							}
							Logd(TAG, "isSliding=true,deltaX:" + deltaX + ",mScrollOffset:" + mScrollOffset);
							moveX(deltaX);
						} else {//用手拖动
							Logd(TAG, "isSliding=false,mDragOffset:" + mDragOffset);
							moveX(mDragOffset);
						}
					}
				} else {
					if (mScrollHeight != 0) {
						if (isSliding) {//放手后的滑动
							int t = mScrollOffset / mScrollHeight;
							int deltaY = mScrollOffset - t * mScrollHeight;
							if (!forwardDirection) {
								deltaY = deltaY - mScrollHeight;
							} else {
							}
							Logd(TAG, "isSliding=true,deltaY:" + deltaY + ",mScrollOffset:" + mScrollOffset);
							moveY(deltaY);
						} else {//用手拖动
							Logd(TAG, "isSliding=false,mDragOffset:" + mDragOffset);
							moveY(mDragOffset);
						}
					}
				}
				mDragOffset = 0;
				isSliding = false;
				break;
			case SCROLL_STATE_DRAGGING://1
				break;
			case SCROLL_STATE_SETTLING://2
				isSliding = true;
				break;
		}
	}

	private void moveX(int deltaX) {
		Logd(TAG, "move,deltaX:" + deltaX + ",mCurrentPage:" + mCurrentPage);
		if (Math.abs(deltaX) == 0 || Math.abs(deltaX) == mScrollWidth) {
			return;
		}
		int itemWidth = mScrollWidth / 2;
		if (deltaX >= itemWidth) {//下一页
			int moveX = mScrollWidth - deltaX;
			Logd(TAG, "move,deltaX:" + moveX);
			smoothScrollBy(moveX, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(moveX)));
		} else if (deltaX <= -itemWidth) {//上一页
			int moveX = -(mScrollWidth + deltaX);
			Logd(TAG, "move,deltaX:" + moveX);
			smoothScrollBy(moveX, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(moveX)));
		} else {//回弹
			Logd(TAG, "move,deltaX:" + deltaX);
			smoothScrollBy(-deltaX, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(deltaX)));
		}
	}

	private void moveY(int deltaY) {
		Logd(TAG, "move,deltaY:" + deltaY + ",mCurrentPage:" + mCurrentPage);
		if (Math.abs(deltaY) == 0 || Math.abs(deltaY) == mScrollHeight) {
			return;
		}
		int itemHeight = mScrollHeight / 2;
		if (deltaY >= itemHeight) {//下一页
			int moveY = mScrollHeight - deltaY;
			Logd(TAG, "move,deltaY:" + moveY);
			smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(mVelocity, Math.abs(moveY)));
		} else if (deltaY <= -itemHeight) {//上一页
			int moveY = -(mScrollHeight + deltaY);
			Logd(TAG, "move,deltaY:" + moveY);
			smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(mVelocity, Math.abs(moveY)));
		} else {//回弹
			Logd(TAG, "move,deltaY:" + deltaY);
			smoothScrollBy(0, -deltaY, calculateTimeForVerticalScrolling(mVelocity, Math.abs(deltaY)));
		}
	}

	@Override
	public void onScrolled(int dx, int dy) {
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			mScrollOffset += dx;
			if (mScrollState == SCROLL_STATE_DRAGGING) {
				mDragOffset += dx;
			}
			if (dx < 0) {
				forwardDirection = false;
			} else if (dx > 0) {
				forwardDirection = true;
			}
			Logd(TAG, "onScrolled: mScrollOffset:" + mScrollOffset + ",mCurrentPage:" + mCurrentPage +
					",mDragOffset:" + mDragOffset + ",forwardDirection:" + forwardDirection + ",mScrollWidth:" + mScrollWidth);
			if (mScrollWidth == 0) {
				return;
			}
			mLastPage = mCurrentPage;
			if (mScrollOffset % mScrollWidth == 0) {
				mCurrentPage = mScrollOffset / mScrollWidth;
			} else if (dx < 0) {
				int targetPage = mScrollOffset / mScrollWidth + 1;
				mCurrentPage = Math.min(targetPage, mCurrentPage);
			} else {
				int targetPage = mScrollOffset / mScrollWidth;
				mCurrentPage = Math.max(targetPage, mCurrentPage);
			}
			if (mOnPageChangeListener != null) {
				int positionOffsetPixels = mScrollOffset % mScrollWidth;
				float positionOffset = Float.parseFloat(decimalFormat.format(mScrollOffset % mScrollWidth / (double) mScrollWidth));
				mOnPageChangeListener.onPageScrolled(mCurrentPage, positionOffset, positionOffsetPixels);
				if (mLastPage - mCurrentPage != 0 || positionOffset == 0) {
					mOnPageChangeListener.onPageSelected(mCurrentPage);
				}
			}
			if (mPageTransformer != null) {
				int scrollX = getScrollX();
				int childCount = getChildCount();
				for (int i = 0; i < childCount; i++) {
					View child = getChildAt(i);
					int left = child.getLeft();
					float transformPos = (left - scrollX - getPaddingLeft()) / (float) mScrollWidth;
					boolean nextPage = mScrollOffset >= mLastPage * mScrollWidth;
					Logd(TAG, "onScrolled: transformPos:" + transformPos + ",left:" + left
							+ ",mScrollWidth:" + mScrollWidth + ",childCount:" + childCount
							+ ",nextPage:" + nextPage);
					mPageTransformer.transformPage(child, transformPos, nextPage, mOrientation);
				}
			}
		} else {
			mScrollOffset += dy;
			if (mScrollState == SCROLL_STATE_DRAGGING) {
				mDragOffset += dy;
			}
			if (dy < 0) {
				forwardDirection = false;
			} else if (dy > 0) {
				forwardDirection = true;
			}
			Logd(TAG, "onScrolled: mScrollOffset:" + mScrollOffset + ",mCurrentPage:" + mCurrentPage +
					",mDragOffset:" + mDragOffset + ",forwardDirection:" + forwardDirection + ",mScrollHeight：" + mScrollHeight);
			if (mScrollHeight == 0) {
				return;
			}
			mLastPage = mCurrentPage;
			if (mScrollOffset % mScrollHeight == 0) {
				mCurrentPage = mScrollOffset / mScrollHeight;
			} else if (dy < 0) {
				int targetPage = mScrollOffset / mScrollHeight + 1;
				mCurrentPage = Math.min(targetPage, mCurrentPage);
			} else {
				int targetPage = mScrollOffset / mScrollHeight;
				mCurrentPage = Math.max(targetPage, mCurrentPage);
			}
			if (mOnPageChangeListener != null) {
				int positionOffsetPixels = mScrollOffset % mScrollHeight;
				float positionOffset = Float.parseFloat(decimalFormat.format(mScrollOffset % mScrollHeight / (double) mScrollHeight));
				mOnPageChangeListener.onPageScrolled(mCurrentPage, positionOffset, positionOffsetPixels);
				if (mLastPage - mCurrentPage != 0 || positionOffset == 0) {
					mOnPageChangeListener.onPageSelected(mCurrentPage);
				}
			}
			if (mPageTransformer != null) {
				int scrollY = getScrollY();
				int childCount = getChildCount();
				for (int i = 0; i < childCount; i++) {
					View child = getChildAt(i);
					int top = child.getTop();
					float transformPos = (top - scrollY - getPaddingTop()) / (float) mScrollHeight;
					boolean nextPage = mScrollOffset >= mLastPage * mScrollHeight;
					Logd(TAG, "onScrolled: transformPos:" + transformPos + ",top:" + top
							+ ",mScrollHeight:" + mScrollHeight + ",childCount:" + childCount
							+ ",nextPage:" + nextPage);
					mPageTransformer.transformPage(child, transformPos, nextPage, mOrientation);
				}
			}
		}
		Logd(TAG, "onScrolled,mCurrentPage:" + mCurrentPage);
	}

	public void scrollToEndPage(@IntRange(from = 0) int page) {
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			mScrollOffset = page * mScrollWidth;
		} else {
			mScrollOffset = page * mScrollHeight;
		}
		super.scrollToPosition(getAdapter().getItemCount() - 1);
	}

	public void scrollToBeginPage() {
		mScrollOffset = 0;
		super.scrollToPosition(0);
	}

	public void scrollToPage(@IntRange(from = 0) int page, boolean smoothScroll) {
		if (mCurrentPage == page) {
			return;
		}
		if (mFirstLayout) {
			mCurrentPage = page;
		} else {
			if (mOrientation == OnPageDataListener.HORIZONTAL) {
				int scrollX = page * mScrollWidth;
				int moveX = scrollX - mScrollOffset;
				if (mScrollWidth == 0) {//中断recyclerView滚动
					mCurrentPage = page;
					return;
				}
				if (smoothScroll) {
					smoothScrollBy(moveX, 0, calculateTimeForHorizontalScrolling(mVelocity, moveX));
				} else {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//compat recyclerview-v7-26.1.0 above
						scrollBy(moveX, 0);
					} else {
						smoothScrollBy(moveX, 0, 0);
					}
				}
			} else {
				int scrollY = page * mScrollHeight;
				int moveY = scrollY - mScrollOffset;
				if (mScrollHeight == 0) {//中断recyclerView滚动
					mCurrentPage = page;
					return;
				}
				if (smoothScroll) {
					smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(mVelocity, moveY));
				} else {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//compat recyclerview-v7-26.1.0
						scrollBy(0, moveY);
					} else {
						smoothScrollBy(0, moveY, 0);
					}
				}
			}
		}
		Logd(TAG, "scrollToPage: mCurrentPage:" + mCurrentPage + ",page:" + page);
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
		Logd(TAG, "snapFromFling,mScrollState:" + this.mScrollState + ",velocityX:" + velocityX + ",velocityY:" + velocityY);
		if (SCROLL_STATE_DRAGGING == this.mScrollState) {
			if (mOrientation == OnPageDataListener.HORIZONTAL) {
				int moveX = getMoveDistance(mScrollOffset, mScrollWidth);
				Loge(TAG, "snapFromFling: deltaX:" + moveX + ",mCurrentPage:" + mCurrentPage);
				if (Math.abs(moveX) != 0 && Math.abs(moveX) != mScrollWidth) {
					smoothScrollBy(moveX, 0, calculateTimeForHorizontalScrolling(velocityX, moveX));
				}
			} else {
				int moveY = getMoveDistance(mScrollOffset, mScrollHeight);
				Loge(TAG, "snapFromFling: deltaY:" + moveY + ",mCurrentPage:" + mCurrentPage);
				if (Math.abs(moveY) != 0 && Math.abs(moveY) != mScrollHeight) {
					smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(velocityY, moveY));
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private int getMoveDistance(int scrollDistance, int length) {
		int moveDistance;
		if (forwardDirection) {
			int targetPage = scrollDistance / length + 1;
			moveDistance = targetPage * length - scrollDistance;
		} else {
			int targetPage = scrollDistance / length;
			moveDistance = targetPage * length - scrollDistance;
		}
		return moveDistance;
	}

	/**
	 * {@link android.support.v4.view.ViewPager}的smoothScrollTo(int,int ,int)
	 *
	 * @param velocity
	 * @param dx
	 * @return
	 */
	private int calculateTimeForHorizontalScrolling(int velocity, int dx) {
		final int width = mScrollWidth;
		final int halfWidth = width / 2;
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		final float distance = halfWidth + halfWidth
				* distanceInfluenceForSnapDuration(distanceRatio);
		int duration;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			final float pageWidth = width * 1.0f;
			final float pageDelta = (float) Math.abs(dx) / (pageWidth);
			duration = (int) ((pageDelta + 1) * 100);
		}
		duration = Math.min(duration, MAX_SETTLE_DURATION);
		return duration;
	}

	private int calculateTimeForVerticalScrolling(int velocity, int dy) {
		final int height = mScrollHeight;
		final int halfHeight = height / 2;
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dy) / height);
		final float distance = halfHeight + halfHeight
				* distanceInfluenceForSnapDuration(distanceRatio);
		int duration;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			final float pageWidth = height * 1.0f;
			final float pageDelta = (float) Math.abs(dy) / (pageWidth);
			duration = (int) ((pageDelta + 1) * 100);
		}
		duration = Math.min(duration, MAX_SETTLE_DURATION);
		return duration;
	}

	private float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f; // center the values about 0.
		f *= 0.3f * Math.PI / 2.0f;
		return (float) Math.sin(f);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		mScrollOffset = bundle.getInt(ARGS_SCROLL_OFFSET, 0);
		mCurrentPage = bundle.getInt(ARGS_PAGE, 0);
		mScrollWidth = bundle.getInt(ARGS_WIDTH, 0);
		mScrollHeight = bundle.getInt(ARGS_HEIGHT, 0);
		forwardDirection = bundle.getBoolean(ARGS_FORWARD_DIRECTION, true);
		isSaveState = bundle.getBoolean(ARGS_SAVE_STATE, false);
		Parcelable parcelable = bundle.getParcelable(ARGS_SUPER);
		Logd(TAG, "onLayoutChange onRestoreInstanceState: mOrientation:" + mOrientation + ",mScrollOffset:" + mScrollOffset + ",mScrollWidth:" + mScrollWidth
				+ ",mScrollHeight:" + mScrollHeight + ",mCurrentPage:" + mCurrentPage + ",forwardDirection:" + forwardDirection);
		super.onRestoreInstanceState(parcelable);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Logd(TAG, "onLayoutChange onSaveInstanceState: mOrientation:" + mOrientation + ",mScrollOffset:" + mScrollOffset + ",mScrollWidth:" + mScrollWidth
				+ ",mScrollHeight:" + mScrollHeight + ",mCurrentPage:" + mCurrentPage + ",forwardDirection:" + forwardDirection);
		Bundle bundle = new Bundle();
		bundle.putInt(ARGS_SCROLL_OFFSET, mScrollOffset);
		bundle.putInt(ARGS_PAGE, mCurrentPage);
		bundle.putInt(ARGS_WIDTH, mScrollWidth);
		bundle.putInt(ARGS_HEIGHT, mScrollHeight);
		bundle.putBoolean(ARGS_FORWARD_DIRECTION, forwardDirection);
		bundle.putBoolean(ARGS_SAVE_STATE, true);
		bundle.putParcelable(ARGS_SUPER, super.onSaveInstanceState());
		return bundle;
	}

	public void addOnPageChangeListener(PageRecyclerView.OnPageChangeListener listener) {
		this.mOnPageChangeListener = listener;
	}

	public void addPageTransformer(PageRecyclerView.PageTransformer pageTransformer) {
		this.mPageTransformer = pageTransformer;
	}

	public interface OnPageChangeListener {
		void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		void onPageSelected(int position);

		void onPageScrollStateChanged(int state);
	}

	public interface PageTransformer {
		void transformPage(View page, float position, boolean forwardDirection, @OnPageDataListener.LayoutOrientation int mOrientation);
	}
}
