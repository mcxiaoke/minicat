package com.fanfou.app;

import com.fanfou.app.update.AutoUpdateManager;
import com.fanfou.app.update.VersionInfo;
import com.fanfou.app.util.DeviceHelper;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class OptionsPage extends PreferenceActivity implements
		OnPreferenceClickListener, OnSharedPreferenceChangeListener {
	public static final String TAG="OptionsPage";

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.options);

		ListPreference fontsize = (ListPreference) findPreference(getText(R.string.option_fontsize));
		fontsize.setSummary(fontsize.getEntry());

		ListPreference photoQuality = (ListPreference) findPreference(getText(R.string.option_photo_quality));
		photoQuality.setSummary(photoQuality.getEntry());

		ListPreference picLevel = (ListPreference) findPreference(getText(R.string.option_pic_level));
		picLevel.setSummary(picLevel.getEntry());

		ListPreference writeIcon = (ListPreference) findPreference(getText(R.string.option_write_icon));
		writeIcon.setSummary(writeIcon.getEntry());
		
		Preference notification=findPreference(getText(R.string.option_notification));
		
		ListPreference interval=(ListPreference) findPreference(getText(R.string.option_notification_interval));
		interval.setSummary(interval.getEntry());

		Preference checkUpdate = findPreference(getText(R.string.option_check_update));
		checkUpdate.setOnPreferenceClickListener(this);

		Preference feedback = findPreference(getText(R.string.option_feedback));
		feedback.setOnPreferenceClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void checkUpdate() {
		if(App.DEBUG){
			Log.i(TAG, "checkUpdate");
		}
		new CheckTask(this).execute();
	}

	private void feedback() {
		IntentHelper.sendFeedback(this, "");
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(App.DEBUG){
			Log.i(TAG, "onPreferenceClick key="+preference.getKey());
		}
		if (preference.getKey().equals(getString(R.string.option_check_update))) {
			checkUpdate();
		} else if (preference.getKey().equals(
				getString(R.string.option_feedback))) {
			feedback();
		}
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference p = findPreference(key);
		if(key.equals(getString(R.string.option_notification))){
			CheckBoxPreference cp=(CheckBoxPreference) p;
			if(cp.isChecked()){
				Utils.notifyOn(this);
			}else{
				Utils.notifyOff(this);
			}
		}
		else if (p instanceof ListPreference) {
			ListPreference lp = (ListPreference) p;
			lp.setSummary(lp.getEntry());
		}

	}

	private static class CheckTask extends AsyncTask<Void, Void, VersionInfo> {
		private Context c;
		private ProgressDialog pd = null;

		public CheckTask(Context context) {
			this.c = context;
			if(App.DEBUG){
				Log.i(TAG, "CheckTask init");
			}
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(c);
			pd.setTitle("检测更新");
			pd.setMessage("正在检测新版本...");
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected void onPostExecute(VersionInfo info) {
			pd.dismiss();
			if (info != null
					&& info.versionCode > App.me.appVersionCode) {
				AutoUpdateManager.showUpdateConfirmDialog(c, info);
			} else {
				Utils.notify(c, "你使用的已经是最新版");
			}
		}

		@Override
		protected VersionInfo doInBackground(Void... params) {
			VersionInfo info= AutoUpdateManager.fetchVersionInfo();
			if(App.DEBUG){
				Log.i(TAG, "doInBackground "+info.toString());
			}
			return info;
		}
	}

}
