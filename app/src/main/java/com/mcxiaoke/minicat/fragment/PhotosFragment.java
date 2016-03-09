package com.mcxiaoke.minicat.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.util.NetworkHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragment
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午9:02
 */
public class PhotosFragment extends AbstractFragment implements AdapterView.OnItemClickListener {
    private ViewGroup mEmptyView;
    private EmptyViewController mEmptyViewController;
    private GridView mGridView;
    private ArrayAdapter<StatusModel> mArrayAdapter;
    private List<StatusModel> mData;
    private UserModel user;
    private Thread mThread;
    private Handler mHandler;
    private volatile boolean mCancelled;

    public static PhotosFragment newInstance(UserModel user) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        user = getArguments().getParcelable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_photos, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = getView();
        mEmptyView = (ViewGroup) root.findViewById(android.R.id.empty);
        mEmptyViewController = new EmptyViewController(mEmptyView);

        mGridView = (GridView) root.findViewById(R.id.gridview);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mData = new ArrayList<StatusModel>();
        mArrayAdapter = new GridViewAdapter(getActivity(), R.id.text, mData);
        mGridView.setOnItemClickListener(this);
        mGridView.setAdapter(mArrayAdapter);
        getActivity().getActionBar().setTitle(user.getScreenName() + "的相册");
        startRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTask();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public String getTitle() {
        return "照片集";
    }

    @Override
    public void startRefresh() {
        startTask();
        getBaseSupport().showProgressIndicator();
        showProgress();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StatusModel status = (StatusModel) parent.getItemAtPosition(position);
        if (status != null) {
//            UIController.showPhoto(getActivity(), status.getPhotoLargeUrl());
            UIController.showGallery(getActivity(), mData, position);
        }
    }

    private void showProgress() {
        mGridView.setVisibility(View.GONE);
        mEmptyViewController.showProgress();
    }

    private void showContent(int size) {
        mEmptyViewController.hideProgress();
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showEmpty(String text) {
        mGridView.setVisibility(View.GONE);
        mEmptyViewController.showEmpty(text);
    }

    private void stopTask() {
        if (mThread != null) {
            mCancelled = true;
            mThread.interrupt();
            mThread = null;
        }
    }

    private void startTask() {
        stopTask();
        loadData();
    }

    private void updateUI(final List<StatusModel> data) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getBaseSupport().hideProgressIndicator();
                if (data != null && data.size() > 0) {
                    int size = data.size();
                    showContent(size);
                    mArrayAdapter.addAll(data);
                    mArrayAdapter.notifyDataSetChanged();
                } else {
                    if (mArrayAdapter.isEmpty()) {
                        showEmpty("无法获取数据");
                    }
                }
            }
        };
        mHandler.post(runnable);

    }

    private void loadData() {

        mThread = new Thread() {
            @Override
            public void run() {
                final String id = user.getId();
                final Api api = AppContext.getApi();
                List<StatusModel> statusModels = new ArrayList<StatusModel>();
                try {
                    // 分页获取，最多3页=180张照片
                    Paging paging = new Paging();
                    paging.count = 60;
                    final int max = NetworkHelper.isWifi(getActivity()) ? 10 : 1;
                    for (int i = 0; i < max; i++) {
                        paging.page = i;
                        if (mCancelled) {
                            break;
                        }
                        if (AppContext.DEBUG) {
                            Log.d("PhotosFragment", "fetch next page photos " + i);
                        }
                        final List<StatusModel> models = api.getPhotosTimeline(id, paging);
                        if (mCancelled) {
                            break;
                        }
                        updateUI(models);
                        if (models == null || models.size() < 60) {
                            break;
                        }
                    }
                    if (AppContext.DEBUG) {
                        Log.d("PhotosFragment", "fetch photos finished");
                    }
                } catch (Exception ex) {
                    if (AppContext.DEBUG) {
                        Log.w("PhotosFragment", "fetch photos error:" + ex);
                    }
                    updateUI(null);
                }
            }
        };
        mThread.start();
    }


    static class GridViewAdapter extends ArrayAdapter<StatusModel> {
        private Picasso picasso;

        public GridViewAdapter(Context context, int textViewResourceId, List<StatusModel> objects) {
            super(context, textViewResourceId, objects);
            picasso = new Picasso.Builder(context).build();
        }

        @Override
        public StatusModel getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_photo, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StatusModel status = getItem(position);
            holder.text.setText(status.getSimpleText());
            picasso.load(status.getPhotoLargeUrl()).placeholder(R.drawable.photo_placeholder).into(holder.image);
            return convertView;
        }

        static class ViewHolder {
            public ImageView image;
            public TextView text;
        }
    }

}
