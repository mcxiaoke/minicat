package com.mcxiaoke.minicat.fragment;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.push.PushService;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragment
 * User: mcxiaoke
 * Date: 13-5-26
 * Time: 下午8:04
 */
public class OptionFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public OptionFragment() {
    }

    public static OptionFragment newInstance() {
        OptionFragment fragment = new OptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.options);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PackageManager pm = AppContext.getApp().getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(AppContext.getApp().getPackageName(), 0);
        } catch (NameNotFoundException ignored) {
        }

        final Preference about = findPreference(getString(R.string.option_about_key));
        about.setSummary(pi == null ? "1.0.0" : pi.versionName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.option_push_notification_key).equals(key)) {
            PushService.check(getActivity());
        }
    }
}
