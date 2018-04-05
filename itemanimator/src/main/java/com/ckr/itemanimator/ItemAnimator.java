package com.ckr.itemanimator;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by PC大佬 on 2018/4/5.
 */

public enum ItemAnimator  {
	ScaleIn(ScaleInAnimator.class);

	private Class animatorClazz;

	private ItemAnimator(Class clazz) {
		animatorClazz = clazz;
	}

	public BaseViewAnimator getAnimator() {
		try {
			return (BaseViewAnimator) animatorClazz.newInstance();
		} catch (Exception e) {
			throw new Error("Can not init animatorClazz instance");
		}
	}
}
