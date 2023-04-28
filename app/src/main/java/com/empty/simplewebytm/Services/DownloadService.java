package com.empty.simplewebytm.Services;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.empty.simplewebytm.Activities.YTMActivity;
import com.empty.simplewebytm.CustomDialogs;
import com.empty.simplewebytm.R;
import com.yausername.youtubedl_android.BuildConfig;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

public class DownloadService {
    private final Context context;
    private final String processId = "MyDlProcess";
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final String DOWNLOADING_CHANNEL = "download_channel";

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    private float newprogress;
    public DownloadService(Context context) {
        this.context = context;
    }

    public void DownloadCall(String url, String type) {
        String chosen_type;
        File youtubeDLDir = getDownloadLocation();
        YoutubeDLRequest request = new YoutubeDLRequest(url);
        request.addOption("--no-mtime");
        request.addOption("--downloader", "libaria2c.so"); // aria2c
        request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\""); // aria2c

        switch (type) {
            // FFMPEG //
            case "Video":
                // BEST Video ONLY
                chosen_type = "bestvideo[ext=mp4]";
                break;
            case "Video + Audio":
                // BEST VIDEO + AUDIO
                chosen_type = "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best";
                break;
            default:
                // BEST AUDIO ONLY
                chosen_type = "bestaudio[ext=m4a]";
                break;

        }

        request.addOption("-f", chosen_type);
        request.addOption("--no-check-certificate");
        request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s-" + type + ".%(ext)s");

        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, processId, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    builder.setContentTitle("Download Finished")
                            .setContentText(null)
                            .setOngoing(false)
                            .setProgress(100,100,false);
                    notificationManager.notify(2,builder.build());
                    Toast.makeText(context, "Download successful", Toast.LENGTH_LONG).show();
                }, e -> {
                    if (BuildConfig.DEBUG) Log.e(TAG, "Failed to download", e);
                    Log.e(TAG, e + "");
                    Toast.makeText(context, "Download failed. Please Try Again " + e, Toast.LENGTH_LONG).show();
                });
        compositeDisposable.add(disposable);
    }

    private final Function3<Float, Long, String, Unit> callback = new Function3<Float, Long, String, Unit>() {
        @Override
        public Unit invoke(Float progress, Long o2, String line) {
            newprogress = progress;
            final int max = 100;
            createNotificationChannel();
            downloadNoti(max);

            Runnable download = () -> {
                for(newprogress = 0; newprogress <= max; newprogress++) {
                    builder.setProgress(max,(int)newprogress,false);
                    notificationManager.notify(2,builder.build());
                }
            };
            Thread thread = new Thread(download);
            thread.start();

//            activity.runOnUiThread(() -> {
//                for(newprogress = 0; newprogress <= max; newprogress++) {
//                    builder.setProgress(max,(int)newprogress,false);
//                    notificationManager.notify(2,builder.build());
//                }
//            });
            return Unit.INSTANCE;
        }
    };

    private void downloadNoti(int max){

        Intent actIntent = new Intent(context, YTMActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, actIntent, PendingIntent.FLAG_IMMUTABLE);

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context, DOWNLOADING_CHANNEL)
                .setSmallIcon(R.drawable.icon_audio)
                .setContentTitle("Downloading " + CustomDialogs.title)
                .setContentText("Downloading in progress")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSound(null).setOngoing(true).setOnlyAlertOnce(true)
                .setProgress(max, 0, false);
        notificationManager.notify(2,builder.build());
    }

    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, ".Empty");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }

    private void createNotificationChannel() {
        // https://developer.android.com/develop/ui/views/notifications/channels#java //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Downloading_Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DOWNLOADING_CHANNEL, name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
