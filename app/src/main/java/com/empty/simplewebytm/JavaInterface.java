package com.empty.simplewebytm;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JavaInterface{
    Context context;
    public JavaInterface(Context context){
        this.context = context;
    }

    @JavascriptInterface
    public void showDebug(String message){
        Log.e("CALLIG FROM " ,message);
    }

    @JavascriptInterface
    public void showTitle(String message){
        CustomDialogs.title = message;
    }
}
