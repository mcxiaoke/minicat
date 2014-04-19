package com.mcxiaoke.minicat.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;
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
    private LoadDataTask mLoadDataTask;

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
        user = getArguments().getParcelable("user");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StatusModel status = (StatusModel) parent.getItemAtPosition(position);
        if (status != null) {
//            UIController.showPhoto(getActivity(), status.getPhotoLargeUrl());
            UIController.showGallery(getActivity(), mData, position);
        }
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

    private void showProgress() {
        mGridView.setVisibility(View.GONE);
        mEmptyViewController.showProgress();
    }

    private void showContent(int size) {
        mEmptyViewController.hideProgress();
        if (size < 10) {
//            mGridView.setNumColumns(1);
//            mGridView.setPadding(mGridViewPaddingMax, mGridViewPaddingMax, mGridViewPaddingMax, mGridViewPaddingMax);
        } else {
//            mGridView.setVerticalSpacing(mGridViewPaddingMin);
//            mGridView.setHorizontalSpacing(mGridViewPaddingMin);
//            mGridView.setNumColumns(3);
        }
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showEmpty(String text) {
        mGridView.setVisibility(View.GONE);
        mEmptyViewController.showEmpty(text);
    }

    private void stopTask() {
        if (mLoadDataTask != null) {
            mLoadDataTask.stop();
            mLoadDataTask = null;
        }
    }

    private void startTask() {
        stopTask();
        mLoadDataTask = new LoadDataTask();
        mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LoadDataTask extends AsyncTask<Void, Void, List<StatusModel>> {

        private volatile boolean mCancelled;
        private Exception mException;

        public LoadDataTask() {
            mCancelled = false;
        }

        public void stop() {
            mCancelled = true;
            cancel(true);
        }

        @Override
        protected List<StatusModel> doInBackground(Void... params) {
            final String id = user.getId();
            List<StatusModel> statusModels = null;
            try {
                Paging paging = new Paging();
                paging.count = 60;
                statusModels = AppContext.getApi().getPhotosTimeline(id, paging);
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }
            return statusModels;
        }

        @Override
        protected void onPostExecute(List<StatusModel> statusModels) {
            super.onPostExecute(statusModels);
            getBaseSupport().hideProgressIndicator();
            if (mCancelled) {
                showEmpty("");
            } else {
                if (statusModels != null && statusModels.size() > 0) {
                    int size = statusModels.size();
                    showContent(size);
                    mArrayAdapter.addAll(statusModels);
                    mArrayAdapter.notifyDataSetChanged();
                } else {
                    if (mException != null) {
                        showEmpty("无法获取数据");
                    } else {
                        showEmpty("没有数据");
                    }
                }
            }

        }
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
