package com.empty.simplewebytm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;

import androidx.fragment.app.Fragment;

import com.empty.simplewebytm.CustomViews.EmptyWebChromeClient;
import com.empty.simplewebytm.CustomViews.EmptyWebView;
import com.empty.simplewebytm.CustomViews.EmptyWebViewClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmptyWebView webView;
    private WebSettings webSettings;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public static String url_send;

    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebFragment newInstance(String param1, String param2) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        pref = getContext().getSharedPreferences(PrefsManagers.MAIN_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = pref.edit();

        View view = inflater.inflate(R.layout.fragment_web, container, false);


        String url = getString(R.string.url);

        final EmptyWebViewClient webclient = new EmptyWebViewClient(getContext(),null);
        final EmptyWebChromeClient chromeClient = new EmptyWebChromeClient(getContext(), null);

        //WebView
        webView = view.findViewById(R.id.webview_frag);

        //WebSettings
        webSettings = webView.getSettings();
        IncognitoCheck();

        webView.setWebViewClient(webclient);
        webView.setWebChromeClient(chromeClient);

        webView.setOnLongClickListener(v -> {
            if(pref.getBoolean(PrefsManagers.TOOLBAR, false)) {
                editor.putBoolean(PrefsManagers.TOOLBAR, false).apply();
            }
            return false;
        });

        webView.loadUrl(url);

        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PrefsManagers.INCOGNITO)){
            IncognitoCheck();
        }
    }
    private void IncognitoCheck(){
        boolean incogBool = pref.getBoolean(PrefsManagers.INCOGNITO, false);
        IncognitoMode(incogBool);
    }

    private void IncognitoMode(boolean incognito){
        if(incognito){
            //Make sure No cookies are created
            CookieManager.getInstance().setAcceptCookie(false);

            //Make sure no caching is done
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.clearHistory();
            webView.clearCache(true);

            //Make sure no autofill for Forms/ user-name password happens for the app
            webView.clearFormData();
            editor.putBoolean(PrefsManagers.INCOGNITO,true).apply();
            webView.reload();
        }else{
            // Does not delete old history when changed *(NEW FEATURE LMAO)*
            CookieManager.getInstance().setAcceptCookie(true);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.clearCache(false);
            editor.putBoolean(PrefsManagers.INCOGNITO,false).apply();
            webView.reload();
        }
    }
}