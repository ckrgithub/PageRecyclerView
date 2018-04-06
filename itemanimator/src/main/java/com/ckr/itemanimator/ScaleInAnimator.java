package com.ckr.itemanimator;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by PC大佬 on 2018/4/5.
 */

public class ScaleInAnimator extends BaseViewAnimator {

	private static final float scaleRatio=1.0f;

	@Override
	public void prepare(View target) {
		getAnimatorAgent().playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", scaleRatio,scaleRatio),
				ObjectAnimator.ofFloat(target, "scaleY", scaleRatio, scaleRatio)
//				ObjectAnimator.ofFloat(target, "alpha", 0, 1)
		);
	}
}
