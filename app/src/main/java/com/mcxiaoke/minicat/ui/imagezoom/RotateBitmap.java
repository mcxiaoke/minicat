package com.mcxiaoke.minicat.ui.imagezoom;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

public class RotateBitmap {

    public static final String TAG = "RotateBitmap";
    private Bitmap mBitmap;
    private int mRotation;
    private int mWidth;
    private int mHeight;
    private int mBitmapWidth;
    private int mBitmapHeight;

    public RotateBitmap(Bitmap bitmap, int rotation) {
        mRotation = rotation % 360;
        setBitmap(bitmap);
    }

    public int getRotation() {
        return mRotation % 360;
    }

    public void setRotation(int rotation) {
        mRotation = rotation;
        invalidate();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;

        if (mBitmap != null) {
            mBitmapWidth = bitmap.getWidth();
            mBitmapHeight = bitmap.getHeight();
            invalidate();
        }
    }

    private void invalidate() {
        Matrix matrix = new Matrix();
        int cx = mBitmapWidth / 2;
        int cy = mBitmapHeight / 2;
        matrix.preTranslate(-cx, -cy);
        matrix.postRotate(mRotation);
        matrix.postTranslate(cx, cx);

        RectF rect = new RectF(0, 0, mBitmapWidth, mBitmapHeight);
        matrix.mapRect(rect);
        mWidth = (int) rect.width();
        mHeight = (int) rect.height();
    }

    public Matrix getRotateMatrix() {
        Matrix matrix = new Matrix();
        if (mRotation != 0) {
            int cx = mBitmapWidth / 2;
            int cy = mBitmapHeight / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(mRotation);
            matrix.postTranslate(mWidth / 2, mHeight / 2);
        }

        return matrix;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
