package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.ckr.pageview.adapter.BasePageAdapter;
import com.ckr.pageview.adapter.OnPageDataListener;
import com.ckr.pageview.transform.DepthPageTransformer;
import com.ckr.pageview.transform.StackTransformer;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.ckr.pageview.utils.PageLog.Logd;
import static com.ckr.pageview.utils.PageLog.Loge;
import static com.ckr.pageview.utils.PageLog.Logi;
import static com.ckr.pageview.utils.PageLog.Logv;

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
    private static final String ARGS_FORWARD_DIRECTION = "mForwardDirection";
    private static final String ARGS_SAVE_STATE = "isSaveState";
    private static final int DEFAULT_VELOCITY = 4000;
    private static final int MODE_DEFAULT = 0;
    private static final int MODE_AUTO_WIDTH = 1;
    private static final int MODE_AUTO_HEIGHT = 2;
    private static final int INIT_VALUE = -1;
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
    private boolean mForwardDirection;//滑动方向
    private DecimalFormat mDecimalFormat;
    private PageRecyclerView.OnPageChangeListener mOnPageChangeListener;
    private PageRecyclerView.PageTransformer mPageTransformer;
    private boolean isSaveState;
    private boolean isOnSizeChanged;
    private int mSize = INIT_VALUE;
    private int mMeasureMode = MODE_DEFAULT;
    private int maxScrollDuration = 0;
    private int minScrollDuration = 0;
    private boolean enableTouchScroll = true;
    private Method smoothScrollBy = null;
    private Field mViewFlingerField = null;

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

    public void setMaxScrollDuration(int maxScrollDuration) {
        this.maxScrollDuration = maxScrollDuration;
    }

    public void setMinScrollDuration(int minScrollDuration) {
        this.minScrollDuration = minScrollDuration;
    }

    public void setEnableTouchScroll(boolean enableTouchScroll) {
        this.enableTouchScroll = enableTouchScroll;
    }

    private void init() {
        mDecimalFormat = new DecimalFormat("0.00");
        mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        getScrollerByReflection();
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
                        + ",mForwardDirection:" + mForwardDirection);
                label:
                if (mFirstLayout) {
                    if (mOrientation == OnPageDataListener.HORIZONTAL) {
                        notifySizeChanged(mScrollWidth);
                        mMeasureMode = MODE_AUTO_WIDTH;
                        int mOffset = mScrollOffset;
                        mScrollOffset = mCurrentPage * mScrollWidth;
                        if (mScrollWidth == 0) {
                            break label;
                        }
                        if (mIsLooping && !isSaveState) {
                            PageRecyclerView.super.scrollToPosition(mCurrentPage);
                        } else {
                            if (!mIsLooping && isOnSizeChanged) {//屏幕大小变化，如：横竖屏切换
                                int lastScrollOffset = mCurrentPage * mScrollWidth;
                                mCurrentPage = 0;
                                mScrollOffset = 0;
                                smoothScrollBy(lastScrollOffset, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(lastScrollOffset)));
                            } else {
                                int remainder = mOffset % mScrollWidth;
                                Loge(TAG, "onLayoutChange: remainder:" + remainder);
                                if (remainder != 0) {//滑动中，页面切换回来后继续惯性滑动
                                    mScrollOffset = mOffset;
                                    int moveX = mScrollWidth - remainder;
                                    if (mForwardDirection) {
                                        smoothScrollBy(moveX, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(moveX)));
                                    } else {
                                        smoothScrollBy(-remainder, 0, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(remainder)));
                                    }
                                }
                            }
                        }
                    } else {
                        notifySizeChanged(mScrollHeight);
                        mMeasureMode = MODE_AUTO_HEIGHT;
                        int mOffset = mScrollOffset;
                        mScrollOffset = mCurrentPage * mScrollHeight;
                        if (mScrollHeight == 0) {
                            break label;
                        }
                        if (mIsLooping && !isSaveState) {
                            PageRecyclerView.super.scrollToPosition(mCurrentPage);
                        } else {
                            if (!mIsLooping && isOnSizeChanged) {
                                int lastScrollOffset = mCurrentPage * mScrollHeight;
                                mCurrentPage = 0;
                                mScrollOffset = 0;
                                smoothScrollBy(0, lastScrollOffset, calculateTimeForHorizontalScrolling(mVelocity, Math.abs(lastScrollOffset)));
                            } else {
                                int remainder = mOffset % mScrollHeight;
                                if (remainder != 0) {
                                    mScrollOffset = mOffset;
                                    int moveY = mScrollHeight - remainder;
                                    if (mForwardDirection) {
                                        smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(mVelocity, Math.abs(moveY)));
                                    } else {
                                        smoothScrollBy(0, -remainder, calculateTimeForVerticalScrolling(mVelocity, Math.abs(remainder)));
                                    }
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

    /**
     * {@link ViewFlinger}
     */
    private void getScrollerByReflection() {
        Class<?> c = null;
        Class<?> ViewFlingerClass = null;
        try {
            c = Class.forName(getClass().getSuperclass().getName());
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

    private void notifySizeChanged(int size) {
        Logd(TAG, "notifySizeChanged: size:" + size);
        if (this.mSize != size) {
            Adapter adapter = getAdapter();
            if (adapter != null && adapter instanceof BasePageAdapter) {
                BasePageAdapter pageAdapter = (BasePageAdapter) adapter;
                if (pageAdapter.isAutoSize()) {
                    pageAdapter.notifySizeChanged(size);
                    Loge(TAG, "notifySizeChanged: s:" + this.mSize);
                    if (this.mSize == INIT_VALUE) {
                        setAdapter(adapter);
                    }
                }
            }
        }
        this.mSize = size;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Logi(TAG, "onSizeChanged: w:" + w + ",h:" + h);
        isOnSizeChanged = true;
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
        Logv(TAG, "onGetChildDrawingOrder: childCount:" + childCount + ",i:" + i);
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

    public int getSize() {
        return mSize;
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
        try {
            smoothScrollBy.invoke(mViewFlingerField.get(this), dx, dy, duration, sQuinticInterpolator);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!enableTouchScroll) {
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!enableTouchScroll) {
            return true;
        }
        return super.onTouchEvent(e);
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
                            if (!mForwardDirection) {//向前
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
                            if (!mForwardDirection) {
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
                mForwardDirection = false;
            } else if (dx > 0) {
                mForwardDirection = true;
            }
            Logd(TAG, "onScrolled: mScrollOffset:" + mScrollOffset + ",mCurrentPage:" + mCurrentPage +
                    ",mDragOffset:" + mDragOffset + ",mForwardDirection:" + mForwardDirection + ",mScrollWidth:" + mScrollWidth);
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
                float positionOffset = Float.parseFloat(mDecimalFormat.format(mScrollOffset % mScrollWidth / (double) mScrollWidth));
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
                mForwardDirection = false;
            } else if (dy > 0) {
                mForwardDirection = true;
            }
            Logd(TAG, "onScrolled: mScrollOffset:" + mScrollOffset + ",mCurrentPage:" + mCurrentPage +
                    ",mDragOffset:" + mDragOffset + ",mForwardDirection:" + mForwardDirection + ",mScrollHeight：" + mScrollHeight);
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
                float positionOffset = Float.parseFloat(mDecimalFormat.format(mScrollOffset % mScrollHeight / (double) mScrollHeight));
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
                    return true;
                }
            } else {
                int moveY = getMoveDistance(mScrollOffset, mScrollHeight);
                Loge(TAG, "snapFromFling: deltaY:" + moveY + ",mCurrentPage:" + mCurrentPage);
                if (Math.abs(moveY) != 0 && Math.abs(moveY) != mScrollHeight) {
                    smoothScrollBy(0, moveY, calculateTimeForVerticalScrolling(velocityY, moveY));
                    return true;
                }
            }
        }
        return false;
    }

    private int getMoveDistance(int scrollDistance, int length) {
        int moveDistance;
        if (mForwardDirection) {
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
        duration = Math.max(Math.min(duration, maxScrollDuration), minScrollDuration);
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
        duration = Math.max(Math.min(duration, maxScrollDuration), minScrollDuration);
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
        mForwardDirection = bundle.getBoolean(ARGS_FORWARD_DIRECTION, true);
        isSaveState = bundle.getBoolean(ARGS_SAVE_STATE, false);
        Parcelable parcelable = bundle.getParcelable(ARGS_SUPER);
        Logv(TAG, "onLayoutChange onRestoreInstanceState: mOrientation:" + mOrientation + ",mScrollOffset:" + mScrollOffset + ",mScrollWidth:" + mScrollWidth
                + ",mScrollHeight:" + mScrollHeight + ",mCurrentPage:" + mCurrentPage + ",mForwardDirection:" + mForwardDirection);
        super.onRestoreInstanceState(parcelable);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Logv(TAG, "onLayoutChange onSaveInstanceState: mOrientation:" + mOrientation + ",mScrollOffset:" + mScrollOffset + ",mScrollWidth:" + mScrollWidth
                + ",mScrollHeight:" + mScrollHeight + ",mCurrentPage:" + mCurrentPage + ",mForwardDirection:" + mForwardDirection);
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_SCROLL_OFFSET, mScrollOffset);
        bundle.putInt(ARGS_PAGE, mCurrentPage);
        bundle.putInt(ARGS_WIDTH, mScrollWidth);
        bundle.putInt(ARGS_HEIGHT, mScrollHeight);
        bundle.putBoolean(ARGS_FORWARD_DIRECTION, mForwardDirection);
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
