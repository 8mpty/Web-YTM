package com.empty.simplewebytm.CustomViews;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.empty.simplewebytm.Checkers.InjectorChecker;
import com.empty.simplewebytm.R;

public class EmptyWebViewClient extends WebViewClient {
    private final ProgressBar pb;
    private final Context context;

    public EmptyWebViewClient(@NonNull Context context, ProgressBar pb){
        this.context = context;
        this.pb = pb;
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        pb.setVisibility(View.INVISIBLE);
        try {
            if(url.contains(context.getString(R.string.url))){
                Injector(view, context.getAssets());
                Log.e("InjectorMethod", "onPageFinished");
                Toast.makeText(context, "Scripts Loaded", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("InjectorMethod FAILED", "onPageFinished FAILED" + e + "");
            Toast.makeText(context, "Scripts Not Loaded " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void Injector(WebView view, AssetManager am){
        InjectorChecker ic = new InjectorChecker();
        ic.InjectorMethod(view,am);
    }
}
