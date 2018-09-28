package com.ckr.pagesnaphelper.view;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ckr.pageview.transform.BaseTransformer;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by PC大佬 on 2018/1/14.
 */

public abstract class BaseFragment extends Fragment {
	private View view;
	private Unbinder unbinder;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(getContentLayoutId(), container, false);
		unbinder = ButterKnife.bind(this, view);
		init();
		return view;
	}

	@CallSuper
	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			onVisible();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	protected abstract int getContentLayoutId();

	protected abstract void init();

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser&&isResumed()) {
			onVisible();
		} else {
			onInvisible();
		}
	}

	protected void onVisible() {
	}

	protected void onInvisible() {
	}

	protected void addData(int index) {}

	protected void jumpToPage(int page) {}

	public abstract void refreshFragment(BaseTransformer baseTransformer);

}
