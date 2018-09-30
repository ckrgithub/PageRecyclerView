package com.ckr.pageview.adapter;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.ckr.pageview.utils.PageLog.Logd;
import static com.ckr.pageview.utils.PageLog.Loge;
import static com.ckr.pageview.utils.PosUtil.adjustPosition22;
import static com.ckr.pageview.utils.PosUtil.adjustPosition23;
import static com.ckr.pageview.utils.PosUtil.adjustPosition24;
import static com.ckr.pageview.utils.PosUtil.adjustPosition25;


/**
 * Created by PC大佬 on 2018/1/15.
 */

public abstract class BasePageAdapter<T, ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> implements OnPageDataListener<T> , OnAutoSizeListener{
	private static final String TAG = "BasePageAdapter";
	protected Context mContext;
	protected List<T> mTargetData;
	protected List<T> mRawData;
	protected int mRow;
	protected int mColumn;
	private int mPageCount;
	protected int mOrientation;
	protected int mLayoutFlag;
	protected boolean mIsLooping;
	private OnIndicatorListener mOnIndicatorListener;
	private int widthOrHeight;

	public BasePageAdapter(Context context) {
		mContext = context;
		mRawData = new ArrayList<>();
		mTargetData = new ArrayList<>();
	}

	public BasePageAdapter setRow(@IntRange(from = 1) int mRow) {
		this.mRow = mRow;
		return this;
	}

	public BasePageAdapter setColumn(@IntRange(from = 1) int mColumn) {
		this.mColumn = mColumn;
		return this;
	}

	public BasePageAdapter setOrientation(@LayoutOrientation int mOrientation) {
		this.mOrientation = mOrientation;
		return this;
	}

	public BasePageAdapter setLayoutFlag(@LayoutFlag int mLayoutFlag) {
		this.mLayoutFlag = mLayoutFlag;
		return this;
	}

	public BasePageAdapter setLooping(boolean isLooping) {
		this.mIsLooping = isLooping;
		return this;
	}

	public BasePageAdapter setOnIndicatorListener(OnIndicatorListener listener) {
		mOnIndicatorListener = listener;
		return this;
	}

	public void updateAll(List<T> list) {
		if (list == null || mRawData == null)
			return;
		mRawData.clear();
		mRawData.addAll(list);
		supplyData(mRawData);
		notifyDataSetChanged();
		if (mOnIndicatorListener != null) {
			mOnIndicatorListener.updateIndicator();
		}
	}

	public void updateItem(T t) {
		if (t == null) {
			return;
		}
		int index = mRawData.size();
		mRawData.add(t);
		int pageCount = (int) Math.ceil(mRawData.size() / (double) (mRow * mColumn));
		if (pageCount == this.mPageCount) {
			mTargetData.add(index, t);
			mTargetData.remove(mTargetData.size() - 1);
			notifyDataSetChanged();
		} else {
			supplyData(mRawData);
			notifyDataSetChanged();
			if (mOnIndicatorListener != null) {
				mOnIndicatorListener.updateIndicator();
			}
		}
	}

	public void updateItem(int start, T t) {
		if (t == null) {
			return;
		}
		if (start < 0 && start > mRawData.size()) {
			throw new ArrayIndexOutOfBoundsException(start);
		}
		mRawData.add(start, t);
		int pageCount = (int) Math.ceil(mRawData.size() / (double) (mRow * mColumn));
		if (pageCount == this.mPageCount) {
			mTargetData.add(start, t);
			mTargetData.remove(mTargetData.size() - 1);
			notifyDataSetChanged();
		} else {
			supplyData(mRawData);
			notifyDataSetChanged();
			if (mOnIndicatorListener != null) {
				mOnIndicatorListener.updateIndicator();
			}
		}
	}

	public void removeItem(int adjustedPosition) {
		if (adjustedPosition < 0 && adjustedPosition >= mRawData.size()) {
			throw new ArrayIndexOutOfBoundsException(adjustedPosition);
		}
		mRawData.remove(adjustedPosition);
		int pageCount = (int) Math.ceil(mRawData.size() / (double) (mRow * mColumn));
		if (pageCount == this.mPageCount) {
			mTargetData.remove(adjustedPosition);
			mTargetData.add(null);
			notifyDataSetChanged();
		} else {
			supplyData(mRawData);
			notifyDataSetChanged();
			if (mOnIndicatorListener != null) {
				mOnIndicatorListener.updateIndicator();
			}
		}
	}

