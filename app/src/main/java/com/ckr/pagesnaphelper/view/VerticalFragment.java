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

public class VerticalFragment extends BaseFragment {
	private static final String ID_COLOR = "colorId";
	@BindView(R.id.frameLayout)
	View frameLayout;
	private int colorId;

	public static VerticalFragment newInstance(@ColorRes int colorId) {
		Bundle args = new Bundle();
		args.putInt(ID_COLOR, colorId);
		VerticalFragment fragment = new VerticalFragment();
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
		return R.layout.fragment_vertical;
	}

	@Override
	protected void init() {
		frameLayout.setBackgroundResource(colorId);
	}
}
