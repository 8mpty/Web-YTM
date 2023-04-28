package com.empty.simplewebytm.Checkers;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionsChecker {

    Context context;

    Activity act;

    String[] permissions = new String[]{
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static boolean post_perm = false;

    public PermissionsChecker(Context context, Activity activity){
        this.context = context;
        this.act = activity;
    }
    public void postPerms(ActivityResultLauncher<String> arl) {
        if (ContextCompat.checkSelfPermission(context, permissions[0])
                != PackageManager.PERMISSION_GRANTED) {
            if (act.shouldShowRequestPermissionRationale(permissions[0])) {
                Log.e(TAG, "Permissions not allowed 1st");
            } else {
                showPostDialog("Needed for foreground service!");
            }
        } else {
            post_perm = true;
        }
        arl.launch(permissions[0]);
    }

    public void showPostDialog(String permission_desc){
        new AlertDialog.Builder(context)
                .setTitle("Foreground Notifications")
                .setMessage(permission_desc)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                    intent.setData(uri);
                    context.startActivity(intent);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();

    }
}
