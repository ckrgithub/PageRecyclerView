package com.ckr.pageview.adapter;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by PC大佬 on 2018/1/16.
 */

public interface OnPageDataListener<T> {
	int ONE = 1;
	int TWO = 2;
	int THREE = 3;
	int FOUR = 4;
	int FIVE = 5;
	int HORIZONTAL = 0;
	int VERTICAL = 1;
	int LINEAR = 0;
	int GRID = 1;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ONE, TWO, THREE, FOUR,FIVE})
	@interface PageRow {
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ONE, TWO})
	@interface PageColumn {
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({HORIZONTAL, VERTICAL})
	@interface LayoutOrientation {
	}
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({LINEAR, GRID})
	@interface LayoutFlag {
	}

	@PageRow
	int getPageColumn();

	@PageColumn
	int getPageRow();

	@LayoutOrientation
	int getLayoutOrientation();

	@LayoutFlag
	int getLayoutFlag();

	boolean isLooping();

	int getPageCount();

	int getRawItemCount();

	List<T> getRawData();
}
