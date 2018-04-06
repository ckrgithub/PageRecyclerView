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

public class ForegroundTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float height = view.getHeight();
		final float width = view.getWidth();
		final float scale = Math.max(position > 0 ? 1f : Math.abs(1f + position), 0.5f);
		ViewCompat.setScaleX(view,scale);
		ViewCompat.setScaleY(view,scale);
		ViewCompat.setPivotX(view,width * 0.5f);
		ViewCompat.setPivotY(view,height * 0.5f);
		ViewCompat.setTranslationX(view,position > 0 ? width * position : -width * position * 0.25f);
	}
}
