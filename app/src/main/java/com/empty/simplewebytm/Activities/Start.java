package com.empty.simplewebytm.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.empty.simplewebytm.Checkers.ImmersiveChecker;
import com.empty.simplewebytm.R;

import java.util.Random;

public class Start extends AppCompatActivity {
    private static final int milliSeconds = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Imm();
        HideStatusBar();
        Run();
    }

    private void Run(){
        // Testing Random //
        int min  = 1;
        int max = 2;
        Random random = new Random();
        final int set = random.nextInt((max - min) + 1) + min;

        int total = set * milliSeconds;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), YTMActivity.class);
            startActivity(intent);
            finish();
        }, total);
    }

    private void HideStatusBar(){
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    private void Imm(){
        ImmersiveChecker ic = new ImmersiveChecker();
        ic.hideSystemBars(Start.this);
    }
}