package com.fanfou.app.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;

public class IconTabHost extends TabHost {

	@Override
	public void dispatchWindowFocusChanged(boolean hasFocus) {
		if (getCurrentView() == null) {
			return;
		}
		super.dispatchWindowFocusChanged(hasFocus);
	}

	@Override
	public void setCurrentTab(int index) {
		int i = getCurrentTab();
		if (index == i) {
			View view = getCurrentView();
			if (view == null) {
				return;
			}
			ListView list = (ListView) view.findViewById(16908298);
			if (list != null) {
				if (Build.VERSION.SDK_INT >= 8) {
					int position = list.getFirstVisiblePosition();
					if (position > 15) {
						list.setSelection(0);
						list.smoothScrollToPosition(0);
						return;
					}
				}
				list.setSelection(0);
				return;
			}
		}
		super.setCurrentTab(index);
	}

	public IconTabHost(Context context) {
		super(context);
	}

	public IconTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
