package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.1 2012.02.27
 */
public abstract class UserArrayAdapter extends BaseAdapter implements
        OnScrollListener {
    private static final String TAG = UserArrayAdapter.class.getSimpleName();
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ImageLoader mLoader;

    protected List<UserModel> mData;
    protected boolean busy;

    public UserArrayAdapter(Context context, List<UserModel> data) {
        super();
        initialize(context, data);
    }

    private void initialize(Context context, List<UserModel> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mLoader = ImageLoader.getInstance();
        mData = new ArrayList<UserModel>();
        if (data != null) {
            mData.addAll(data);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public UserModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(getLayoutId(), parent, false);
            holder = new UserViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (UserViewHolder) convertView.getTag();
        }
        final UserModel u = getData().get(position);

        holder.setUserContent(mContext.getResources(), u);

        String headUrl = u.getProfileImageUrlLarge();
        ImageLoader.getInstance().displayImage(headUrl, holder.head);
        return convertView;
    }

    protected List<UserModel> getData() {
        return mData;
    }

    public void setData(List<UserModel> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void addData(List<UserModel> data) {
        mData.addAll(data);
    }

    protected int getLayoutId() {
        // return R.layout.list_item_user;
        return -1;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                busy = false;
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                busy = true;
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                busy = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

}
