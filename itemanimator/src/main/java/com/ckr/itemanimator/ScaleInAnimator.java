package com.ckr.itemanimator;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by PC大佬 on 2018/4/5.
 */

public class ScaleInAnimator extends BaseViewAnimator {
	@Override
	public void prepare(View target) {
		getAnimatorAgent().playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", 0.45f, 1),
				ObjectAnimator.ofFloat(target, "scaleY", 0.45f, 1)
//				ObjectAnimator.ofFloat(target, "alpha", 0, 1)
		);
	}
}
