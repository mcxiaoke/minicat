package com.fanfou.app.util;

import com.fanfou.app.R;

import android.view.View;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.06
 * 
 */
public final class ThemeHelper {

	private static final void setMentionBgColor(View view) {
//		boolean useCustom = OptionHelper.readBoolean(
//				R.string.option_color_use_custom, false);
//		if (!useCustom) {
//			return;
//		}
		int color = OptionHelper.readInt(
				R.string.option_color_highlight_mention, view.getContext()
						.getResources().getColor(R.color.mentioned_color));
		view.setBackgroundColor(color);
	}

	private static final void setSelfBgColor(View view) {
//		boolean useCustom = OptionHelper.readBoolean(
//				R.string.option_color_use_custom, false);
//		if (!useCustom) {
//			return;
//		}
		int color = OptionHelper.readInt(R.string.option_color_highlight_self,
				view.getContext().getResources().getColor(R.color.self_color));
		view.setBackgroundColor(color);
	}

	private static final void setNameTextColor(TextView view) {
		boolean useCustom = OptionHelper.readBoolean(
				R.string.option_color_use_custom, false);
		if (!useCustom) {
			return;
		}
		int color = OptionHelper.readInt(R.string.option_color_name, view
				.getContext().getResources().getColor(R.color.name_color));
		view.setTextColor(color);
	}

	private static final void setContentTextColor(TextView view) {
		boolean useCustom = OptionHelper.readBoolean(
				R.string.option_color_use_custom, false);
		if (!useCustom) {
			return;
		}
		int color = OptionHelper.readInt(R.string.option_color_text, view
				.getContext().getResources().getColor(R.color.content_color));
		view.setTextColor(color);
	}

	private static final void setMetaInfoTextColor(TextView view) {
		boolean useCustom = OptionHelper.readBoolean(
				R.string.option_color_use_custom, false);
		if (!useCustom) {
			return;
		}
		int color = OptionHelper.readInt(R.string.option_color_metainfo, view
				.getContext().getResources().getColor(R.color.metainfo_color));
		view.setTextColor(color);
	}

	public static final void setBackgroundColor(View view) {
//		boolean useCustom = OptionHelper.readBoolean(
//				R.string.option_color_use_custom, false);
//		if (!useCustom) {
//			return;
//		}
//		int color = OptionHelper
//				.readInt(R.string.option_color_background, view.getContext()
//						.getResources().getColor(R.color.background_color));
//		view.setBackgroundColor(color);
	}

}
