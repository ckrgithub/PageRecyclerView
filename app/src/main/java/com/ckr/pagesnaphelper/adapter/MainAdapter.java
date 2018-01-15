package com.ckr.pagesnaphelper.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ckr.pagesnaphelper.R;
import com.ckr.pagesnaphelper.model.Item;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PC大佬 on 2018/1/4.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainHolder> {
	public static final String TAG = "MainAdapter";
	private Context mContext;
	private List<Item> mList;
	private final static int COLUMN = 4, SUM = 8;


	public MainAdapter(Context context, ArrayList<Item> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public MainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MainHolder(LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false));
	}

	@Override
	public void onBindViewHolder(MainHolder holder, int position) {
		int realPosition = getRealPosition24(position, SUM);
		if (position<8) {
			holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color1));
		}else if (position<16) {
			holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color2));
		}else {
			holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color3));

		}
		if (realPosition != -1) {
			Item item = mList.get(realPosition);
			if (item == null) {
				holder.linearLayout.setVisibility(View.INVISIBLE);
			}else {
				holder.linearLayout.setVisibility(View.VISIBLE);
				holder.textView.setText(item.getName());
			}
		}
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
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
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
