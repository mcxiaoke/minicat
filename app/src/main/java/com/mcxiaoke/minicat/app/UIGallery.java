package com.mcxiaoke.minicat.app;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.fragment.GalleryFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

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
        int index = getIntent().getIntExtra("index", 0);
        String userId = getIntent().getStringExtra("userId");
        setContentView(R.layout.ui_gallery);
        getFragmentManager().beginTransaction().replace(R.id.content, GalleryFragment.newInstance(userId, index)).commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        System.gc();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_enter_2, R.anim.zoom_exit_2);
    }
}
