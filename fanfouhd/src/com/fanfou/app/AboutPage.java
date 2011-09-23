package com.fanfou.app;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import com.fanfou.app.api.ApiConfig;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Status;
import com.fanfou.app.http.BasicClient;
import com.fanfou.app.http.Request;
import com.fanfou.app.http.Response;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.util.StatusHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 20110826
 * 
 */
public class AboutPage extends BaseActivity {
	public static final String COPYRIGHT = "\u00a9";
	public static final String REGISTERED = "\u00ae";

	private ActionBar mActionBar;

	private TextView mTitle;
	private TextView mVersion;
	private TextView mIntroduction;
	private TextView mSupport;
	private TextView mSupportText;
	private TextView mContact;
	private TextView mContactText;
	private TextView mCopyright;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayout();
	}

	private void setLayout() {
		setContentView(R.layout.about);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setLeftAction(new ActionBar.BackAction(this));
		mActionBar.setTitle("关于饭否客户端");

		mTitle = (TextView) findViewById(R.id.about_title);
		mVersion = (TextView) findViewById(R.id.about_version);
		mIntroduction = (TextView) findViewById(R.id.about_intro);
		mSupport = (TextView) findViewById(R.id.about_support);
		mSupportText = (TextView) findViewById(R.id.about_support_text);
		mContact = (TextView) findViewById(R.id.about_contact);
		mContactText = (TextView) findViewById(R.id.about_contact_text);
		mCopyright = (TextView) findViewById(R.id.about_copyright);

		mTitle.setText("饭否Android客户端");
		TextPaint t1 = mTitle.getPaint();
		t1.setFakeBoldText(true);
		mVersion.setText("版本：" + Utils.getViersionInfo());
		mIntroduction.setText(R.string.introduction_text);
		mSupport.setText("技术支持");
		TextPaint t2 = mSupport.getPaint();
		t2.setFakeBoldText(true);
		mSupportText.setText(R.string.support_text);
		StatusHelper.linkifySupport(mSupportText);
		mContact.setText("联系方式");
		TextPaint t3 = mContact.getPaint();
		t3.setFakeBoldText(true);
		mContactText.setText(R.string.contact_text);
		mCopyright.setText("\u00a9 2007-2011 fanfou.com");

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
