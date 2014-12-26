package com.mcxiaoke.minicat.quickaction;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import com.mcxiaoke.minicat.R;

import java.util.ArrayList;
import java.util.List;

public class QuickAction extends PopupWindows implements OnDismissListener {
    private static final android.widget.LinearLayout.LayoutParams ACTION_ITEM_PARAMS = new android.widget.LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
    private ViewGroup mRootView;
    private LayoutInflater mInflater;
    private OnActionItemClickListener mItemClickListener;
    private OnDismissListener mDismissListener;
    private List<ActionItem> actionItems = new ArrayList<ActionItem>();
    private int mChildPos;
    private int mInsertPos;
    private int rootWidth = 0;
    ;

    public QuickAction(Context context) {
        super(context);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRootViewId(R.layout.popup);
        mChildPos = 0;
    }

    /**
     * Get action item at an index
     *
     * @param index Index of item (position from callback)
     * @return Action Item at the position
     */
    public ActionItem getActionItem(int index) {
        return actionItems.get(index);
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    public void setRootViewId(int id) {
        mRootView = (ViewGroup) mInflater.inflate(id, null);
        mRootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        setContentView(mRootView);
    }

    /**
     * Set listener for action item clicked.
     *
     * @param listener Listener
     */
    public void setOnActionItemClickListener(OnActionItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Add action item
     *
     * @param action {@link ActionItem}
     */
    public void addActionItem(ActionItem action) {
        actionItems.add(action);

        Drawable icon = action.getIcon();

        View actionItem = mInflater.inflate(R.layout.popup_action_item, null);
        ImageView img = (ImageView) actionItem
                .findViewById(R.id.action_item_icon);

        if (icon != null) {
            img.setImageDrawable(icon);
        } else {
            img.setVisibility(View.GONE);
        }

        final int pos = mChildPos;
        final int actionId = action.getActionId();

        actionItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(QuickAction.this, pos,
                            actionId);
                }
                dismiss();
            }
        });

        actionItem.setFocusable(true);
        actionItem.setClickable(true);

        mRootView.addView(actionItem, mInsertPos, ACTION_ITEM_PARAMS);

        mChildPos++;
        mInsertPos++;
    }

    private LayoutParams getLayoutParams() {
        return new android.widget.LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or
     * bottom of anchor view.
     */
    public void show(View anchor) {
        preShow();

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());

        mRootView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        if (rootWidth == 0) {
            rootWidth = mRootView.getMeasuredWidth();
        }

        int xPos, yPos;
        xPos = anchorRect.left;
        yPos = anchorRect.bottom;

        mWindow.setAnimationStyle(R.style.Animations_Move);
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    /**
     * Set listener for window dismissed. This listener will only be fired if
     * the quicakction dialog is dismissed by clicking outside the dialog or
     * clicking on sticky item.
     */
    public void setOnDismissListener(QuickAction.OnDismissListener listener) {
        setOnDismissListener(this);

        mDismissListener = listener;
    }

    @Override
    public void onDismiss() {
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    /**
     * Listener for item click
     */
    public interface OnActionItemClickListener {
        public abstract void onItemClick(QuickAction source, int pos,
                                         int actionId);
    }

    /**
     * Listener for window dismiss
     */
    public interface OnDismissListener {
        public abstract void onDismiss();
    }
}