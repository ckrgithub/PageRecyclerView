package com.ckr.pageview.utils;

import android.util.Log;

/**
 * Created by PC大佬 on 2018/1/26.
 */

public class PageLog {
	private static final String TAG = "PageLog";
	private static boolean isDebug = false;

	public static void debug() {
		PageLog.isDebug = true;
	}

	public static void Logv(String msg) {
		Logv("", msg);
	}

	public static void Logv(String tag, String msg) {
		if (isDebug) {
			Log.v(TAG, tag + "--->" + msg);
		}
	}

	public static void Logd(String msg) {
		Logd("", msg);
	}

	public static void Logd(String tag, String msg) {
		if (isDebug) {
			Log.d(TAG, tag + "--->" + msg);
		}
	}

	public static void Logi(String msg) {
		Logi("", msg);
	}

	public static void Logi(String tag, String msg) {
		if (isDebug) {
			Log.i(TAG, tag + "--->" + msg);
		}
	}

	public static void Logw(String msg) {
		Logw("", msg);
	}

	public static void Logw(String tag, String msg) {
		if (isDebug) {
			Log.w(TAG, tag + "--->" + msg);
		}
	}

	public static void Loge(String msg) {
		Loge("", msg);
	}

	public static void Loge(String tag, String msg) {
		if (isDebug) {
			Log.e(TAG, tag + "--->" + msg);
		}
	}
}
