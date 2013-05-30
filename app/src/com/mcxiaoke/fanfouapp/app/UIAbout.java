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

}
