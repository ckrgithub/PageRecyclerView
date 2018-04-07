package com.ckr.pageview.transform;

import android.view.View;

import com.ckr.pageview.adapter.OnPageDataListener;

public class ScaleInOutTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position, boolean forwardDirection, int mOrientation) {
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			view.setPivotX(position < 0 ? 0 : view.getWidth());
			view.setPivotY(view.getHeight() / 2f);
			float scale = position < 0 ? 1f + position : 1f - position;
			view.setScaleX(scale);
			view.setScaleY(scale);
		} else {
			view.setPivotY(position < 0 ? 0 : view.getHeight());
			view.setPivotX(view.getWidth() / 2f);
			float scale = position < 0 ? 1f + position : 1f - position;
			view.setScaleX(scale);
			view.setScaleY(scale);
		}
	}

	@Override
	protected void onPreTransform(View view, float position, int mOrientation) {
		super.onPreTransform(view, position, mOrientation);
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			view.setTranslationX(-view.getWidth() * position);
		} else {
			view.setTranslationY(-view.getHeight() * position);
		}
	}
}
