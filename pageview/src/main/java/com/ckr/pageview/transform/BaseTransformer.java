package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

import com.ckr.pageview.adapter.OnPageDataListener;
import com.ckr.pageview.view.PageRecyclerView;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public abstract class BaseTransformer implements PageRecyclerView.PageTransformer {
	protected static final String TAG = "BaseTransformer";

	protected abstract void onTransform(View view, float position, boolean forwardDirection, @OnPageDataListener.LayoutOrientation int mOrientation);

	@Override
	public void transformPage(View page, float position, boolean forwardDirection, int mOrientation) {
		Logd(TAG, "transformPage: position:" + position);
		onPreTransform(page, position, mOrientation);
		onTransform(page, position, forwardDirection, mOrientation);
		onPostTransform(page, position, mOrientation);
	}

	protected void onPreTransform(View view, float position, @OnPageDataListener.LayoutOrientation int mOrientation) {
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

	protected void onPostTransform(View view, float position, @OnPageDataListener.LayoutOrientation int mOrientation) {
	}

}
