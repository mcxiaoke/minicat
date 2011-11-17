package com.fanfou.app.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fanfou.app.App;
import com.fanfou.app.api.Status;

import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.26
 * @version 1.6 2011.11.17
 * 
 */
public class StatusHelper {
	private static final String TAG = "StatusHelper";

	private static HashMap<String, String> userNameIdMap = new HashMap<String, String>();

	private static final Pattern PATTERN_USER = Pattern.compile("@.+?\\s");
	private static final Linkify.MatchFilter MATCH_FILTER_USER = new Linkify.MatchFilter() {
		@Override
		public final boolean acceptMatch(final CharSequence s, final int start,
				final int end) {
			String name = s.subSequence(start + 1, end).toString().trim();
			return userNameIdMap.containsKey(name);
		}
	};

	private static final Linkify.TransformFilter TRANSFORM_USER = new Linkify.TransformFilter() {

		@Override
		public String transformUrl(Matcher match, String url) {
			String name = url.subSequence(1, url.length()).toString().trim();
			return userNameIdMap.get(name);
		}
	};

	private static final String SCHEME_USER = "fanfou://user/";

	public static void linkifyUsers(TextView view) {
		Linkify.addLinks(view, PATTERN_USER, SCHEME_USER, MATCH_FILTER_USER,
				TRANSFORM_USER);
	}

	private static final Pattern PATTERN_SEARCH = Pattern.compile("#\\w+#");

	private static final Linkify.TransformFilter TRANSFORM_SEARCH = new Linkify.TransformFilter() {
		@Override
		public final String transformUrl(Matcher match, String url) {
			String result = url.substring(1, url.length() - 1);
			return result;
		}
	};

	private static final String SCHEME_SEARCH = "fanfou://search/";

	public static void linkifyTags(TextView view) {
		Linkify.addLinks(view, PATTERN_SEARCH, SCHEME_SEARCH, null,
				TRANSFORM_SEARCH);
	}

	private static Pattern PATTERN_USERLINK = Pattern
			.compile("@<a href=\"http:\\/\\/fanfou\\.com\\/(.*?)\" class=\"former\">(.*?)<\\/a>");

	private static String preprocessText(String text) {
		// 处理HTML格式返回的用户链接
		Matcher m = PATTERN_USERLINK.matcher(text);
		while (m.find()) {
			userNameIdMap.put(m.group(2), m.group(1));
			if(App.DEBUG){
				Log.d(TAG, "preprocessText() screenName="+m.group(2)+" userId="+m.group(1)); 
			}
		}
		// 将User Link的连接去掉
//		StringBuffer sb = new StringBuffer();
//		m = PATTERN_USERLINK.matcher(text);
//		while (m.find()) {
//			m.appendReplacement(sb, "@$2");
//		}
//		m.appendTail(sb);
//		if(App.DEBUG){
//			Log.d(TAG, "preprocessText() result="+sb.toString()); 
//		}
//		return sb.toString();
		
		return Html.fromHtml(text).toString();
	}
	
	private HashMap<String, String> extractNames(final String text){
		HashMap<String, String> names=new HashMap<String, String>();
		// 处理HTML格式返回的用户链接
		Matcher m = PATTERN_USERLINK.matcher(text);
		while (m.find()) {
			names.put(m.group(2), m.group(1));
			if(App.DEBUG){
				Log.d(TAG, "extractNames() screenName="+m.group(2)+" userId="+m.group(1)); 
			}
		}
		return names;
	}

	public static String getSimpifiedText(String text) {
		return Html.fromHtml(text).toString();
	}

	public static void setStatus(final TextView textView, final String text) {
		String processedText = preprocessText(text);
		textView.setText(Html.fromHtml(processedText), BufferType.SPANNABLE);
		Linkify.addLinks(textView, Linkify.WEB_URLS);
		linkifyUsers(textView);
		linkifyTags(textView);
		userNameIdMap.clear();
	}
	
	 public static void removeUnderlines(TextView textView) {
	        Spannable s = (Spannable)textView.getText();
	        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
	        for (URLSpan span: spans) {
	            int start = s.getSpanStart(span);
	            int end = s.getSpanEnd(span);
	            s.removeSpan(span);
	            span = new Linkify.URLSpanNoUnderline(span.getURL());
	            s.setSpan(span, start, end, 0);
	        }
	        textView.setText(s);
	    }

	/**
	 * 从消息中获取全部提到的人，将它们按先后顺序放入一个列表
	 * 
	 * @param text
	 *            消息文本
	 * @return 消息中@的人的列表，按顺序存放
	 */
	public static HashSet<String> getMentionedNames(Status status) {
		HashSet<String> names = new HashSet<String>();

		final Pattern p = Pattern.compile("@(.*?)\\s");
		final int MAX_NAME_LENGTH = 12;

		Matcher m = p.matcher(status.simpleText);
		while (m.find()) {
			String name = m.group(1);
			if (name.length() <= MAX_NAME_LENGTH + 1) {
				names.add(m.group(1));
			}
		}
		names.add(status.userScreenName);
		String name = App.me.userScreenName;
		names.remove(name);
		return names;
	}
}
