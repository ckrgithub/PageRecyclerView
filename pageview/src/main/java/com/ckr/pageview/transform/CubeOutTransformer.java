package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public class CubeOutTransformer extends BaseTransformer {
	private static final String TAG = "CubeOutTransformer";

	@Override
	protected void onTransform(View view, float position) {
		Logd(TAG, "onTransform: position:" + position);
		ViewCompat.setPivotX(view, position < 0f ? view.getWidth() : 0f);
		ViewCompat.setPivotY(view, view.getHeight() * 0.5f);
		ViewCompat.setRotationY(view, 90f * position);
	}
}
