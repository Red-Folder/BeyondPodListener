package com.red_folder.beyondpodlistener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class ListenerService extends Service implements INotificationListener {
    private static String TAG = "ListenerService";

    private static final int ONGOING_NOTIFICATION_ID = 5567;
    private static final String NOTIFICATION_CHANNEL_ID = "com.red_folder.beyondpodlistener_001";
    private static final String NOTIFICATION_CHANNEL_NAME = "com.red_folder.beyondpodlistener";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    private BroadcastReceiver mReceiver = new BeyondPodReceiver(this);
    private Notification.Builder mBuilder = null;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!AppCenter.isConfigured()) {
            AppCenter.start(getApplication(), BuildConfig.AppCenterSecretKey, Analytics.class, Crashes.class);
        }

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
        notificationManager.createNotificationChannel(notificationChannel);


        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void startForegroundService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("mobi.beyondpod.action.PLAYBACK_STATUS");
        registerReceiver(mReceiver, filter);

        mBuilder.setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setColor(Color.GREEN)
                .setProgress(0, 0, false)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());
    }

    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    private Notification buildNotification(PodModel model) {
        String title = model.getPlaying() ? "Listening" : "Was listening to";
        String text = model.getEpisodeName();
        int color = model.getPlaying() ? Color.RED : Color.GREEN;

        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setColor(color);

        if (model.getPlaying() && model.getEpisodeDuration() > 0) {
            mBuilder.setProgress((int)model.getEpisodeDuration(), (int)model.getEpisodePosition(), false);
        } else {
            mBuilder.setProgress(0, 0, false);
        }

        return mBuilder.build();
    }

    @Override
    public void updateNotification(PodModel model) {
        if (model != null) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(ONGOING_NOTIFICATION_ID, buildNotification(model));
        }
    }
}
