package com.empty.simplewebytm.Activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.empty.simplewebytm.Checkers.ImmersiveChecker;
import com.empty.simplewebytm.Checkers.PermissionsChecker;
import com.empty.simplewebytm.CustomDialogs;
import com.empty.simplewebytm.CustomViews.EmptyWebChromeClient;
import com.empty.simplewebytm.CustomViews.EmptyWebView;
import com.empty.simplewebytm.CustomViews.EmptyWebViewClient;
import com.empty.simplewebytm.PrefsManagers;
import com.empty.simplewebytm.R;
import com.empty.simplewebytm.Services.DownloadService;
import com.yausername.aria2c.Aria2c;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import java.util.Objects;

public class YTMActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView txtView;
    private EmptyWebView webView;
    private ProgressBar pb;
    private static ConstraintLayout tb_lay;
    private PrefsManagers pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_main);
        init_all();
    }

    private void init_all(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        pb = findViewById(R.id.progressbar);
        txtView = findViewById(R.id.text);
        tb_lay = findViewById(R.id.topbar);
        ConstraintLayout bottom_lay = findViewById(R.id.bottom_lay);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        pm = new PrefsManagers(this);

        pb.getProgressDrawable()
                .setColorFilter(Color.rgb(255,255,255),
                        PorterDuff.Mode.SRC_IN);

        ToolbarCheck();
        ImmersiveCheck();
        webActivities();

        try {
            YoutubeDL.getInstance().init(this);
            FFmpeg.getInstance().init(this);
            Aria2c.getInstance().init(this);
        } catch (YoutubeDLException e) {
            Log.e(TAG, "failed to initialize youtubedl-android", e);
        }
        bottom_lay.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu != null){
            if(pm.getService()){
                menu.findItem(R.id.service).setChecked(true);
            }

            if(pm.getImmersive()) {
                menu.findItem(R.id.immersive).setChecked(true);
            }

            if(pm.getIncognito()) {
                menu.findItem(R.id.incognito).setChecked(true);
            }

            if(!webView.getUrl().contains("https://music.youtube.com/")){
                menu.findItem(R.id.share).setEnabled(false);
                menu.findItem(R.id.download).setEnabled(false);
            }

        } return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        CustomDialogs cd = new CustomDialogs(this);
        PermissionsChecker pc = new PermissionsChecker(this, this);

        switch (item.getItemId()) {

            case R.id.refresh:
                webView.reload();
                return true;

            case R.id.tb:
                cd.HideToolbar();
                return true;

            case R.id.service:
                item.setChecked(!item.isChecked());
                pc.postPerms(requestLauncher);
                pm.setService(item.isChecked());
                return true;

            case R.id.immersive:
                item.setChecked(!item.isChecked());
                pm.setImmersive(item.isChecked());
                return true;

            case R.id.incognito:
                item.setChecked(!item.isChecked());
                pm.setIncognito(item.isChecked());
                webView.reload();
                return true;

            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(intent,"Share via"));
                return true;

            case R.id.download:
                String url = webView.getUrl();
                cd.DownloadAudio(url);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final ActivityResultLauncher<String> requestLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> PermissionsChecker.post_perm = isGranted);

    @SuppressLint("SetJavaScriptEnabled")
    private void webActivities(){
        String url = getString(R.string.url);

        final EmptyWebViewClient webclient = new EmptyWebViewClient(this, pb);
        final EmptyWebChromeClient chromeClient = new EmptyWebChromeClient(this,pb);

        //WebView
        webView = findViewById(R.id.webview);

        IncognitoCheck();

        webView.setWebViewClient(webclient);
        webView.setWebChromeClient(chromeClient);

        webView.setOnLongClickListener(v -> {
            if(tb_lay.getVisibility() != View.VISIBLE){
                tb_lay.setVisibility(View.VISIBLE);
                pm.setToolbar(false);
            }
            return false;
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String sharedLink = intent.getStringExtra(Intent.EXTRA_TEXT);

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if("text/plain".equals(type) && sharedLink.contains(url)) {
                webView.loadUrl(sharedLink);
            }
            else {
                txtView.setText(getString(R.string.errorText));
                webView.loadUrl("file:///android_asset/html/error.html");
            }
        }else{
            webView.loadUrl(url);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PrefsManagers.IMMERSIVE:
                ImmersiveCheck();
                break;
            case PrefsManagers.TOOLBAR:
                ToolbarCheck();
                break;
            case PrefsManagers.INCOGNITO:
                IncognitoCheck();
                break;
        }
    }

    private void ToolbarCheck(){
        if(pm.getToolbar()){
            tb_lay.setVisibility(View.GONE);
        }else{
            tb_lay.setVisibility(View.VISIBLE);
        }
    }

    private void ImmersiveCheck(){
        ImmersiveChecker ims = new ImmersiveChecker();
        if(pm.getImmersive()){
            ims.hideSystemBars(YTMActivity.this);
        }else{
            ims.showSystemBars(YTMActivity.this);
        }
    }

    private void IncognitoCheck(){
        IncognitoMode(pm.getIncognito());
    }

    private void IncognitoMode(boolean incognito){
        if(incognito){
            CookieManager.getInstance().setAcceptCookie(false);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.clearHistory();
            webView.clearCache(true);
            webView.clearFormData();
            pm.setIncognito(true);
            txtView.setText(getString(R.string.incognitoText));
        }else{
            CookieManager.getInstance().setAcceptCookie(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.clearCache(false);
            pm.setIncognito(false);
            txtView.setText(getString(R.string.normalText));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
        outState.clear();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences(PrefsManagers.MAIN_SHARED_PREFS, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSharedPreferences(PrefsManagers.MAIN_SHARED_PREFS, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webView != null){
            webView.destroy();
        }else {
            DownloadService ds = new DownloadService(this);
            ds.compositeDisposable.dispose();
            super.onDestroy();
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        if(webView != null){
            webView.destroy();
        }
        return super.onSupportNavigateUp();
    }
}