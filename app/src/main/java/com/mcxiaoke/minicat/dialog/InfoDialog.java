package com.mcxiaoke.minicat.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class InfoDialog extends AlertDialog implements
        DialogInterface.OnClickListener {
    private DialogListener mListener;

    public InfoDialog(Context context) {
        super(context);
        initialize(context);
    }

    public InfoDialog(Context context, int theme) {
        super(context, theme);
        initialize(context);
    }

    private void initialize(Context context) {
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), this);
    }

    public DialogListener getClickListener() {
        return mListener;
    }

    public void setClickListener(DialogListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mListener != null) {
                    mListener.onPositiveClick();
                }
                break;
            default:
                break;
        }
        dismiss();
    }

}
