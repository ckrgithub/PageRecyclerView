package com.ckr.pageview.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by PC大佬 on 2018/1/14.
 */
public class PageRecyclerView extends RecyclerView {
    private static final String TAG = "PageRecyclerView";
    public static final String SCROLL_X = "mScrollX";
    public static final String ARGS_PAGE = "mCurrentPage";
    public static final String ARGS_SUPER = "super";
    public static final String ARGS_WIDTH = "mWidth";
    private static final int MAX_SETTLE_DURATION = 600; // ms
    private int mWidth;
    private int mColumn;
    private Method smoothScrollBy = null;
    private Field mViewFlingerField = null;
    private static final Interpolator mInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private int mScrollX;//x轴滚动距离
    private int mDeltaX;//拖动时x轴滚动偏移量
    private int mScrollState;
    private int mCurrentPage;
    private boolean isSliding;//是否是滑动
    private boolean forwardDirection;//滑动方向
    private OnPageChangeListener listener;
    private DecimalFormat decimalFormat;

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
        getScrollerByReflection();
        setOnFlingListener(new OnPageFlingListener());
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                mWidth = getWidth();
            }
        });
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setColumn(int mColumn) {
        this.mColumn = mColumn;
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

    @Override
    public void onScrollStateChanged(int state) {
        Log.d(TAG, "onScrollStateChanged,mScrollState:" + state);
        this.mScrollState = state;
        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }
        switch (state) {
            case SCROLL_STATE_IDLE://0
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
                        Log.d(TAG, "isSliding=false,deltaX:" + mDeltaX);
                        move(mDeltaX);
                    }
                }
                mDeltaX = 0;
                isSliding = false;
                break;
            case SCROLL_STATE_DRAGGING://1
                break;
            case SCROLL_STATE_SETTLING://2
                isSliding = true;
                break;
        }
    }

    private void move(int deltaX) {
        Log.d(TAG, "move,deltaX:" + deltaX + ",mCurrentPage:" + mCurrentPage);
        if (Math.abs(deltaX) == 0 || Math.abs(deltaX) == mWidth) {
            return;
        }
        int itemWidth = mWidth / Math.max(2, mColumn);
        if (deltaX >= itemWidth) {//下一页
            int moveX = mWidth - deltaX;
            Log.d(TAG, "move,deltaX:" + moveX);
            smoothScrollBy(moveX, 0, calculateTimeForViewPager(4000, Math.abs(moveX)));
        } else if (deltaX <= -itemWidth) {//上一页
            int moveX = -(mWidth + deltaX);
            Log.d(TAG, "move,deltaX:" + moveX);
            smoothScrollBy(moveX, 0, calculateTimeForViewPager(4000, Math.abs(moveX)));
        } else {//回弹
            Log.d(TAG, "move,deltaX:" + deltaX);
            smoothScrollBy(-deltaX, 0, calculateTimeForViewPager(4000, Math.abs(deltaX)));
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
        Log.d(TAG, "onScrolled: mScrollX:" + mScrollX + ",mCurrentPage:" + mCurrentPage +
                ",mDeltaX:" + mDeltaX + ",forwardDirection:" + forwardDirection);
        if (mWidth == 0) {
            return;
        }
        if (dx < 0 && mScrollX % mWidth != 0) {
            int targetPage = mScrollX / mWidth + 1;
            mCurrentPage = targetPage;
        } else {
            int targetPage = mScrollX / mWidth;
            mCurrentPage = targetPage;
        }
        Log.d(TAG, "onScrolled,mCurrentPage:" + mCurrentPage);
        if (listener != null) {
            int positionOffsetPixels = mScrollX % mWidth;
            float positionOffset = Float.parseFloat(decimalFormat.format(mScrollX % mWidth / (double) mWidth));
            listener.onPageScrolled(mCurrentPage, positionOffset, positionOffsetPixels);
            if (positionOffsetPixels == 0) {
                listener.onPageSelected(mCurrentPage);
            }
        }
    }

    @Override
    public void setScrollX(@Px int value) {
        mScrollX = value;
        super.setScrollX(value);
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
        Log.d(TAG, "snapFromFling,mScrollState:" + this.mScrollState + ",velocityX:" + velocityX);
        if (SCROLL_STATE_DRAGGING == this.mScrollState) {
            int moveX = 0;
            if (forwardDirection) {
                int targetPage = mScrollX / mWidth + 1;
                moveX = targetPage * mWidth - mScrollX;
            } else {
                int targetPage = mScrollX / mWidth;//1941
                moveX = targetPage * mWidth - mScrollX;
            }
            Log.e(TAG, "snapFromFling: deltaX:" + moveX + ",mCurrentPage:" + mCurrentPage + ",mScrollX:" + mScrollX);
            if (Math.abs(moveX) != 0 && Math.abs(moveX) != mWidth) {
                smoothScrollBy(moveX, 0, calculateTimeForViewPager(velocityX, moveX));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@link android.support.v4.view.ViewPager}的smoothScrollTo(int,int ,int)
     *
     * @param velocity
     * @param dx
     * @return
     */
    private int calculateTimeForViewPager(int velocity, int dx) {
        final int width = mWidth;
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

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState: mColumn:" + mColumn);
        Bundle bundle = (Bundle) state;
        mScrollX = bundle.getInt(SCROLL_X, 0);
        mCurrentPage = bundle.getInt(ARGS_PAGE, 0);
        mWidth = bundle.getInt(ARGS_WIDTH, 0);
        Parcelable parcelable = bundle.getParcelable(ARGS_SUPER);
        super.onRestoreInstanceState(parcelable);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState: mColumn:" + mColumn);
        Bundle bundle = new Bundle();
        bundle.putInt(SCROLL_X, mScrollX);
        bundle.putInt(ARGS_PAGE, mCurrentPage);
        bundle.putInt(ARGS_WIDTH, mWidth);
        bundle.putParcelable(ARGS_SUPER, super.onSaveInstanceState());
        return bundle;
    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        this.listener = listener;
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }
}
