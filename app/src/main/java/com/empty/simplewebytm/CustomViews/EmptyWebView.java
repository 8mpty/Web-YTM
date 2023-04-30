package com.empty.simplewebytm.CustomViews;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.empty.simplewebytm.JavaInterface;
import com.empty.simplewebytm.PrefsManagers;
import com.empty.simplewebytm.R;
import com.empty.simplewebytm.Services.ForegroundService;

public class EmptyWebView extends WebView {

    private Context context;
    private Intent serviceIntent;
    private PrefsManagers pm;
    public EmptyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public EmptyWebView(Context context) {
        super(context);
    }

    public EmptyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        pm = new PrefsManagers(context);
        initStuff();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public synchronized void initStuff(){
        final JavaInterface JI = new JavaInterface(context);

        String ua = getResources().getString(R.string.ua);

        // WebView //
        this.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        this.setScrollbarFadingEnabled(true);
        this.setLongClickable(true);
        this.clearCache(true);
        this.clearFormData();
        this.clearSslPreferences();
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.setBackgroundColor(0x00000000);
        this.addJavascriptInterface(JI, "JS");


        // WebSettings //
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setSupportZoom(false);
        this.getSettings().setDisplayZoomControls(false);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setMediaPlaybackRequiresUserGesture(false);
        this.getSettings().setUserAgentString(ua);
        this.getSettings().setAllowFileAccess(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setForceDarkAllowed(true);
            this.getSettings().setForceDark(WebSettings.FORCE_DARK_ON);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if(pm.getService()){
            if(visibility == View.VISIBLE){
                startService();
            }
            startService();
        }else if(!pm.getService()){
            stopServiceAll();
        }else super.onWindowVisibilityChanged(View.VISIBLE);
        super.onWindowVisibilityChanged(visibility);
    }

    private void startService(){
        serviceIntent = new Intent(getContext(), ForegroundService.class);
        getContext().startForegroundService(serviceIntent);
    }

    private void stopServiceAll(){
        serviceIntent = new Intent(getContext(), ForegroundService.class);
        getContext().stopService(serviceIntent);
    }
}
