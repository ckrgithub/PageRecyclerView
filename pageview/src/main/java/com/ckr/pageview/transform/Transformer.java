package com.ckr.pageview.transform;

/**
 * Created by PC大佬 on 2018/4/6.
 */

public enum Transformer {
	Default(DefaultTransformer.class),
	Accordion(AccordionTransformer.class),
	CubeOut(CubeOutTransformer.class),
	DepthPage(DepthPageTransformer.class),
	FlipHorizontal(FlipHorizontalTransformer.class),
	ScaleInOut(ScaleInOutTransformer.class),
	Stack(StackTransformer.class),
	Tablet(TabletTransformer.class),
	ZoomIn(ZoomInTransformer.class),
	ZoomOutSlide(ZoomOutSlideTransformer.class);

	private Class mClazz;

	private Transformer(Class clazz) {
		mClazz = clazz;
	}

	public BaseTransformer getTransformer() {
		try {
			if (mClazz == null) {
				return null;
			} else {
				return (BaseTransformer) mClazz.newInstance();
			}
		} catch (Exception e) {
			throw new Error("Can not init mClazz instance");
		}
	}
}
