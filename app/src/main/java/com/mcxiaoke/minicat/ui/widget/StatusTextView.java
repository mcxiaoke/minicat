package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mcxiaoke.minicat.util.PatternHelper;

import java.util.regex.Pattern;

public class StatusTextView extends TextView {
    public static final Uri URI_PERSONAL_INFO = Uri.parse("shejiaomao://info/");         // 用户资料查看地址
    public static final Uri URI_TOPIC = Uri.parse("shejiaomao://topic/");        // 话题查看地址
    private final static String TAG = "RichTextView";


    public StatusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.getPaint().setUnderlineText(false);
    }

    public StatusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.getPaint().setUnderlineText(false);
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable s;
        if (text instanceof Spannable) {
            s = (Spannable) text;
        } else {
            if (text == null) {
                text = "";
            }
            s = Spannable.Factory.getInstance().newSpannable(text);
        }

        //email
        Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);

        //metion
        Pattern mentionPattern = PatternHelper.getMentionPattern();
        if (mentionPattern != null) {
            Linkify.addLinks(s, mentionPattern, URI_PERSONAL_INFO.toString());
        }

        //topic
        Pattern topicPattern = PatternHelper.getTopicPattern();
        if (topicPattern != null) {
            Linkify.addLinks(s, topicPattern, URI_TOPIC.toString());
        }

        //url
        Pattern urlPattern = PatternHelper.getUrlPattern();
        if (urlPattern != null) {
            Linkify.addLinks(s, urlPattern, "http://");

        }

        stripUnderlines(s);
        text = s;

        super.setText(text, type);

        if (this.getLinksClickable()) {
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void stripUnderlines(Spannable s) {
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            super.updateDrawState(tp);
            tp.setUnderlineText(false);
        }
    }
}



