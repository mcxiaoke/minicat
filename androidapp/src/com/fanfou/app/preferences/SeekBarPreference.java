package com.fanfou.app.preferences;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.fanfou.app.R;

public class SeekBarPreference extends DialogPreference {
	private static final String TAG = "SeekBarPreference";

	private Drawable mMyIcon;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.seekbar_preference_dialog);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		mMyIcon = getDialogIcon();
		setDialogIcon(null);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		final ImageView iconView = (ImageView) view
				.findViewById(R.id.seekbar_preference_icon);
		if (mMyIcon != null) {
			iconView.setImageDrawable(mMyIcon);
		} else {
			iconView.setVisibility(View.GONE);
		}
	}

	protected static SeekBar getSeekBar(View dialogView) {
		return (SeekBar) dialogView
				.findViewById(R.id.seekbar_preference_seekbar);
	}
}
