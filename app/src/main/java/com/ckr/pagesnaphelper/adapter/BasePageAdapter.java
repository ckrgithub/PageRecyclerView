package com.ckr.pagesnaphelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PC大佬 on 2018/1/15.
 */

public abstract class BasePageAdapter<T, ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> implements OnPageDataListener {
	private static final String TAG = "BasePageAdapter";
	protected Context mContext;
	protected List<T> data;
	protected int mRow;
	protected int mColumn;
	private int mPages;
	private int mOrientation;

	public BasePageAdapter(Context context) {
		mContext = context;
		data = new ArrayList<>();
	}

	public BasePageAdapter setRow(int mRow) {
		this.mRow = mRow;
		return this;
	}

	public BasePageAdapter setColumn(int mColumn) {
		this.mColumn = mColumn;
		return this;
	}

	public BasePageAdapter setOrientation(int mOrientation) {
		this.mOrientation = mOrientation;
		return this;
	}

	public void updateAll(List list) {
		if (list == null || data == null)
			return;
		data.clear();
		data.addAll(list);
		supplyData(data);
		notifyDataSetChanged();
	}

	private void supplyData(List<T> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		Log.i(TAG, "dividePage-->size:" + list.size());
		mPages = (int) Math.ceil(list.size() / (double) (mRow * mColumn));//多少页
		Log.i(TAG, "dividePage-->pages:" + mPages);
		for (int i = list.size(); i < mPages * mRow * mColumn; i++) {
			list.add(null);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return getViewHolder(LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false), viewType);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		convert(holder, position, data.get(position));
	}

	@Override
	public int getItemCount() {
		return data.size();
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
	public int getPageCount() {
		return mPages;
	}

	protected abstract int getLayoutId(int viewType);

	protected abstract ViewHolder getViewHolder(View itemView, int viewType);

	protected abstract void convert(ViewHolder holder, int position, T t);

}
