package com.mcxiaoke.minicat.util;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.StatusModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author mcxiaoke
 * @version 2.0 2012.03.18
 */
public class StatusHelper {

    public static class URLSpanNoUnderline extends URLSpan {

        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            super.updateDrawState(tp);
            tp.setUnderlineText(false);
        }
    }

    private static final String TAG = "StatusHelper";

    private static final Pattern PATTERN_USER = Pattern.compile("@.+?\\s");

    private static final String SCHEME_USER = "fanfouapp://profile/";

    public static void linkifyUsers(final Spannable spannable, final HashMap<String, String> mentions) {
        final Linkify.MatchFilter filter = new Linkify.MatchFilter() {
            @Override
            public final boolean acceptMatch(final CharSequence s, final int start,
                                             final int end) {
                String name = s.subSequence(start + 1, end).toString().trim();
                return mentions.containsKey(name);
            }
        };
        final Linkify.TransformFilter transformer = new Linkify.TransformFilter() {

            @Override
            public String transformUrl(Matcher match, String url) {
                String name = url.subSequence(1, url.length()).toString().trim();
                return mentions.get(name);
            }
        };
        Linkify.addLinks(spannable, PATTERN_USER, SCHEME_USER, filter,
                transformer);
    }

    private static final Pattern PATTERN_SEARCH = Pattern.compile("#\\w+#");

    private static final Linkify.TransformFilter TRANSFORM_SEARCH = new Linkify.TransformFilter() {
        @Override
        public final String transformUrl(Matcher match, String url) {
            return url.substring(1, url.length() - 1);
        }
    };

    private static final String SCHEME_SEARCH = "fanfouapp://search/";

    public static void linkifyTags(final Spannable spannable) {
        Linkify.addLinks(spannable, PATTERN_SEARCH, SCHEME_SEARCH, null,
                TRANSFORM_SEARCH);
    }

    private static Pattern PATTERN_USERLINK = Pattern
            .compile("<a href=\"http://fanfou\\.com/(.*?)\" class=\"former\">(.*?)</a>");

    private static HashMap<String, String> findMentions(final String text) {
        final HashMap<String, String> map = new HashMap<String, String>();
        final Matcher m = PATTERN_USERLINK.matcher(text);
        while (m.find()) {
            map.put(m.group(2), m.group(1));
            if (AppContext.DEBUG) {
                Log.d(TAG, "findMentions() screenName=" + m.group(2)
                        + " userId=" + m.group(1));
            }
        }
        return map;
    }

    public static void setStatus(final TextView textView, final String htmlText) {
        final HashMap<String, String> mentions = findMentions(htmlText);
        final SpannableString spannable = new SpannableString(Html.fromHtml(htmlText).toString());
        Linkify.addLinks(textView, Linkify.WEB_URLS);
        linkifyUsers(spannable, mentions);
        linkifyTags(spannable);
        removeUnderlines(spannable);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void removeUnderlines(final Spannable s) {
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
    }

    /**
     * 从消息中获取全部提到的人，将它们按先后顺序放入一个列表
     *
     * @param text
     * 消息文本
     * @return 消息中@的人的列表，按顺序存放
     */
    private static final Pattern namePattern = Pattern.compile("@(.*?)\\s");
    private static final int MAX_NAME_LENGTH = 12;

    public static ArrayList<String> getMentions(final StatusModel status) {
        String text = status.getSimpleText();
        ArrayList<String> names = new ArrayList<String>();
        names.add(status.getUserScreenName());
        Matcher m = namePattern.matcher(text);
        while (m.find()) {
            String name = m.group(1);
            if (!names.contains(name) && name.length() <= MAX_NAME_LENGTH + 1) {
                names.add(m.group(1));
            }
        }
        String name = AppContext.getScreenName();
        names.remove(name);
        return names;
    }

}
