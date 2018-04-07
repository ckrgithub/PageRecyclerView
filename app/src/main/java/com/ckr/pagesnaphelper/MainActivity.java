package com.ckr.pagesnaphelper;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ckr.pagesnaphelper.view.MainFragment;
import com.ckr.pageview.transform.Transformer;
import com.ckr.pageview.utils.PageLog;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
	private Unbinder unbinder;
	private static final int[] MENU_ITEM_ID = {R.id.item, R.id.accordion, R.id.cubeOut
			, R.id.depthPage, R.id.flipHorizontal, R.id.scaleInOut, R.id.stack, R.id.tablet
			, R.id.zoomIn, R.id.zoomOutSlide};
	private Menu menu;
	private MainFragment mainFragment;
	private int lastIndex;
	public Map<Integer, Integer> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PageLog.debug();
		unbinder = ButterKnife.bind(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (savedInstanceState == null) {
			mainFragment = MainFragment.newInstance();
			fragmentManager.beginTransaction()
					.add(R.id.container, mainFragment, MainFragment.class.getName())
					.commit();
		} else {
			fragmentManager.beginTransaction().show(mainFragment = (MainFragment) fragmentManager.findFragmentByTag(MainFragment.class.getName()))
					.commit();
		}
		map = new HashMap<>(MENU_ITEM_ID.length);
		for (int i = 0; i < MENU_ITEM_ID.length; i++) {
			map.put(MENU_ITEM_ID[i], i);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbinder.unbind();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_main, this.menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.isChecked()) {
			return true;
		}
		switch (item.getItemId()) {
			case R.id.item1:
			case R.id.item2:
			case R.id.item3:
			case R.id.item4:
			case R.id.item5:
			case R.id.item6:
			case R.id.item7:
			case R.id.item8:
			case R.id.item9:
			case R.id.item10:
			case R.id.item11:
				disableChecked(lastIndex);
				boolean checked = !item.isChecked();
				item.setChecked(checked);
				int index = map.get(item.getItemId());
				lastIndex = index;
				mainFragment.refreshFragment(Transformer.values()[index].getTransformer());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void disableChecked(int pos) {
		MenuItem menuItem = menu.findItem(MENU_ITEM_ID[pos]);
		if (menuItem.isChecked()) {
			menuItem.setChecked(false);
		}
	}
}
