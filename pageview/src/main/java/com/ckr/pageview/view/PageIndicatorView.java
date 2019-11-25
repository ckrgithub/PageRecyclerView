package com.ckr.pageview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ckr.pageview.R;
import com.ckr.pageview.adapter.OnPageDataListener;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created on 2019/11/24
 *
 * @author ckr
 */
public class PageIndicatorView extends View {
	private static final String TAG = "PageIndicatorView";
	
	private int selectedIndicatorColor = Color.WHITE;
	private int unselectedIndicatorColor = Color.GRAY;
	private int selectedIndicatorDiameter = 15;
	private int unselectedIndicatorDiameter = 15;
	//指示器间的间隔
	private int indicatorMargin = 15;
	private Drawable selectedIndicatorDrawable = null;
	private Drawable unselectedIndicatorDrawable = null;
	//一组指示器的父View
	private LinearLayout indicatorGroup;
	private int indicatorGroupAlignment = 0x11;
	private int indicatorGroupMarginLeft;
	private int indicatorGroupMarginTop;
	private int indicatorGroupMarginRight;
	private int indicatorGroupMarginBottom;
	//可移动的指示器
	private View moveIndicator;

	public PageIndicatorView(Context context) {
		this(context, null);
	}

	public PageIndicatorView(Context context,  AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public PageIndicatorView(Context context,  AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageView, defStyleAttr, 0);
		selectedIndicatorColor = typedArray.getColor(R.styleable.PageView_selected_indicator_color, selectedIndicatorColor);
		unselectedIndicatorColor = typedArray.getColor(R.styleable.PageView_unselected_indicator_color, unselectedIndicatorColor);
		selectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_selected_indicator_diameter, selectedIndicatorDiameter);
		unselectedIndicatorDiameter = typedArray.getDimensionPixelSize(R.styleable.PageView_unselected_indicator_diameter, unselectedIndicatorDiameter);
		indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_margin, indicatorMargin);
		if (typedArray.hasValue(R.styleable.PageView_selected_indicator_drawable)) {
			selectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_selected_indicator_drawable);
		}
		if (typedArray.hasValue(R.styleable.PageView_unselected_indicator_drawable)) {
			unselectedIndicatorDrawable = typedArray.getDrawable(R.styleable.PageView_unselected_indicator_drawable);
		}
		indicatorGroupAlignment = typedArray.getInteger(R.styleable.PageView_indicator_group_alignment, indicatorGroupAlignment);
		indicatorGroupMarginLeft = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginLeft, indicatorGroupMarginLeft);
		indicatorGroupMarginTop = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginTop, indicatorGroupMarginTop);
		indicatorGroupMarginRight = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginRight, indicatorGroupMarginRight);
		indicatorGroupMarginBottom = typedArray.getDimensionPixelSize(R.styleable.PageView_indicator_group_marginBottom, indicatorGroupMarginBottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		View rootView = getRootView();
		if (rootView != null) {
			Object tag = rootView.getTag();
			if (tag instanceof Integer) {
				if (tag==OnPageDataListener.HORIZONTAL) {
					int widthMode = MeasureSpec.getMode(widthMeasureSpec);
					int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//					widthSize>unselectedIndicatorDiameter
					int heightMode = MeasureSpec.getMode(heightMeasureSpec);
					int heightSize = MeasureSpec.getSize(heightMeasureSpec);
					Logd(TAG, "onMeasure  widthMode: " + widthMode + ",widthSize:" + widthSize);
				}else {

				}
			}
		}
	}
}
