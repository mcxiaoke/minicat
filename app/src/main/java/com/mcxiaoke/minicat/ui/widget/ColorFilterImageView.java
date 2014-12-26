/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A @{code ImageView} which grey out the icon if disabled.
 */
public class ColorFilterImageView extends ImageView {
    private final int DISABLED_COLOR;
    private boolean mFilterEnabled = true;

    public ColorFilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DISABLED_COLOR = context.getResources().getColor(
                android.R.color.darker_gray);
    }

    public ColorFilterImageView(Context context) {
        this(context, null);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mFilterEnabled) {
            if (enabled) {
                clearColorFilter();
            } else {
                setColorFilter(DISABLED_COLOR);
            }
        }
    }

    public void enableFilter(boolean enabled) {
        mFilterEnabled = enabled;
    }
}
