package com.empty.simplewebytm.Checkers;

import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

public class InjectorChecker {
    private void scriptLoader(WebView view, AssetManager am, @NonNull String[] file){
        InputStream input;
        try {
            for(int i = 0; i < file.length; i++){
                input = am.open(file[i]);
                byte[] buffer = new byte[input.available()];
                input.read(buffer);
                input.close();

                // String-ify the script byte-array using BASE64 encoding !!!
                String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
                view.loadUrl("javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var script = document.createElement('script');" +
                        "script.type = 'text/javascript';" +
                        // Tell the browser to BASE64-decode the string into your script !!!
                        "script.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(script)" +
                        "})()");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void InjectorMethod(WebView view, AssetManager am) {
        try {
            String[] files = {
                    "scripts/adblocker_yt.js",
                    "scripts/audioonly.js",
                    "scripts/background.js",
                    "scripts/config.js",
                    "scripts/controls.js",
                    "scripts/mediasession.js",
                    "scripts/music_enhancer.js",
                    "scripts/swipe.js",
                    "scripts/ui.js",
                    "scripts/plugin.js",
                    "scripts/get_title.js",
                    "scripts/js.js",

//                    "scripts/test.js",

            };
            scriptLoader(view, am, files);
            Log.e("InjectorARRAY", "LOADED ARRAY");
        } catch (Exception e) {
            Log.e("InjectorARRAY", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
