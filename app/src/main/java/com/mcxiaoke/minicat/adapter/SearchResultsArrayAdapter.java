package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.ui.widget.ItemView;
import com.mcxiaoke.minicat.util.StringHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mcxiaoke
 * @version 2.5 2012.03.28
 */
public class SearchResultsArrayAdapter extends BaseAdapter implements
        OnScrollListener {
    private static final String TAG = SearchResultsArrayAdapter.class
            .getSimpleName();
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ImageLoader mLoader;
    protected boolean busy;

    protected List<StatusModel> mData;

    private String mKeyword;
    private Pattern mPattern;
    private int mHighlightColor;

    public SearchResultsArrayAdapter(Context context, int highlightColor) {
        super();
        this.mHighlightColor = highlightColor;
        initialize(context, null);
    }


    public void updateDataAndUI(List<StatusModel> data, String keyword) {
        if (!StringHelper.isEmpty(keyword)) {
            mKeyword = keyword;
            mPattern = Pattern.compile(mKeyword);
            addData(data);
        }
    }

    private void initialize(Context context, List<StatusModel> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mLoader = ImageLoader.getInstance();
        mData = new ArrayList<StatusModel>();
        if (data != null) {
            mData.addAll(data);
        }
    }

    private SpannableStringBuilder buildHighlightSpan(String text) {
        SpannableStringBuilder span = new SpannableStringBuilder(text);
        if (!TextUtils.isEmpty(mKeyword)) {
            Matcher m = mPattern.matcher(span);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                span.setSpan(new ForegroundColorSpan(mHighlightColor), start,
                        end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return span;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public StatusModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_status, parent, false);
            holder = new ViewHolder();
            holder.view = (ItemView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StatusModel s = getData().get(position);
//        StatusHelper.setStatus(holder.view.getContentTextView(), s.getText());
//        holder.view.setPhoto(s.getPhotoThumbUrl(), s.getPhotoLargeUrl());
        UIHelper.setContent(holder.view, s);
        UIHelper.setMetaInfo(holder.view, s);
        UIHelper.setImageClick(holder.view, s.getUserId());
        String headUrl = s.getUserProfileImageUrl();
        mLoader.displayImage(headUrl, holder.view.getImageView());
        return convertView;
    }

    protected List<StatusModel> getData() {
        return mData;
    }

    public void setData(List<StatusModel> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void addData(List<StatusModel> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void remove(StatusModel status) {
        mData.remove(status);
        notifyDataSetChanged();
    }

    public void add(StatusModel status) {
        mData.add(status);
        notifyDataSetChanged();
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

    static class ViewHolder {
        public ItemView view;
    }

}
