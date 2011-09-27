package com.fanfou.app.preferences;

import com.fanfou.app.App;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.AttributeSet;

public class CheckUpdateDialogPreference extends DialogPreference {

	public CheckUpdateDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckUpdateDialogPreference(Context context, AttributeSet attrs,
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
			pd.setTitle("清空缓存图片");
			pd.setMessage("正在清空缓存图片...");
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pd.dismiss();
			if (result.booleanValue() == true) {
				Utils.notify(c, "缓存图片已清空");
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(300);
				clean(c);
				return true;
			} catch (InterruptedException e) {
				if (App.DEBUG)
					e.printStackTrace();
				return false;
			}
		}

		private void clean(Context context) {
			IOHelper.ClearCache(context);
		}
	}

}
