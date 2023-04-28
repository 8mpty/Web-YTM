package com.empty.simplewebytm.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.empty.simplewebytm.Activities.YTMActivity;
import com.empty.simplewebytm.R;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundService";

    @Override
    public void onCreate() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotiChannel();
        Intent notiIntent = new Intent(this, YTMActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notiIntent, PendingIntent.FLAG_IMMUTABLE);

        String webname = intent.getStringExtra("inputWebName");
        String webLink = intent.getStringExtra("inputWebLink");

        String defName = "Music playing in background";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_audio)
                .setContentTitle(defName)
                .setContentText(webLink)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
//                .addAction(R.mipmap.ic_launcher, "Play", null)
//                .addAction(R.mipmap.ic_launcher, "Stop", null)
                .build();
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    public void createNotiChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "ForegroundService",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            serviceChannel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
