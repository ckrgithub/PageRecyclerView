package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

import com.ckr.pageview.view.PageRecyclerView;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public abstract class BaseTransformer implements PageRecyclerView.PageTransformer {
	private static final String TAG = "BaseTransformer";
	protected abstract void onTransform(View view, float position);

	@Override
	public void transformPage(View page, float position) {
		Logd(TAG, "transformPage: position:" + position);
		onPreTransform(page, position);
		onTransform(page, position);
		onPostTransform(page, position);
	}

	protected void onPreTransform(View view, float position) {
//		final float width = view.getWidth();
		ViewCompat.setAlpha(view, 1);
		ViewCompat.setPivotX(view, 0);
		ViewCompat.setPivotY(view, 0);
		ViewCompat.setRotationY(view, 0);
		ViewCompat.setRotationX(view, 0);
		ViewCompat.setScaleX(view, 1);
		ViewCompat.setScaleY(view, 1);
		ViewCompat.setTranslationX(view, 0);
		ViewCompat.setTranslationY(view, 0);

	}

	protected void onPostTransform(View view, float position) {
	}

	/**
	 * cubeInTransformer 需要
	 * @return
	 */
	protected boolean isPagingEnabled() {
		return false;
	}

}
