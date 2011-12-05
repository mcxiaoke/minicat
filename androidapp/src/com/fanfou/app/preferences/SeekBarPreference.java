package com.fanfou.app.preferences;

import com.fanfou.app.App;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.09
 * @version 1.5 2011.11.10
 * 
 */
public class SeekBarPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String TAG = SeekBarPreference.class.getSimpleName();

	private static final String androidns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mMessageText, mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix;
	private int mDefault, mMax, mValue = 0;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		mSuffix = attrs.getAttributeValue(androidns, "text");
		mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		mMax = attrs.getAttributeIntValue(androidns, "max", 100);

	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		if (mDialogMessage != null) {
			mMessageText = new TextView(mContext);
			mMessageText.setText(mDialogMessage);
			layout.addView(mMessageText);
		}

		mValueText = new TextView(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.addView(mValueText, params);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		if (shouldPersist())
			mValue = getPersistedInt(mDefault);

		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			mValue = (Integer) defaultValue;

		setSummary("" + mValue + mSuffix);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (App.DEBUG) {
			Log.d(TAG, "onDialogClosed positive=" + positiveResult + " mvalue="
					+ mValue);
		}
		if (positiveResult) {
			if (shouldPersist())
				persistInt(mValue);
			callChangeListener(new Integer(mValue));
		}
	}

	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		String t = String.valueOf(value);
		mValue = value;
		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		if (App.DEBUG) {
			Log.d(TAG, "onProgressChanged mvalue=" + mValue);
		}
		// if (shouldPersist())
		// persistInt(value);
		// callChangeListener(new Integer(value));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seek) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seek) {
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public int getProgress() {
		return mValue;
	}
}
