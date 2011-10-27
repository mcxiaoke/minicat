package com.fanfou.app;

import java.io.File;

import org.apache.http.client.utils.URIUtils;

import com.fanfou.app.adapter.DraftsCursorAdaper;
import com.fanfou.app.api.Draft;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.27
 * 
 */
public class DraftsPage extends BaseActivity implements OnItemClickListener{
	private ActionBar mBar;
	private ListView mListView;

	private Cursor mCursor;
	private DraftsCursorAdaper mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayout();
	}

	private void setLayout() {
		setContentView(R.layout.list_drafts);
		setActionBar();
		setListView();

	}

	private void setActionBar() {
		mBar = (ActionBar) findViewById(R.id.actionbar);
		mBar.setTitle("草稿箱");
		mBar.setLeftAction(new ActionBar.BackAction(this));
		mBar.setRightAction(new SendAllAction());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected int getPageType() {
		return PAGE_DRAFTS;
	}

	@Override
	protected void onClearClick() {
		getContentResolver().delete(DraftInfo.CONTENT_URI, null, null);
		mCursor.requery();
		Utils.notify(this, "草稿箱已清空");
	}

	private class SendAllAction extends AbstractAction {

		public SendAllAction() {
			super(R.drawable.i_sendall);
		}

		@Override
		public void performAction(View view) {
			doSendAll();
		}

	}

	private void doSendAll() {
		Utils.notify(this, "发送所有");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c=(Cursor) parent.getItemAtPosition(position);
		if(c!=null){
			final Draft draft=Draft.parse(c);
			goWritePage(draft);
		}
	}
	
	private void goWritePage(final Draft draft){
		if(draft==null){
			return;
		}
		
		Intent intent=new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, draft.type);
		intent.putExtra(Commons.EXTRA_TEXT, draft.text);
		intent.putExtra(Commons.EXTRA_IN_REPLY_TO_ID, draft.replyTo);
		if(!StringHelper.isEmpty(draft.filePath)){
			intent.putExtra(Commons.EXTRA_FILE, new File(draft.filePath));
		}
		startActivity(intent);
		deleteDraft(draft);
		finish();
		
	}
	
	private void deleteDraft(final Draft draft){
		Uri uri=ContentUris.withAppendedId(DraftInfo.CONTENT_URI, draft.id);
		getContentResolver().delete(uri, null, null);
	}

}
