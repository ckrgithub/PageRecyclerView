package com.ckr.pagesnaphelper.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.model.Item;


/**
 * Created by PC大佬 on 2018/1/14.
 */

public class MainAdapter extends BasePageAdapter<Item,MainAdapter.MainHolder>{
	public static final String TAG = "MainAdapter";

	public MainAdapter(Context context, @LayoutOrientation int orientation, @PageRow int row, @PageColumn int column) {
		super(context,orientation,row,column);
	}


	public int getRealPosition24(int position, int sum) {
		int pos = -1;
		int page = position / sum;
		Log.i(TAG, "getRealPosition-->下标：" + position + "，每页的总个数：" + sum);
		switch (position % sum) {
			case 0:
			case 7:
				pos = position;
				break;
			case 1:
				pos = 4 + page * sum;
				break;
			case 2:
				pos = 1 + page * sum;
				break;
			case 3:
				pos = 5 + page * sum;
				break;
			case 4:
				pos = 2 + page * sum;
				break;
			case 6:
				pos = 3 + page * sum;
				break;
			case 5:
				pos = 6 + page * sum;
				break;
		}
		return pos;
	}

	@Override
	protected int getLayoutId(int viewType) {
		return R.layout.item_picture;
	}

	@Override
	protected MainHolder getViewHolder(View itemView, int viewType) {
		return new MainHolder(itemView);
	}

	@Override
	protected void convert(MainHolder holder, int position, Item item) {
		int realPosition = getRealPosition24(position, mRow*mColumn);
		if (position < mRow*mColumn) {
			holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color1));
		} else if (position < mRow*mColumn*2) {
			holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color2));
		} else {
			holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color3));
		}
		if (realPosition != -1) {
			if (item == null) {
				holder.linearLayout.setVisibility(View.INVISIBLE);
			} else {
				holder.linearLayout.setVisibility(View.VISIBLE);
				holder.textView.setText(item.getName());
			}
		}
	}

	class MainHolder extends RecyclerView.ViewHolder {
		private TextView textView;
		private LinearLayout linearLayout;

		public MainHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
		}
	}

}
