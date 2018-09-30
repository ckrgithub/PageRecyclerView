package com.ckr.pageview.adapter;

/**
 * Created by ckr on 2018/9/12.
 */

public interface OnAutoSizeListener {
    void notifySizeChanged(int size);
    int calculateItemWidthOrHeight();
    boolean isAutoSize();
}
