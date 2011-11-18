package com.fanfou.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.util.Linkify;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.26
 * @version 1.1 2011.10.26
 * @version 1.2 2011.11.17
 * 
 */
public class AboutPage extends Activity implements OnClickListener {
	public static final String COPYRIGHT = "\u00a9";
	public static final String REGISTERED = "\u00ae";

	private ActionBar mActionBar;

	private ImageView mLogo;

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
		Utils.initScreenConfig(this);
		setLayout();
	}

	private void setLayout() {
		setContentView(R.layout.about);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mLogo = (ImageView) findViewById(R.id.about_icon);
		mLogo.setOnClickListener(this);

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
		linkifySupport(mSupportText);
		mContact.setText("联系方式");
		TextPaint t3 = mContact.getPaint();
		t3.setFakeBoldText(true);
		mContactText.setText(R.string.contact_text);
		mCopyright.setText("\u00a9 2007-2011 fanfou.com");

	}
	
	private void linkifySupport(final TextView textView){
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		Spannable span=(Spannable) textView.getText();
		String text=textView.getText().toString();
		String spanText="@Android客户端";
		int start=text.indexOf(spanText);
		if(start>0){
			int end=start+spanText.length();
			span.setSpan(new Linkify.URLSpanNoUnderline("fanfou://user/androidsupport"), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.about_icon) {
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(uri);
			startActivity(intent);
		}
	}

}
