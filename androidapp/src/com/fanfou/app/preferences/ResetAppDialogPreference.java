package com.fanfou.app.preferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.AttributeSet;

import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 * 
 */
public class ResetAppDialogPreference extends DialogPreference {

	public ResetAppDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResetAppDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if (which == DialogInterface.BUTTON_POSITIVE) {
			new CleanTask(context).execute();
		}
	}

	private static class CleanTask extends AsyncTask<Void, Void, Boolean> {
		private Context c;
		private ProgressDialog pd = null;

		public CleanTask(Context context) {
			this.c = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(c);
			pd.setTitle("重置所有数据和设置");
			pd.setMessage("正在重置所有数据和设置...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pd.dismiss();
			if (result.booleanValue() == true) {
				Utils.notify(c, "数据和设置已全部重置");
				IntentHelper.goLoginPage(c);
				// android.os.Process.killProcess(android.os.Process.myPid());
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			AlarmHelper.unsetScheduledTasks(c);
			OptionHelper.clearSettings(c);
			IOHelper.cleanDB(c);
			IOHelper.ClearCache(c);
			return true;
		}
	}

}
