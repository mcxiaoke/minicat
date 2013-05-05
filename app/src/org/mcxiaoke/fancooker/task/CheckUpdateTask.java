package org.mcxiaoke.fancooker.task;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.service.DownloadService;
import org.mcxiaoke.fancooker.service.VersionInfo;
import org.mcxiaoke.fancooker.util.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


/**
 * @author mcxiaoke
 * @version 1.0 2011.11.10
 * @version 1.1 2012.04.24
 *
 */
public class CheckUpdateTask  extends AsyncTask<Void, Void, VersionInfo>{
	private Context c;
	private final ProgressDialog pd;

	public CheckUpdateTask(Context context) {
		this.c = context;
		pd = new ProgressDialog(c);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd.setTitle("检查更新");
		pd.setMessage("正在检查新版本...");
		pd.setIndeterminate(true);
		pd.show();
	}

	@Override
	protected void onPostExecute(VersionInfo info) {
		pd.dismiss();
		if (info != null && info.versionCode > AppContext.versionCode) {
			DownloadService.showUpdateConfirmDialog(c, info);
		} else {
			Utils.notify(c, "你使用的已经是最新版");
		}
	}

	@Override
	protected VersionInfo doInBackground(Void... params) {
		return DownloadService.fetchVersionInfo();
	}
}
