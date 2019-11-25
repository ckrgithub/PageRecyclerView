package com.ckr.pageview.manager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ckr.pageview.view.PageView;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created on 2019/11/24
 *
 * @author ckr
 */
public class LifecycleManager {
	private static final String TAG = "LifecycleManager";
	private FragmentActivity mHostActivity;
	private Fragment mHostFragment;
	private PageView mPageView;

	public LifecycleManager(PageView view) {
		this.mPageView = view;
	}

	public void onDetachedFromWindow() {
		Logd(TAG, "onDetachedFromWindow");
		removeLifeCycleObserver(mHostActivity);
		removeLifeCycleObserver(mHostFragment);
		mHostActivity = null;
		mHostFragment = null;
		if (mPageView != null) {
			mPageView.release();
		}
	}

	public void onAttachedToWindow() {
		Logd(TAG, "onAttachedToWindow");
	}

	public void onResume() {
		Logd(TAG, "onResume");
		if (mPageView != null) {
			mPageView.restartLooping();
		}
	}

	public void onStop() {
		Logd(TAG, "onStop");
		if (mPageView != null) {
			mPageView.stopLooping();
		}
	}

	public void onDestroy() {
		Logd(TAG, "onDestroy");
	}

	public void registerLifeCycleObserver(@NonNull FragmentActivity activity) {
		if (activity == null) {
			return;
		}
		mHostActivity = activity;
//		activity.getLifecycle().addObserver(this);
	}

	public void removeLifeCycleObserver(@NonNull FragmentActivity activity) {
		if (activity == null) {
			return;
		}
//		activity.getLifecycle().removeObserver(this);
	}

	public void registerLifeCycleObserver(@NonNull Fragment fragment) {
		if (fragment == null) {
			return;
		}
		mHostFragment = fragment;
//		mHostFragment.getLifecycle().addObserver(this);
	}

	public void removeLifeCycleObserver(@NonNull Fragment fragment) {
		if (fragment == null) {
			return;
		}
//		fragment.getLifecycle().removeObserver(this);
	}
}
