package com.mcxiaoke.fanfouapp.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.ui.widget.HorizontalTextGroupView;

public class UIDebugMode extends UIBaseSupport {

    private ViewGroup mContainer;
    private HorizontalTextGroupView mTextGroupView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_debug);
        mContainer = (ViewGroup) findViewById(R.id.container);
    }

    @Override
    protected int getMenuResourceId() {
        return super.getMenuResourceId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
