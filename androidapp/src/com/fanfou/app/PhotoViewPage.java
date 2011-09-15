package com.fanfou.app;

import com.fanfou.app.ui.widget.TwoDScrollView;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 20110828
 * 
 */
public class PhotoViewPage extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView view = new ImageView(this);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		TwoDScrollView root = new TwoDScrollView(this);
		root.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		setContentView(root);
		
		view.setImageResource(R.drawable.icon);
	}

}
