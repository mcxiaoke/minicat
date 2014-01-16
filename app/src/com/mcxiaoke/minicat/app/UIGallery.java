package com.mcxiaoke.minicat.app;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.fragment.GalleryFragment;

import java.util.ArrayList;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.app
 * User: mcxiaoke
 * Date: 13-6-5
 * Time: 下午9:55
 */
public class UIGallery extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0x66333333));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("查看照片");
        ArrayList<String> data = getIntent().getStringArrayListExtra("data");
        int index = getIntent().getIntExtra("index", 0);
        if (data == null || data.isEmpty()) {
            finish();
        }

        setContentView(R.layout.ui_gallery);
        getFragmentManager().beginTransaction().replace(R.id.content, GalleryFragment.newInstance(data, index)).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_enter_2, R.anim.zoom_exit_2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
//                doSave();
                break;
        }
        return true;
    }
}
