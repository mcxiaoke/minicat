package com.mcxiaoke.minicat.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;

class UserCheckBoxViewHolder {

    final ImageView headIcon;
    final ImageView lockIcon;
    final TextView nameText;
    final TextView idText;
    final TextView genderText;
    final TextView locationText;
    final CheckBox checkBox;

    UserCheckBoxViewHolder(View base) {
        this.headIcon = (ImageView) base.findViewById(R.id.head);
        this.lockIcon = (ImageView) base.findViewById(R.id.lock);
        this.nameText = (TextView) base.findViewById(R.id.name);
        this.genderText = (TextView) base.findViewById(R.id.gender);
        this.locationText = (TextView) base.findViewById(R.id.location);
        this.idText = (TextView) base.findViewById(R.id.id);
        this.checkBox = (CheckBox) base.findViewById(R.id.checkbox);
    }

}
