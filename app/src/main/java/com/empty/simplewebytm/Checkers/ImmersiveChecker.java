package com.empty.simplewebytm.Checkers;

import android.app.Activity;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class ImmersiveChecker {
    WindowInsetsControllerCompat wicc;
    public void hideSystemBars(Activity act){
        wicc = WindowCompat.getInsetsController(act.getWindow(), act.getWindow().getDecorView());

        wicc.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        wicc.hide(WindowInsetsCompat.Type.systemBars());
    }

    public void showSystemBars(Activity act){
        wicc = WindowCompat.getInsetsController(act.getWindow(), act.getWindow().getDecorView());

        wicc.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        wicc.show(WindowInsetsCompat.Type.systemBars());
    }
}
