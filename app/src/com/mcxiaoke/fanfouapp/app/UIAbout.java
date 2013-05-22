package com.mcxiaoke.fanfouapp.app;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.util.IOHelper;
import com.mcxiaoke.fanfouapp.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mcxiaoke
 * @version 1.2 2011.11.17
 */
public class UIAbout extends UIBaseSupport {
    private static final String LICENSE_FILE_NAME = "apache-license-2.0.txt";

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("关于");
        setLayout();
    }

    protected void setLayout() {
        setContentView(R.layout.about);
        setProgressBarIndeterminateVisibility(false);

        mTextView = (TextView) findViewById(R.id.about_text);

        StringBuilder builder = new StringBuilder();
        builder.append("Introduction\n    MoguFan is a simple Holo style fanfou app, created by mcxiaoke.\n\n\n");
        builder.append(loadLicenseText());
        mTextView.setText(builder);

    }

    private String loadLicenseText() {
        StringBuilder builder = new StringBuilder();
        AssetManager am = getAssets();
        InputStream is = null;
        try {
            is = am.open(LICENSE_FILE_NAME);
            return convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOHelper.closeStream(is);
        }
        return builder.toString();
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8")
                .useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    protected int getMenuResourceId() {
        return 0;
    }

    // private void linkifySupport(final TextView textView) {
    // textView.setMovementMethod(LinkMovementMethod.getInstance());
    // Spannable span = (Spannable) textView.getText();
    // String text = textView.getText().toString();
    // String spanText = "@Android客户端";
    // int start = text.indexOf(spanText);
    // if (start > 0) {
    // int end = start + spanText.length();
    // span.setSpan(new Linkify.URLSpanNoUnderline(
    // "fanfouhd://user/androidsupport"), start, end,
    // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    // }
    // }

}
