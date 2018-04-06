package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

import com.ckr.pageview.view.PageRecyclerView;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public abstract class BaseTransformer implements PageRecyclerView.PageTransformer {

	protected abstract void onTransform(View page, float position);

	@Override
	public void transformPage(View page, float position) {
		onPreTransform(page, position);
		onTransform(page, position);
		onPostTransform(page, position);
	}

	protected void onPreTransform(View page, float position) {
		ViewCompat.setAlpha(page, 1);
		ViewCompat.setPivotX(page, 0);
		ViewCompat.setPivotY(page, 0);
		ViewCompat.setRotationY(page, 0);
		ViewCompat.setRotationX(page, 0);
		ViewCompat.setScaleX(page, 1);
		ViewCompat.setScaleY(page, 1);
		ViewCompat.setTranslationX(page, 0);
		ViewCompat.setTranslationY(page, 0);
	}

	protected void onPostTransform(View page, float position) {
	}
}
