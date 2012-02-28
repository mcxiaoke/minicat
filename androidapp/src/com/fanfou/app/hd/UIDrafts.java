package com.fanfou.app.hd;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fanfou.app.hd.adapter.RecordCursorAdaper;
import com.fanfou.app.hd.dao.model.RecordColumns;
import com.fanfou.app.hd.dao.model.RecordModel;
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
 * @version 2.0 2012.02.21
 * 
 */
public class UIDrafts extends UIBaseSupport implements OnItemClickListener {
	private ListView mListView;

	private Cursor mCursor;
	private RecordCursorAdaper mAdapter;

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
		mCursor = managedQuery(RecordColumns.CONTENT_URI, null, null,
				null, null);
		mAdapter = new RecordCursorAdaper(this, mCursor);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected int getPageType() {
		return PAGE_DRAFTS;
	}

	private void onMenuClearClick() {
		getContentResolver().delete(RecordColumns.CONTENT_URI, null, null);
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
		final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		if (cursor != null) {
			final RecordModel record = RecordModel.from(cursor);
			goWritePage(record);
		}
	}

	private void goWritePage(final RecordModel record) {
		if (record == null) {
			return;
		}

		Intent intent = new Intent(this, UIWrite.class);
		intent.putExtra("type", record.getType());
		intent.putExtra("text", record.getText());
		intent.putExtra("id", record.getId());
		intent.putExtra("reply", record.getReply());
		if (!StringHelper.isEmpty(record.getFile())) {
			intent.putExtra("data", new File(record.getFile()));
		}
		startActivity(intent);
		finish();

	}

	@Override
	protected void initialize() {
	}

}