	private void supplyData(List<T> list) {
		if (list == null) {
			return;
		}
		Logd(TAG, "supplyData,size:" + list.size());
		mTargetData.clear();
		mTargetData.addAll(list);
		mPageCount = (int) Math.ceil(list.size() / (double) (mRow * mColumn));//多少页
		Logd(TAG, "supplyData,pages:" + mPageCount);
		for (int i = list.size(); i < mPageCount * mRow * mColumn; i++) {
			mTargetData.add(null);
		}
	}

	@Override
	public List<T> getRawData() {
		return mRawData;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false);
		if (isAutoSize()) {
			ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
			int itemWidthOrHeight = calculateItemWidthOrHeight();
			Logd(TAG, "onMeasure  onCreateViewHolder: widthOrHeight:" + widthOrHeight + ",itemWidthOrHeight:" + itemWidthOrHeight+",mOrientation:"+mOrientation);
			if (itemWidthOrHeight > 0) {
				if (mOrientation == HORIZONTAL) {
					layoutParams.width = itemWidthOrHeight;
				} else {
					layoutParams.height = itemWidthOrHeight;
//                    layoutParams.width = 600;
				}
				Loge(TAG, "onMeasure  onCreateViewHolder: width:" + layoutParams.width + ",height:" + layoutParams.height);
				itemView.setLayoutParams(layoutParams);
			}
		}
		return getViewHolder(itemView, viewType);	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Logd(TAG, "onBindViewHolder: position:" + position + ",size:" + mTargetData.size());
		int index = position;
		if (mIsLooping) {
			index = position % mTargetData.size();
		}
		int adjustedPosition = adjustPosition(index);
		convert(holder, index, mTargetData.get(index), adjustedPosition, mTargetData.get(adjustedPosition));
	}

	@Override
	public int getItemCount() {
		return mTargetData.size();
	}

	protected int adjustPosition(int index) {
		int pos = index;
		if (mLayoutFlag == OnPageDataListener.GRID && mOrientation == OnPageDataListener.LINEAR) {
			pos = getAdjustedPosition(index, mRow * mColumn);
		}
		return pos;
	}

	protected int getAdjustedPosition(int position, int sum) {
		if (mRow == OnPageDataListener.TWO) {
			int index = position;
			switch (mColumn) {
				case OnPageDataListener.TWO:
					index = adjustPosition22(position, sum);
					break;
				case OnPageDataListener.THREE:
					index = adjustPosition23(position, sum);
					break;
				case OnPageDataListener.FOUR:
					index = adjustPosition24(position, sum);
					break;
				case OnPageDataListener.FIVE:
					index = adjustPosition25(position, sum);
					break;
				default:
					break;
			}
			return index;
		} else {
			return position;
		}
	}

	@Override
	public int getPageColumn() {
		return mColumn;
	}

	@Override
	public int getPageRow() {
		return mRow;
	}

	@Override
	public int getLayoutOrientation() {
		return mOrientation;
	}

	@Override
	public int getLayoutFlag() {
		return mLayoutFlag;
	}

	@Override
	public boolean isLooping() {
		return mIsLooping;
	}

	@Override
	public int getPageCount() {
		return mPageCount;
	}

	@Override
	public int getRawItemCount() {
		return mRawData.size();
	}


	@Override
	public void notifySizeChanged(@IntRange(from = 0) int size) {
		this.widthOrHeight = size;
	}

	@Override
	public int calculateItemWidthOrHeight() {
		if (isGridLayout()) {
			if (mOrientation == HORIZONTAL) {
				return widthOrHeight / mColumn;
			} else {
				return widthOrHeight / mRow;
			}
		}
		return 0;
	}

	@Override
	public boolean isAutoSize() {
		return true&&isGridLayout();
	}

	private boolean isGridLayout() {
		return mLayoutFlag == GRID;
	}

	protected abstract int getLayoutId(int viewType);

	protected abstract ViewHolder getViewHolder(View itemView, int viewType);

	protected abstract void convert(ViewHolder holder, int originalPos, T originalItem, int adjustedPos, T adjustedItem);

}
