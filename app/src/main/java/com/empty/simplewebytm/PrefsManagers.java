package com.empty.simplewebytm;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManagers {

    // INITIALIZATION OF SHARED PREFS / CONTEXT //
    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;
    public Context ct;

    // ALL SHARED PREF CONSTANTS //
    public static final String MAIN_SHARED_PREFS = "com.empty.simplewebytm.mainPrefs";
    public static final String SERVICE = "pref_service";
    public static final String TOOLBAR = "pref_toolbar";
    public static final String IMMERSIVE = "pref_immersive";
    public static final String INCOGNITO = "pref_incognito";



    // TESTING PREFS FOR SETTINGS //
    public static final String ADBLOCK = "pref_adblock";
    public static final String ENHANCER = "pref_enhancer";
    public static final String BETTER_UI = "pref_ui";

    public PrefsManagers(Context context) {
        this.ct = context;
        sp = context.getSharedPreferences(MAIN_SHARED_PREFS, MODE_PRIVATE);
        editor = sp.edit();
    }

    // GET METHODS //
    public boolean getService(){
        return sp.getBoolean(SERVICE,false);
    }
    public boolean getToolbar(){
        return sp.getBoolean(TOOLBAR,false);
    }
    public boolean getImmersive(){
        return sp.getBoolean(IMMERSIVE,false);
    }
    public boolean getIncognito(){
        return sp.getBoolean(INCOGNITO,false);
    }



    // SET METHODS //
    public void setService(boolean service){
        editor.putBoolean(SERVICE,service).apply();
    }
    public void setToolbar(boolean toolbar){
        editor.putBoolean(TOOLBAR,toolbar).apply();
    }
    public void setImmersive(boolean immersive){
        editor.putBoolean(IMMERSIVE,immersive).apply();
    }
    public void setIncognito(boolean incognito){
        editor.putBoolean(INCOGNITO,incognito).apply();
    }
}
