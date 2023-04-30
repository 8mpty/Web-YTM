package com.empty.simplewebytm.CustomViews;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class EmptyWebChromeClient extends WebChromeClient {
    private final Context context;
    private ProgressBar pb;

    public EmptyWebChromeClient(Context context, ProgressBar pb) {
        this.context = context;
        this.pb = pb;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        pb.setProgress(newProgress);
    }
}
