package com.mcxiaoke.fanfouapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.ui.widget.HorizontalTextGroupView;

public class UIDebugMode extends Activity {

    private ViewGroup mContainer;
    private HorizontalTextGroupView mTextGroupView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ui_debug);
        setProgressBarIndeterminateVisibility(false);
        mContainer = (ViewGroup) findViewById(R.id.container);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

}
