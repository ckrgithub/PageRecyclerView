package com.ckr.pagesnaphelper.adapter;

import android.support.annotation.IntDef;
import android.support.v7.widget.OrientationHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by PC大佬 on 2018/1/16.
 */

public interface OnPageDataListener {
	int ONE = 1;
	int TWO = 2;
	int THREE = 3;
	int FOUR = 4;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ONE, TWO})
	@interface PageRow {
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ONE, TWO, THREE, FOUR})
	@interface PageColumn {
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({OrientationHelper.HORIZONTAL, OrientationHelper.VERTICAL})
	@interface LayoutOrientation {
	}

	@PageRow
	int getPageColumn();

	@PageColumn
	int getPageRow();

	@LayoutOrientation
	int getLayoutOrientation();

	int getPageCount();

	int getRawItemCount();
}
