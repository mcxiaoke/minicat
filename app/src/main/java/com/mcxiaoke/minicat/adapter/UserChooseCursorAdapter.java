package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.ArrayList;

/**
 * @author mcxiaoke
 * @version 2.1 2012.02.27
 */
public class UserChooseCursorAdapter extends BaseCursorAdapter {

    private ArrayList<Boolean> mStates;
    private SparseBooleanArray mStateMap;

    public UserChooseCursorAdapter(Context context) {
        super(context, null);
        initialize();
    }

    public UserChooseCursorAdapter(Context context, Cursor c) {
        super(context, c);
        initialize();
    }

    private void initialize() {
        mStates = new ArrayList<Boolean>();
        mStateMap = new SparseBooleanArray();
    }

    public ArrayList<Boolean> getCheckedStates() {
        return mStates;
    }

    public void setItemChecked(int position, boolean checked) {
        mStateMap.put(position, checked);
        notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_item_chooseuser;
    }

    private void setTextStyle(UserCheckBoxViewHolder holder) {
        TextPaint tp = holder.nameText.getPaint();
        tp.setFakeBoldText(true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(getLayoutId(), null);
        UserCheckBoxViewHolder holder = new UserCheckBoxViewHolder(view);
        setTextStyle(holder);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View row = view;

        final UserCheckBoxViewHolder holder = (UserCheckBoxViewHolder) row
                .getTag();

        final UserModel u = UserModel.from(cursor);

        String headUrl = u.getProfileImageUrlLarge();
        mImageLoader.displayImage(headUrl, holder.headIcon);

        holder.lockIcon.setVisibility(u.isProtect() ? View.VISIBLE : View.GONE);

        holder.nameText.setText(u.getScreenName());
        holder.idText.setText("(" + u.getId() + ")");
        holder.genderText.setText(u.getGender());
        holder.locationText.setText(u.getLocation());

        Boolean b = mStateMap.get(cursor.getPosition());
        holder.checkBox.setChecked(Boolean.TRUE == b);

    }

}
