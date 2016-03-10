package com.mcxiaoke.minicat.app;

import android.os.Bundle;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.fragment.PhotosFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.app
 * User: mcxiaoke
 * Date: 13-5-26
 * Time: 下午8:05
 */
public class UIPhotos extends UIBaseSupport {
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            finish();
            return;
        }
        setContentView(R.layout.ui_container);
        getActionBar().setTitle("相册");
        getFragmentManager().beginTransaction().replace(R.id.content, PhotosFragment.newInstance(user)).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        System.gc();
    }

    @Override
    protected void onMenuHomeClick() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right);
    }
}
