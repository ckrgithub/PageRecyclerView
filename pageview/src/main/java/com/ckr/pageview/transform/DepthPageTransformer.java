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

import com.ckr.pageview.adapter.OnPageDataListener;

public class DepthPageTransformer extends BaseTransformer {

	private static final float MIN_SCALE = 0.75f;

	@Override
	protected void onTransform(View view, float position, boolean forwardDirection, int mOrientation) {
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			if (position <= 0f) {
				ViewCompat.setTranslationX(view, 0f);
				ViewCompat.setScaleX(view, 1f);
				ViewCompat.setScaleY(view, 1f);
			} else if (position <= 1f) {
				final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
				ViewCompat.setAlpha(view, 1 - position);
				ViewCompat.setPivotY(view, 0.5f * view.getHeight());
				ViewCompat.setTranslationX(view, view.getWidth() * -position);
				ViewCompat.setScaleX(view, scaleFactor);
				ViewCompat.setScaleY(view, scaleFactor);
			}
		}else {
			if (position <= 0f) {
				ViewCompat.setTranslationY(view, 0f);
				ViewCompat.setScaleX(view, 1f);
				ViewCompat.setScaleY(view, 1f);
			} else if (position <= 1f) {
				final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
				ViewCompat.setAlpha(view, 1 - position);
				ViewCompat.setPivotX(view, 0.5f * view.getWidth());
				ViewCompat.setTranslationY(view, view.getHeight() * -position);
				ViewCompat.setScaleX(view, scaleFactor);
				ViewCompat.setScaleY(view, scaleFactor);
			}
		}
	}

}
