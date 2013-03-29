package org.mcxiaoke.fancooker.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class EmailDialogPreference extends DialogPreference {
	Context c;
	String version;

	public EmailDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
	}

	public EmailDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		c = context;
	}

	public void setVersion(String v) {
		version = v;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		//
		// if (which == DialogInterface.BUTTON_POSITIVE) {
		// SmsPopupUtils.launchEmailToIntent(c, c.getString(R.string.app_name) +
		// version, true);
		// } else if (which == DialogInterface.BUTTON_NEGATIVE) {
		// SmsPopupUtils.launchEmailToIntent(c, c.getString(R.string.app_name) +
		// version, false);
		// }
	}
}
