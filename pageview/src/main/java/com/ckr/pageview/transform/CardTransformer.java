/*
 * Copyright 2014 Toxic Bakery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ckr.pageview.transform;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class CardTransformer extends BaseTransformer {
	private static final float MIN_SCALE = 0.75f;
	private static final float MIN_SCALE_1 = 0.70f;
	private static final float MIN_SCALE_2 = 0.65f;

	@Override
	protected void onTransform(View view, float position, boolean forwardDirection) {
		int width = view.getWidth();
		int height = view.getHeight();
		ViewCompat.setPivotY(view, 0.5f * height);
		ViewCompat.setPivotX(view, 0.5f * width);
		if (forwardDirection) {
			/*if (position < -1) {
				ViewCompat.setTranslationY(view, 60 * -position);
				ViewCompat.setScaleX(view, width < height ? MIN_SCALE_2 : MIN_SCALE_2 * height / width);
				ViewCompat.setScaleY(view, width > height ? MIN_SCALE_2 : MIN_SCALE_2 * width / height);
			} else*/ if (position <= 0) {
				ViewCompat.setScaleX(view, width < height ? MIN_SCALE : MIN_SCALE * height / width);
				ViewCompat.setScaleY(view, width > height ? MIN_SCALE : MIN_SCALE * width / height);
			} else if (position < 2) {
				ViewCompat.setTranslationY(view, 60 * position);
				ViewCompat.setScaleX(view, width < height ? MIN_SCALE_1 : MIN_SCALE_1 * height / width);
				ViewCompat.setScaleY(view, width > height ? MIN_SCALE_1 : MIN_SCALE_1 * width / height);
			}
		} else {
			if (position < 0) {
				ViewCompat.setTranslationY(view, 60 * position);
				ViewCompat.setScaleX(view, width < height ? MIN_SCALE_1 : MIN_SCALE_1 * height / width);
				ViewCompat.setScaleY(view, width > height ? MIN_SCALE_1 : MIN_SCALE_1 * width / height);
			} else if (position < 1) {
				ViewCompat.setScaleX(view, width < height ? MIN_SCALE : MIN_SCALE * height / width);
				ViewCompat.setScaleY(view, width > height ? MIN_SCALE : MIN_SCALE * width / height);
			} else if (position < 2) {
				ViewCompat.setTranslationY(view, 30 * position);
				ViewCompat.setScaleX(view, width < height ? MIN_SCALE_1 : MIN_SCALE_1 * height / width);
				ViewCompat.setScaleY(view, width > height ? MIN_SCALE_1 : MIN_SCALE_1 * width / height);
			}
		}

		view.setTranslationX(position < 0 ? 0f : -view.getWidth() * position);
	}

	@Override
	protected void onPreTransform(View view, float position) {
		super.onPreTransform(view, position);
	}
}
