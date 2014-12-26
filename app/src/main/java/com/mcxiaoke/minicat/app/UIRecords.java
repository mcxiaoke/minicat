package com.mcxiaoke.minicat.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.RecordCursorAdaper;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfo;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfoColumns;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.Utils;

/**
 * @author mcxiaoke
 * @version 2.1 2012.03.02
 */
public class UIRecords extends UIBaseSupport implements OnItemClickListener {
    private ListView mListView;

    private Cursor mCursor;
    private RecordCursorAdaper mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drafts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_clearall == item.getItemId()) {
            onMenuClearClick();
            return true;
        } else if (R.id.menu_sendall == item.getItemId()) {
            doSendAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, "删除此条草稿");
        menu.add(0, 1, 0, "清空草稿箱");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                deleteRecord(info.position);
                return true;
            case 1:
                onMenuClearClick();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteRecord(int position) {
        final Cursor cursor = (Cursor) mAdapter.getItem(position);
        if (cursor != null) {
            int id = DataController.parseInt(cursor, BaseColumns._ID);
            int result = DataController.deleteRecord(mContext, id);
            if (result > 0) {
//                mCursor.requery();
                Utils.notify(mContext, "删除成功");
            }
        }
    }

    protected void setLayout() {
        getActionBar().setTitle("草稿箱");
        setContentView(R.layout.list_drafts);
        setProgressBarIndeterminateVisibility(false);

        setListView();

    }

    private void setListView() {
        mCursor = managedQuery(StatusUpdateInfoColumns.CONTENT_URI, null, null, null,
                null);
        mAdapter = new RecordCursorAdaper(this, mCursor);
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);
    }

    private void onMenuClearClick() {
        DataController.clear(mContext, StatusUpdateInfoColumns.CONTENT_URI);
        Utils.notify(this, "草稿箱已清空");
    }

    private void startSendService() {
        Intent i = new Intent(mContext, SyncService.class);
        i.putExtra("type", SyncService.DRAFTS_SEND);
        startService(i);
    }

    private void doSendAll() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定发送所有草稿吗？");
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSendService();
                onMenuHomeClick();
            }
        });
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            final StatusUpdateInfo record = StatusUpdateInfo.from(cursor);
            showWrite(record);
        }
    }

    private void showWrite(final StatusUpdateInfo info) {
        if (info == null) {
            return;
        }

        UIController.goBackToWrite(this, info);
        finish();

    }
}
