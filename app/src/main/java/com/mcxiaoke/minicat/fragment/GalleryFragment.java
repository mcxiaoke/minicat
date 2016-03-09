package com.mcxiaoke.minicat.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.util.IOHelper;
import com.mcxiaoke.minicat.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragment
 * User: mcxiaoke
 * Date: 13-6-5
 * Time: 下午9:56
 */
public class GalleryFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private TextView mTextView;
    private GalleryPagerAdapter mGalleryPagerAdapter;
    private List<String> mImageUris;
    private int mIndex;

    public static GalleryFragment newInstance(ArrayList<String> data, int index) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("data", data);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mImageUris = new ArrayList<String>();
        Bundle args = getArguments();
        ArrayList<String> data = args.getStringArrayList("data");
        mIndex = args.getInt("index");
        if (data != null) {
            mImageUris.addAll(data);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fm_gallery, null);
        mViewPager = (ViewPager) root.findViewById(R.id.gallery);
        mTextView = (TextView) root.findViewById(R.id.text);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGalleryPagerAdapter = new GalleryPagerAdapter(getActivity(), mImageUris);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mGalleryPagerAdapter);
        mViewPager.setCurrentItem(mIndex);
        setPageText(mIndex);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_photo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {

            doSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSave() {
        final String url = mGalleryPagerAdapter.getUrlAt(mViewPager.getCurrentItem());
        if (TextUtils.isEmpty(url)) {
            return;
        }
        File file = ImageLoader.getInstance().getDiscCache().get(url);
        if (file == null || !file.isFile()) {
            return;
        }
        String ext = url.toLowerCase().endsWith(".gif") ? ".gif" : ".jpg";
        final String fileName = "IMG_FANFOU_" + System.currentTimeMillis() + ext;
        File dest = new File(IOHelper.getPictureDir(getActivity()), fileName);
        if (dest.exists() || IOHelper.copyFile(file, dest)) {
            Utils.mediaScan(getActivity(), Uri.fromFile(dest));
            Utils.notifyLong(getActivity(), "图片已保存到存储卡的 Pictures 目录");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
    }

    private void setPageText(int page) {
        mTextView.setText("" + (page + 1) + " / " + mImageUris.size());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setPageText(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    static class GalleryPagerAdapter extends PagerAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<String> mResources;

        public GalleryPagerAdapter(Context context, List<String> resources) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mResources = resources;
        }

        public String getUrlAt(int position) {
            if (position < 0 || position >= getCount()) {
                return null;
            }
            return mResources.get(position);
        }

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.gallery_item_photo, container, false);
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.photo);
            final GifImageView gifImageView = (GifImageView) view.findViewById(R.id.gif);
            View vEmpty = view.findViewById(android.R.id.empty);
            final EmptyViewController emptyViewController = new EmptyViewController(vEmpty);
            ImageLoader.getInstance().loadImage(mResources.get(position), getDisplayImageOptions(),
                    new ImageLoaderCallback(imageView, gifImageView, emptyViewController));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        private DisplayImageOptions getDisplayImageOptions() {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            builder.cacheInMemory(true).cacheOnDisc(true);
            builder.bitmapConfig(Bitmap.Config.RGB_565);
            builder.showImageOnFail(R.drawable.photo_error);
            builder.showImageOnLoading(R.drawable.photo_loading);
            builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
            return builder.build();
        }

        private class ImageLoaderCallback extends SimpleImageLoadingListener {
            private PhotoView imageView;
            private GifImageView gifImageView;
            private EmptyViewController emptyViewController;

            public ImageLoaderCallback(PhotoView imageView, GifImageView gifImageView,
                                       EmptyViewController emptyViewController) {
                this.imageView = imageView;
                this.gifImageView = gifImageView;
                this.emptyViewController = emptyViewController;

            }

            private void showProgress() {
                imageView.setVisibility(View.GONE);
                gifImageView.setVisibility(View.GONE);
                emptyViewController.showProgress();
            }

            private void showEmptyText(String text) {
                imageView.setVisibility(View.GONE);
                gifImageView.setVisibility(View.GONE);
                emptyViewController.showEmpty(text);
            }

            private void showContent(String imageUri, Bitmap bitmap) {
                emptyViewController.hideProgress();
                if (bitmap == null) {
                    return;
                }
                if (imageUri.endsWith(".gif")) {
                    imageView.setVisibility(View.GONE);
                    gifImageView.setVisibility(View.VISIBLE);
                    try {
                        final File file = ImageLoader.getInstance().getDiscCache().get(imageUri);
                        final GifDrawable drawable = new GifDrawable(file);
                        gifImageView.setImageDrawable(drawable);
                    } catch (IOException e) {
                        showEmptyText("IOException");
                    }
                } else {
                    gifImageView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                showProgress();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                showEmptyText(failReason.getType().toString());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                showContent(imageUri, loadedImage);
            }
        }
    }


}
