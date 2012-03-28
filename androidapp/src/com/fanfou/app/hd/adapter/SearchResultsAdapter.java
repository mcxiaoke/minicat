package com.fanfou.app.hd.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.ui.widget.StatusView;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.25
 * @version 1.1 2011.10.12
 * @version 2.0 2011.10.24
 * @version 3.0 2012.02.22
 * 
 */
public class SearchResultsAdapter extends BaseStatusArrayAdapter {

	private static final String TAG = SearchResultsAdapter.class
			.getSimpleName();

	void log(String message) {
		Log.e(TAG, message);
	}

	private String mKeyword;
	private Pattern mPattern;

	public SearchResultsAdapter(Context context, List<StatusModel> ss) {
		super(context, ss);
	}

	@Override
	protected void setStatusContent(final StatusView view,
			String text) {
		view.setContent(buildHighlightSpan(text));
	}

	private SpannableStringBuilder buildHighlightSpan(String text) {
		SpannableStringBuilder span = new SpannableStringBuilder(text);
		if (!StringHelper.isEmpty(mKeyword)) {
			Matcher m = mPattern.matcher(span);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				span.setSpan(new ForegroundColorSpan(mContext.getResources()
						.getColor(R.color.profile_relation_blue)), start, end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				span.setSpan(new StyleSpan(Typeface.BOLD), start, end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return span;
	}

	public void updateDataAndUI(List<StatusModel> data, String keyword) {
		mKeyword = keyword;
		mPattern = Pattern.compile(mKeyword);
		setData(data);
		notifyDataSetChanged();
	}

}
