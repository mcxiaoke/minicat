package com.mcxiaoke.minicat.controller;

import android.view.View;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;

/**
 * @author mcxiaoke
 * @version 1.0 2012.03.06
 */
public class EmptyViewController {

//	@InjectView(android.R.id.empty) View root;
//	@InjectView(R.id.empty_text) TextView emptyText;
//	@InjectView(R.id.empty_progress) View progressView;
//	@InjectView(R.id.empty_progress_bar) ProgressBar progressBar;
//	@InjectView(R.id.empty_progress_text) TextView progressText;

    private View root;
    private TextView emptyTextView;
    private View progressView;
    private TextView progressTextView;

    public EmptyViewController(View empty) {
        this.root = empty;
        init();
    }

    private void init() {
        emptyTextView = (TextView) root.findViewById(R.id.empty_text);
        progressView = root.findViewById(R.id.empty_progress);
        progressTextView = (TextView) root.findViewById(R.id.empty_progress_text);
    }

    public void showProgress() {
        root.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    public void showProgress(String text) {
        progressTextView.setText(text);
        showProgress();
    }

    public void showEmpty() {
        root.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    public void showEmpty(String text) {
        emptyTextView.setText(text);
        showEmpty();
    }

    public void hideProgress() {
        root.setVisibility(View.GONE);
    }

}
