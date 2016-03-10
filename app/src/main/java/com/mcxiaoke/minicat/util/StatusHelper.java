package com.mcxiaoke.minicat.util;

import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.StatusModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author mcxiaoke
 * @version 2.0 2012.03.18
 */
public class StatusHelper {

    private static final String TAG = "StatusHelper";
    private static final int COLOR_HIGHLIGHT = 0xFFFF6666;
    private static final Pattern PATTERN_HIGHLIGHT = Pattern.compile("<b>(\\w+?)</b>");
    private static final Pattern PATTERN_USER = Pattern.compile("(@.+?)\\s+", Pattern.MULTILINE);
    private static final String SCHEME_USER = "fanfouapp://profile/";
    private static final Pattern PATTERN_SEARCH = Pattern.compile("#\\w+#");
    private static final Linkify.TransformFilter TRANSFORM_SEARCH = new Linkify.TransformFilter() {
        @Override
        public final String transformUrl(Matcher match, String url) {
            return url.substring(1, url.length() - 1);
        }
    };
    private static final String SCHEME_SEARCH = "fanfouapp://search/";
    private static final int LINK_COLOR = 0xff28a5c0;
    /**
     * 从消息中获取全部提到的人，将它们按先后顺序放入一个列表
     *
     * @param text
     * 消息文本
     * @return 消息中@的人的列表，按顺序存放
     */
    private static final Pattern namePattern = Pattern.compile("@(.*?)\\s");
    private static final int MAX_NAME_LENGTH = 12;
    private static Pattern PATTERN_USERLINK = Pattern
            .compile("<a href=\"http://fanfou\\.com/(.*?)\" class=\"former\">(.*?)</a>");

    public static void linkifyUsers(final Spannable spannable, final HashMap<String, String> mentions) {
//        LogUtil.v(TAG, "linkifyUsers:mentions:" + mentions.keySet());
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

    public static void linkifyTags(final Spannable spannable) {
        Linkify.addLinks(spannable, PATTERN_SEARCH, SCHEME_SEARCH, null,
                TRANSFORM_SEARCH);
    }

    private static List<String> findHighlightWords(final String htmlText) {
        final Matcher m = PATTERN_HIGHLIGHT.matcher(htmlText);
        List<String> words = new ArrayList<>();
        while (m.find()) {
            final String word = m.group(1);
            words.add(word);
        }
        return words;
    }

    private static HashMap<String, String> findMentions(final String htmlText) {
        final HashMap<String, String> map = new HashMap<String, String>();
        final Matcher m = PATTERN_USERLINK.matcher(htmlText);
        while (m.find()) {
            final String userId = m.group(1);
            final String screenName = Html.fromHtml(m.group(2)).toString();
            map.put(screenName, userId);
        }
        return map;
    }

    public static void setStatus(final TextView textView, final String text) {
        final String htmlText = text + " ";
//        LogUtil.v(TAG, "setStatus:htmlText:" + htmlText);
        final HashMap<String, String> mentions = findMentions(htmlText);
//        LogUtil.v(TAG, "setStatus:mentions:" + mentions);
        final String plainText = Html.fromHtml(htmlText).toString();
//        LogUtil.v(TAG, "setStatus:plainText:" + plainText);
        final SpannableString spannable = new SpannableString(plainText);
        Linkify.addLinks(spannable, Linkify.WEB_URLS);
        linkifyUsers(spannable, mentions);
        linkifyTags(spannable);
        removeUnderLines(spannable);
//        LogUtil.v(TAG, "setStatus:finalText:" + spannable);
        textView.setText(spannable, BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setItemStatus(final TextView textView, final String text) {
        final String htmlText = text + " ";
        final List<String> highlightWords = findHighlightWords(htmlText);
        final String plainText = Html.fromHtml(htmlText).toString();
        final SpannableString spannable = new SpannableString(plainText);
        Linkify.addLinks(spannable, Linkify.WEB_URLS);
        final Matcher m = PATTERN_USER.matcher(spannable);
        while (m.find()) {
            int start = m.start(1);
            int end = m.end(1);
            if (start >= 0 && start < end) {
                spannable.setSpan(new ForegroundColorSpan(LINK_COLOR), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        linkifyTags(spannable);
        removeUnderLines(spannable);
        applyHighlightSpan(spannable, highlightWords);
        textView.setText(spannable, BufferType.SPANNABLE);
    }

    public static void removeUnderLines(final SpannableString spannable) {

        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (final URLSpan span : spans) {
            int start = spannable.getSpanStart(span);
            int end = spannable.getSpanEnd(span);
            spannable.removeSpan(span);
            spannable.setSpan(new URLSpanNoUnderline(span.getURL()), start, end, 0);
        }
    }

    public static void applyHighlightSpan(final SpannableString span,
                                          final List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }
//        LogUtil.v(TAG, "applyHighlightSpan() keywords:" + keywords);
        for (final String keyword : keywords) {
            if (!TextUtils.isEmpty(keyword)) {
                final Pattern pattern = Pattern.compile(keyword);
                Matcher m = pattern.matcher(span);
                if (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    span.setSpan(new ForegroundColorSpan(COLOR_HIGHLIGHT), start,
                            end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

    }

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

    public static class URLSpanNoUnderline extends URLSpan {

        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            super.updateDrawState(tp);
            tp.setUnderlineText(false);
            tp.setColor(LINK_COLOR);
        }
    }

}
