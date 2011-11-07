package com.fanfou.app.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fanfou.app.App;
import com.fanfou.app.api.Status;

import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.26
 *
 */
public class StatusHelper {
	private static final String TAG = "StatusHelper";

	private static HashMap<String, String> userLinks = new HashMap<String, String>();

	private static final String SCHEME_TIMELINE = "fanfou://timeline/";

	private static final Pattern PATTERN_USER = Pattern.compile("@.+?\\s");
	private static final Linkify.MatchFilter MATCH_FILTER_USER = new Linkify.MatchFilter() {
		@Override
		public final boolean acceptMatch(final CharSequence s, final int start,
				final int end) {

			String name = s.subSequence(start + 1, end).toString().trim();
			boolean result = userLinks.containsKey(name);
			return result;
		}
	};

	private static final Linkify.TransformFilter TRANSFORM_USER = new Linkify.TransformFilter() {

		@Override
		public String transformUrl(Matcher match, String url) {
			String name = url.subSequence(1, url.length()).toString().trim();
			return userLinks.get(name);
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

	private static final Pattern PATTERN_SUPPORT = Pattern.compile("@\\S+");
	private static final Linkify.TransformFilter TRANSFORM_SUPPORT = new Linkify.TransformFilter() {
		@Override
		public final String transformUrl(Matcher match, String url) {
			String result = url.substring(1, url.length());
			return "androidsupport";
		}
	};

	public static void linkifySupport(TextView text) {
		Linkify.addLinks(text, PATTERN_SUPPORT, SCHEME_USER, null,
				TRANSFORM_SUPPORT);
	}

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
			userLinks.put(m.group(2), m.group(1));
		}

		// 将User Link的连接去掉
		StringBuffer sb = new StringBuffer();
		m = PATTERN_USERLINK.matcher(text);
		while (m.find()) {
			m.appendReplacement(sb, "@$2");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static String getSimpifiedText(String text) {
		return Html.fromHtml(text).toString();
	}

	public static void setStatus(TextView textView, String text) {
		String processedText = preprocessText(text);
		textView.setText(Html.fromHtml(processedText), BufferType.SPANNABLE);
		Linkify.addLinks(textView, Linkify.ALL);
		linkifyUsers(textView);
		linkifyTags(textView);
		userLinks.clear();
	}

	/**
	 * 从消息中获取全部提到的人，将它们按先后顺序放入一个列表
	 * 
	 * @param text
	 *            消息文本
	 * @return 消息中@的人的列表，按顺序存放
	 */
	public static HashSet<String> getMentionedNames(Status status) {
		HashSet<String> names=new HashSet<String>();

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
		String name=App.me.userScreenName;
		names.remove(name);
		return names;
	}
}
