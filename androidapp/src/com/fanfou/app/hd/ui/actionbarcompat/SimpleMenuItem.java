/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fanfou.app.hd.ui.actionbarcompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * A <em>really</em> dumb implementation of the {@link android.view.MenuItem} interface, that's only useful for our
 * actionbar-compat purposes. See <code>com.android.internal.view.menu.MenuItemImpl</code> in AOSP for a more complete
 * implementation.
 */
public class SimpleMenuItem implements MenuItem
{

        private SimpleMenu mMenu;

        private final int mId;
        private final int mOrder;
        private CharSequence mTitle;
        private CharSequence mTitleCondensed;
        private Drawable mIconDrawable;
        private int mIconResId = 0;
        private boolean mEnabled = true;

        public SimpleMenuItem ( SimpleMenu menu, int id, int order, CharSequence title )
        {
                mMenu = menu;
                mId = id;
                mOrder = order;
                mTitle = title;
        }

        @Override
		public int getItemId ()
        {
                return mId;
        }

        @Override
		public int getOrder ()
        {
                return mOrder;
        }

        @Override
		public MenuItem setTitle ( CharSequence title )
        {
                mTitle = title;
                return this;
        }

        @Override
		public MenuItem setTitle ( int titleRes )
        {
                return setTitle ( mMenu.getContext ().getString ( titleRes ) );
        }

        @Override
		public CharSequence getTitle ()
        {
                return mTitle;
        }

        @Override
		public MenuItem setTitleCondensed ( CharSequence title )
        {
                mTitleCondensed = title;
                return this;
        }

        @Override
		public CharSequence getTitleCondensed ()
        {
                return mTitleCondensed != null ? mTitleCondensed : mTitle;
        }

        @Override
		public MenuItem setIcon ( Drawable icon )
        {
                mIconResId = 0;
                mIconDrawable = icon;
                return this;
        }

        @Override
		public MenuItem setIcon ( int iconResId )
        {
                mIconDrawable = null;
                mIconResId = iconResId;
                return this;
        }

        @Override
		public Drawable getIcon ()
        {
                if ( mIconDrawable != null )
                {
                        return mIconDrawable;
                }

                if ( mIconResId != 0 )
                {
                        return mMenu.getResources ().getDrawable ( mIconResId );
                }

                return null;
        }

        @Override
		public MenuItem setEnabled ( boolean enabled )
        {
                mEnabled = enabled;
                return this;
        }

        @Override
		public boolean isEnabled ()
        {
                return mEnabled;
        }

        // No-op operations. We use no-ops to allow inflation from menu XML.

        @Override
		public int getGroupId ()
        {
                // Noop
                return 0;
        }

        @Override
		public View getActionView ()
        {
                // Noop
                return null;
        }

        @Override
		public MenuItem setActionProvider ( ActionProvider actionProvider )
        {
                // Noop
                return this;
        }

        @Override
		public ActionProvider getActionProvider ()
        {
                // Noop
                return null;
        }

        @Override
		public boolean expandActionView ()
        {
                // Noop
                return false;
        }

        @Override
		public boolean collapseActionView ()
        {
                // Noop
                return false;
        }

        @Override
		public boolean isActionViewExpanded ()
        {
                // Noop
                return false;
        }

        @Override
        public MenuItem setOnActionExpandListener ( OnActionExpandListener onActionExpandListener )
        {
                // Noop
                return this;
        }

        @Override
		public MenuItem setIntent ( Intent intent )
        {
                // Noop
                return this;
        }

        @Override
		public Intent getIntent ()
        {
                // Noop
                return null;
        }

        @Override
		public MenuItem setShortcut ( char c, char c1 )
        {
                // Noop
                return this;
        }

        @Override
		public MenuItem setNumericShortcut ( char c )
        {
                // Noop
                return this;
        }

        @Override
		public char getNumericShortcut ()
        {
                // Noop
                return 0;
        }

        @Override
		public MenuItem setAlphabeticShortcut ( char c )
        {
                // Noop
                return this;
        }

        @Override
		public char getAlphabeticShortcut ()
        {
                // Noop
                return 0;
        }

        @Override
		public MenuItem setCheckable ( boolean b )
        {
                // Noop
                return this;
        }

        @Override
		public boolean isCheckable ()
        {
                // Noop
                return false;
        }

        @Override
		public MenuItem setChecked ( boolean b )
        {
                // Noop
                return this;
        }

        @Override
		public boolean isChecked ()
        {
                // Noop
                return false;
        }

        @Override
		public MenuItem setVisible ( boolean b )
        {
                // Noop
                return this;
        }

        @Override
		public boolean isVisible ()
        {
                // Noop
                return true;
        }

        @Override
		public boolean hasSubMenu ()
        {
                // Noop
                return false;
        }

        @Override
		public SubMenu getSubMenu ()
        {
                // Noop
                return null;
        }

        @Override
		public MenuItem setOnMenuItemClickListener ( OnMenuItemClickListener onMenuItemClickListener )
        {
                // Noop
                return this;
        }

        @Override
		public ContextMenu.ContextMenuInfo getMenuInfo ()
        {
                // Noop
                return null;
        }

        @Override
		public void setShowAsAction ( int i )
        {
                // Noop
        }

        @Override
		public MenuItem setShowAsActionFlags ( int i )
        {
                // Noop
                return null;
        }

        @Override
		public MenuItem setActionView ( View view )
        {
                // Noop
                return this;
        }

        @Override
		public MenuItem setActionView ( int i )
        {
                // Noop
                return this;
        }
}
