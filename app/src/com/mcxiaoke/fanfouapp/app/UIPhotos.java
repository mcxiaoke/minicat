package com.mcxiaoke.fanfouapp.app;

import android.os.Bundle;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.fragment.PhotosFragment;

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
        getActionBar().setTitle("照片集");
        getFragmentManager().beginTransaction().replace(R.id.content, PhotosFragment.newInstance(user)).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
