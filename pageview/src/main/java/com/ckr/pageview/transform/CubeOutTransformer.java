package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public class CubeOutTransformer extends BaseTransformer {


	@Override
	protected void onTransform(View view, float position, boolean forwardDirection) {
		ViewCompat.setPivotX(view, position < 0f ? view.getWidth() : 0f);
		ViewCompat.setPivotY(view, view.getHeight() * 0.5f);
		ViewCompat.setRotationY(view, 90f * position);
	}

	@Override
	public boolean isPagingEnabled() {
		return true;
	}
}
