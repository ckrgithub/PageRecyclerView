package com.ckr.pageview.utils;

import static com.ckr.pageview.utils.PageLog.Logd;

/**
 * Created by PC大佬 on 2018/1/19.
 */

public class PosUtil {
	private static final String TAG="PosUtil";

	/**
	 * 2行2列的下标调整
	 * @param position
	 * @param sum
	 * @return
	 */
	public static int adjustPosition22(int position, int sum) {
		int pos = -1;
		int page = position / sum;
		Logd(TAG, "adjustPosition22,下标：" + position + "，每页的总个数：" + sum);
		switch (position % sum) {
			case 0:
			case 3:
				pos = position;
				break;
			case 1:
				pos = 2 + page * sum;
				break;
			case 2:
				pos = 1 + page * sum;
				break;
		}
		return pos;
	}
	/**
	 * 2行3列的下标调整
	 * @param position
	 * @param sum
	 * @return
	 */
	public static int adjustPosition23(int position, int sum) {
		int pos = -1;
		int page = position / sum;
		Logd(TAG, "adjustPosition23,下标：" + position + "，每页的总个数：" + sum);
		switch (position % sum) {
			case 0:
			case 5:
				pos = position;
				break;
			case 1:
				pos = 3 + page * sum;
				break;
			case 2:
				pos = 1 + page * sum;
				break;
			case 3:
				pos = 4 + page * sum;
				break;
			case 4:
				pos = 2 + page * sum;
				break;
		}
		return pos;
	}

	/**
	 * 2行4列的下标调整
	 * @param position
	 * @param sum
	 * @return
	 */
	public static int adjustPosition24(int position, int sum) {
		int pos = -1;
		int page = position / sum;
		Logd(TAG, "adjustPosition24,下标：" + position + "，每页的总个数：" + sum);
		switch (position % sum) {
			case 0:
			case 7:
				pos = position;
				break;
			case 1:
				pos = 4 + page * sum;
				break;
			case 2:
				pos = 1 + page * sum;
				break;
			case 3:
				pos = 5 + page * sum;
				break;
			case 4:
				pos = 2 + page * sum;
				break;
			case 6:
				pos = 3 + page * sum;
				break;
			case 5:
				pos = 6 + page * sum;
				break;
		}
		return pos;
	}
}
