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

public class FlipHorizontalTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position, boolean forwardDirection, int mOrientation) {
		final float rotation = 180f * (position);
		ViewCompat.setAlpha(view,rotation > 90f || rotation < -90f ? 0 : 1);
		ViewCompat.setPivotX(view,view.getWidth() * 0.5f);
		ViewCompat.setPivotY(view,view.getHeight() * 0.5f);
		ViewCompat.setRotationY(view,rotation);
	}

	@Override
	protected void onPreTransform(View view, float position, int mOrientation) {
		super.onPreTransform(view, position, mOrientation);
		view.setTranslationX(-view.getWidth()*(position));
	}

	@Override
	protected void onPostTransform(View page, float position, int mOrientation) {
		super.onPostTransform(page, position, mOrientation);

		//resolve problem: new page can't handle click event!
		if (position > -0.5f && position < 0.5f) {
			page.setVisibility(View.VISIBLE);
		} else {
			page.setVisibility(View.INVISIBLE);
		}
	}
}
