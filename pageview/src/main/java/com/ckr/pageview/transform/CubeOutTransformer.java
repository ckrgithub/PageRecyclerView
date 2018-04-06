package com.ckr.pageview.transform;

import android.view.View;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public class CubeOutTransformer extends BaseTransformer {
	private static final String TAG = "CubeOutTransformer";

	@Override
	protected void onTransform(View page, float position) {
		Logd(TAG, "onTransform: position:" + position);
		page.setPivotX(position < 0f ? page.getWidth() : 0f);
		page.setPivotY(page.getHeight() * 0.5f);
		page.setRotationY(90f * position);
	}
}
