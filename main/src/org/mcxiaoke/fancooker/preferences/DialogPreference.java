package org.mcxiaoke.fancooker.preferences;

import android.content.Context;
import android.util.AttributeSet;

public class DialogPreference extends android.preference.DialogPreference {
	Context context;

	public DialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public DialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}
}
