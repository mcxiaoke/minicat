package com.fanfou.app.hd;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.adapter.DraftsCursorAdaper;
import com.fanfou.app.hd.api.Draft;
import com.fanfou.app.hd.db.Contents.DraftInfo;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.QueueService;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.27
 * @version 1.1 2011.10.28
 * @version 1.2 2011.11.11
 * 
 */
public class DraftsPage extends UIBaseSupport implements OnItemClickListener {
	private ListView mListView;

	private Cursor mCursor;
	private DraftsCursorAdaper mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.list_drafts);
		setListView();

	}

	private void setListView() {
		mCursor = managedQuery(DraftInfo.CONTENT_URI, DraftInfo.COLUMNS, null,
				null, null);
		mAdapter = new DraftsCursorAdaper(this, mCursor);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected int getPageType() {
		return PAGE_DRAFTS;
	}

	private void onMenuClearClick() {
		getContentResolver().delete(DraftInfo.CONTENT_URI, null, null);
		mCursor.requery();
		Utils.notify(this, "草稿箱已清空");
		finish();
	}

	private void startTaskQueueService() {
		QueueService.start(this);
	}

	private void doSendAll() {
		final ConfirmDialog dialog = new ConfirmDialog(this, "发送所有",
				"确定发送所有草稿吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

			@Override
			public void onButton1Click() {
				startTaskQueueService();
				onMenuHomeClick();
			}
		});
		dialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		if (c != null) {
			final Draft draft = Draft.parse(c);
			goWritePage(draft);
		}
	}

	private void goWritePage(final Draft draft) {
		if (draft == null) {
			return;
		}

		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Constants.EXTRA_TYPE, draft.type);
		intent.putExtra(Constants.EXTRA_TEXT, draft.text);
		intent.putExtra(Constants.EXTRA_ID, draft.id);
		intent.putExtra(Constants.EXTRA_IN_REPLY_TO_ID, draft.replyTo);
		if (!StringHelper.isEmpty(draft.filePath)) {
			intent.putExtra(Constants.EXTRA_DATA, new File(draft.filePath));
		}
		startActivity(intent);
		finish();

	}

	@Override
	protected void initialize() {
	}

}
