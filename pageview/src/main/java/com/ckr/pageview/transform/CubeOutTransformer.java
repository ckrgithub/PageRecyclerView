package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public class CubeOutTransformer extends BaseTransformer {
	private static final String TAG = "CubeOutTransformer";

	@Override
	protected void onTransform(View view, float position) {
		ViewCompat.setPivotX(view,position < 0f ? view.getWidth() : 0f);
		ViewCompat.setPivotY(view,view.getHeight() * 0.5f);
		view.setRotationY(90f * position);
	}
}
