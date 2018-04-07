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

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;

import com.ckr.pageview.adapter.OnPageDataListener;

public class TabletTransformer extends BaseTransformer {

	private static final Matrix OFFSET_MATRIX = new Matrix();
	private static final Camera OFFSET_CAMERA = new Camera();
	private static final float[] OFFSET_TEMP_FLOAT = new float[2];

	@Override
	protected void onTransform(View view, float position, boolean forwardDirection, int mOrientation) {
		final float rotation = (position < 0 ? 30f : -30f) * Math.abs(position);
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			view.setTranslationX(getOffsetForRotation(rotation, view.getWidth(), view.getHeight(), mOrientation));
			view.setPivotX(view.getWidth() * 0.5f);
			view.setPivotY(0);
			view.setRotationY(rotation);
		} else {
			view.setTranslationY(getOffsetForRotation(rotation, view.getWidth(), view.getHeight(), mOrientation));
			view.setPivotY(view.getHeight() * 0.5f);
			view.setPivotX(0);
			view.setRotationX(-rotation);
		}

	}

	protected static final float getOffsetForRotation(float degrees, int width, int height, int mOrientation) {
		OFFSET_MATRIX.reset();
		OFFSET_CAMERA.save();
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			OFFSET_CAMERA.rotateY(Math.abs(degrees));
		} else {
			OFFSET_CAMERA.rotateX(-Math.abs(degrees));
		}
		OFFSET_CAMERA.getMatrix(OFFSET_MATRIX);
		OFFSET_CAMERA.restore();

		OFFSET_MATRIX.preTranslate(-width * 0.5f, -height * 0.5f);
		OFFSET_MATRIX.postTranslate(width * 0.5f, height * 0.5f);
		OFFSET_TEMP_FLOAT[0] = width;
		OFFSET_TEMP_FLOAT[1] = height;
		OFFSET_MATRIX.mapPoints(OFFSET_TEMP_FLOAT);
		if (mOrientation == OnPageDataListener.HORIZONTAL) {
			return (width - OFFSET_TEMP_FLOAT[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
		} else {
			return (height - OFFSET_TEMP_FLOAT[1]) * (degrees > 0.0f ? 1.0f : -1.0f);
		}
	}

}
