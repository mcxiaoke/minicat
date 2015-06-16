package com.mcxiaoke.minicat.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import uk.co.senab.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragment
 * User: mcxiaoke
 * Date: 13-6-5
 * Time: 下午9:56
 */
public class GalleryFragment extends AbstractFragment implements ViewPager.OnPageChangeListener {

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
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void startRefresh() {

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

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            PhotoView view = new PhotoView(container.getContext());
//            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            ImageLoader.getInstance().displayImage(mResources.get(position), view);

            ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.gallery_item_photo, null);
            final PhotoView imageView = (PhotoView) view.findViewById(R.id.photo);
            View vEmpty = view.findViewById(android.R.id.empty);
            final EmptyViewController emptyViewController = new EmptyViewController(vEmpty);
            ImageViewAware aware = new ImageViewAware(imageView, false);
            ImageLoader.getInstance().loadImage(mResources.get(position), getDisplayImageOptions(),
                    new ImageLoaderCallback(imageView, emptyViewController));
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
            private EmptyViewController emptyViewController;

            public ImageLoaderCallback(PhotoView imageView, EmptyViewController emptyViewController) {
                this.imageView = imageView;
                this.emptyViewController = emptyViewController;

            }

            private void showProgress() {
                imageView.setVisibility(View.GONE);
                emptyViewController.showProgress();
            }

            private void showEmptyText(String text) {
                imageView.setVisibility(View.GONE);
                emptyViewController.showEmpty(text);
            }

            private void showContent(Bitmap bitmap) {
                emptyViewController.hideProgress();
                imageView.setVisibility(View.VISIBLE);
                if (bitmap != null) {
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
                showContent(loadedImage);
            }
        }
    }


}
