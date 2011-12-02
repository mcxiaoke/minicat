package com.fanfou.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.fanfou.app.config.Commons;
import com.fanfou.app.preferences.SeekBarPreference;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.update.VersionInfo;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.11
 * @version 1.1 2011.10.25
 * @version 1.5 2011.11.10
 * @version 1.6 2011.11.16
 * @version 1.7 2011.11.25
 * @version 2.0 2011.12.02
 * 
 */
public class OptionsPage extends PreferenceActivity implements
		OnPreferenceClickListener, OnSharedPreferenceChangeListener,
		OnPreferenceChangeListener {
	public static final String TAG = "OptionsPage";

	private boolean needRestart = false;

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
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		App.active = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.active = true;
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.initScreenConfig(this);

		addPreferencesFromResource(R.xml.options);

		ListPreference photoQuality = (ListPreference) findPreference(getText(R.string.option_photo_quality));
		photoQuality.setSummary(photoQuality.getEntry());

		ListPreference picLevel = (ListPreference) findPreference(getText(R.string.option_pic_level));
		picLevel.setSummary(picLevel.getEntry());

		ListPreference bottomWriteIcon = (ListPreference) findPreference(getText(R.string.option_bottom_write_icon));
		bottomWriteIcon.setSummary(bottomWriteIcon.getEntry());

		ListPreference bottomRefreshIcon = (ListPreference) findPreference(getText(R.string.option_bottom_refresh_icon));
		bottomRefreshIcon.setSummary(bottomRefreshIcon.getEntry());

		ListPreference interval = (ListPreference) findPreference(getText(R.string.option_notification_interval));
		interval.setSummary(interval.getEntry());

		Preference currentAccount = findPreference(getText(R.string.option_current_account));
		currentAccount.setSummary("" + App.me.userScreenName + "("
				+ App.me.userId + ")");

		Preference checkUpdate = findPreference(getText(R.string.option_check_update));
		checkUpdate.setOnPreferenceClickListener(this);

		Preference reset = findPreference(getText(R.string.option_clear_data_and_settings));
		reset.setOnPreferenceClickListener(this);

		Preference feedback = findPreference(getText(R.string.option_feedback));
		feedback.setOnPreferenceClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		if (needRestart) {
			android.os.Process.killProcess(android.os.Process.myPid());
		} else {
			super.finish();
		}
	}

	private void checkUpdate() {
		if (App.DEBUG) {
			Log.d(TAG, "checkUpdate");
		}
		if (App.noConnection) {
			Utils.notify(this, "无网络连接，请稍后重试");
			return;
		}
		new CheckTask(this).execute();
	}

	private void feedback() {
		IntentHelper.sendFeedback(this, "");
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (App.DEBUG) {
			Log.d(TAG, "onPreferenceClick key=" + preference.getKey());
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
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return false;

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Preference p = findPreference(key);
		if (key.equals(getString(R.string.option_notification))) {
			CheckBoxPreference cp = (CheckBoxPreference) p;
			NotificationService.set(this, cp.isChecked());
		} else if (key.equals(getString(R.string.option_notification_interval))) {
			NotificationService.set(this);
		} 
		else if(key.equals(getString(R.string.option_autoupdate))){
			CheckBoxPreference cp = (CheckBoxPreference) p;
			if(cp.isChecked()){
				AlarmHelper.setAutoUpdateTask(this);
			}else{
				AlarmHelper.removeAutoUpdateTask(this);
			}
		}
		else if (key.equals(getString(R.string.option_page_scroll_endless))) {
			needRestart = true;
		} else if (key.equals(getString(R.string.option_force_portrait))) {
			CheckBoxPreference cp = (CheckBoxPreference) p;
			if (cp.isChecked()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		} else if (key.equals(getString(R.string.option_bottom_refresh_icon))) {
			ListPreference lp = (ListPreference) p;
			lp.setSummary(lp.getEntry());
			String value = lp.getValue();
			String value2 = OptionHelper.readString(this,
					R.string.option_bottom_write_icon, "none");
			if (value.equals(value2) && !value.equals("none")) {
				lp.setValue("none");
				Utils.notify(this, "请重选，刷新图标和发消息图标不能处于同一位置");
			} else {
				needRestart = true;
			}
		} else if (key.equals(getString(R.string.option_bottom_write_icon))) {
			ListPreference lp = (ListPreference) p;
			lp.setSummary(lp.getEntry());
			String value = lp.getValue();
			String value2 = OptionHelper.readString(this,
					R.string.option_bottom_refresh_icon, "none");
			if (value.equals(value2) && !value.equals("none")) {
				lp.setValue("none");
				Utils.notify(this, "请重选，发消息图标和刷新图标不能处于同一位置");
			} else {
				needRestart = true;
			}
		} else if (key.equals(getString(R.string.option_fontsize))) {
			SeekBarPreference skp = (SeekBarPreference) p;
			int value = sp.getInt(key,
					getResources().getInteger(R.integer.defaultFontSize));
			skp.setSummary(value + "号");
			needRestart = true;
		}else if (p instanceof ListPreference) {
			ListPreference lp = (ListPreference) p;
			lp.setSummary(lp.getEntry());
			setResult(RESULT_OK,
					getIntent().putExtra(Commons.EXTRA_BOOLEAN, true));
			needRestart = true;
		}

	}

	private static class CheckTask extends AsyncTask<Void, Void, VersionInfo> {
		private Context c;
		private final ProgressDialog pd;

		public CheckTask(Context context) {
			this.c = context;
			pd = new ProgressDialog(c);
			if (App.DEBUG) {
				Log.i(TAG, "CheckTask init");
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setTitle("检测更新");
			pd.setMessage("正在检测新版本...");
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected void onPostExecute(VersionInfo info) {
			pd.dismiss();
			if (App.DEBUG) {
				if (info != null) {
					DownloadService.showUpdateConfirmDialog(c, info);
				}
				return;
			}
			if (info != null && info.versionCode > App.me.appVersionCode) {
				DownloadService.showUpdateConfirmDialog(c, info);
			} else {
				Utils.notify(c, "你使用的已经是最新版");
			}
		}

		@Override
		protected VersionInfo doInBackground(Void... params) {
			VersionInfo info = DownloadService.fetchVersionInfo();
			if (App.DEBUG) {
				Log.d(TAG, "doInBackground " + info);
			}
			return info;
		}
	}

}
