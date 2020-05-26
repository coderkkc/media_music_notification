package com.chen.media_music_notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NotificationUtil extends BroadcastReceiver {
    private static NotificationManager manager;
    private static androidx.core.app.NotificationCompat.Builder builder;
    private static Context parent;
    private static String title;
    private static String author;
    private static String album;
    private static boolean setOngoing;
    private static boolean isPlay;
    private static Bitmap bitmap;

    public class PlayBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent play_intent = new Intent();
        if (intent.getAction().equals("Next")) {
            play_intent.setAction("Next");
        }
        if (intent.getAction().equals("Previous")) {
            play_intent.setAction("Previous");
        }
        if (intent.getAction().equals("Resume_Pause")) {
            isPlay = !isPlay;
            builder = getBuilder(isPlay);
            builder.setContentTitle(title);
            builder.setContentText(author);
            builder.setLargeIcon(bitmap);
            manager.notify(1, builder.build());
            play_intent.setAction(isPlay? "Resume": "Pause");

        }
        parent.sendBroadcast(play_intent);
    }

    public NotificationUtil(){

    }

    public NotificationUtil(Context context, String title, String author, String album, boolean setOngoing) {
        this.parent = context;
        this.title = title;
        this.author = author;
        this.album = album;
        this.setOngoing = setOngoing;
        PlayBroadcastReceiver playBroadcastReceiver = new PlayBroadcastReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction("Previous");
        filter.addAction("Next");
        filter.addAction("Resume");
        filter.addAction("Pause");
        parent.registerReceiver(playBroadcastReceiver, filter);
        init();
        operate("Play");
    }

    //初始化
    private void init(){
        if(manager==null){
            manager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//关闭震动
                channel = new NotificationChannel("media_notification", "media_notification", NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(false);
                channel.setVibrationPattern(new long[]{0});
                manager.createNotificationChannel(channel);
            }
        }
    }

    //获取Builder
    private NotificationCompat.Builder getBuilder(boolean isPlay){
        MediaSessionCompat mediaSession = new MediaSessionCompat(parent, "media-session");
        builder = new NotificationCompat.Builder(parent, "media_notification");
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setSmallIcon(getResourceId("mipmap/ic_launcher"));

        //listen switch activity
        Intent par = new Intent(parent, parent.getClass());
        par.setAction("Previous");
        PendingIntent intent_parent = PendingIntent.getActivity(parent, 1, par, PendingIntent.FLAG_UPDATE_CURRENT);

        //listen switch pre
        Intent pre = new Intent(parent, NotificationUtil.class);
        pre.setAction("Previous");
        PendingIntent intent_pre = PendingIntent.getBroadcast(parent, 1, pre, PendingIntent.FLAG_UPDATE_CURRENT);

        //listen switch next
        Intent next = new Intent(parent, NotificationUtil.class);
        next.setAction("Next");
        PendingIntent intent_next = PendingIntent.getBroadcast(parent, 1, next, PendingIntent.FLAG_UPDATE_CURRENT);

        //listen switch resume/pause
        Intent resume_pause = new Intent(parent, NotificationUtil.class);
        resume_pause.setAction("Resume_Pause");
        PendingIntent intent_resume_pause = PendingIntent.getBroadcast(parent, 1, resume_pause, PendingIntent.FLAG_UPDATE_CURRENT);

        ArrayList<NotificationCompat.Action> actions = new ArrayList<NotificationCompat.Action>();
        actions.add(new NotificationCompat.Action(R.drawable.ic_action_skip_previous, "Previous", intent_pre));
        if(isPlay){
            builder.setOngoing(true);
            actions.add(new NotificationCompat.Action(R.drawable.ic_action_pause, "play", intent_resume_pause));
        }else{
            builder.setOngoing(setOngoing);
            actions.add(new NotificationCompat.Action(R.drawable.ic_action_play_arrow, "pause", intent_resume_pause));
        }
        actions.add(new NotificationCompat.Action(R.drawable.ic_action_skip_next, "Next", intent_next));

        for(NotificationCompat.Action action : actions){
            builder.addAction(action);
        }
        builder.setStyle(new MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(1, 2)
                .setShowCancelButton(true));
        builder.setContentIntent(intent_parent);
        builder.setVibrate(new long[]{0});
        return builder;
    }

    //operate
    public void operate(String operate){
        if(operate=="Pause"){
            isPlay = false;
        }else{
            isPlay = true;
        }
        builder = getBuilder(isPlay);
        builder.setContentTitle(title);
        builder.setContentText(author);
        setLargeIcon(album);
    }

    //加载专辑图片
    public void setLargeIcon(final String url) {
        if(url==null){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imgUrl = null;
                try {
                    imgUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    builder.setLargeIcon(bitmap);
                    manager.notify(1,builder.build());
                    is.close();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    int getResourceId(String resource) {
        String[] parts = resource.split("/");
        String resourceType = parts[0];
        String resourceName = parts[1];
        return parent.getResources().getIdentifier(resourceName, resourceType, parent.getPackageName());
    }
}
