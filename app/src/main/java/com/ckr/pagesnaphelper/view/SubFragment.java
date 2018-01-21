package com.ckr.pagesnaphelper.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.view.View;

import com.ckr.pagesnaphelper.R;

import butterknife.BindView;

/**
 * Created by PC大佬 on 2018/1/20.
 */

public class SubFragment extends BaseFragment {
	private static final String ID_COLOR = "colorId";
	@BindView(R.id.frameLayout)
	View frameLayout;
	private int colorId;

	public static SubFragment newInstance(@ColorRes int colorId) {
		Bundle args = new Bundle();
		args.putInt(ID_COLOR, colorId);
		SubFragment fragment = new SubFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Bundle arguments = getArguments();
		if (arguments != null) {
			colorId = arguments.getInt(ID_COLOR, R.color.color1);
		}
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.fragment_sub;
	}

	@Override
	protected void init() {
		frameLayout.setBackgroundResource(colorId);
	}
}
