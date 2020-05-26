package com.chen.media_music_notification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** MediaMusicNotificationPlugin */
public class MediaMusicNotificationPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware{
  private MethodChannel channel;
  private static Context context;
  private static PlayerReceiver playerReceiver;
  private static EventChannel.EventSink eventSink;
  private static NotificationUtil notificationUtil;
  private static Activity flutterActivity;


  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    if(flutterActivity==null){//获取activity
      flutterActivity = activityPluginBinding.getActivity();
    }
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    if(context==null){
      context = flutterPluginBinding.getApplicationContext();
    }
    final EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "media_notification_event");
    eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSinks) {
        eventSink = eventSinks;
      }

      @Override
      public void onCancel(Object o) {
        eventSink = null;
      }
    });
    if(playerReceiver==null){
      playerReceiver = new PlayerReceiver();
      IntentFilter filter = new IntentFilter();
      filter.addAction("Previous");
      filter.addAction("Next");
      filter.addAction("Resume");
      filter.addAction("Pause");
      flutterPluginBinding.getApplicationContext().registerReceiver(playerReceiver, filter);
    }
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "media_music_notification");
    channel.setMethodCallHandler(this);
  }

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "media_music_notification");
    channel.setMethodCallHandler(new MediaMusicNotificationPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if(call.method.equals("Play")){
      String title = call.argument("title");
      String author = call.argument("author");
      String album = call.argument("album");
      boolean setOngoing = call.argument("setOngoing");
      notificationUtil = new NotificationUtil(flutterActivity, title, author, album, setOngoing);
    }else if(call.method.equals("Pause")){
      if(notificationUtil!=null){
        notificationUtil.operate("Pause");
      }
    }else if(call.method.equals("Resume")){
      if(notificationUtil!=null){
        notificationUtil.operate("Resume");
      }
    }else{
      result.notImplemented();
    }
  }

  public class PlayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String intentAction = intent.getAction();
      if(eventSink!=null){//通过eventChannel向flutter传回通知栏按钮监听事件
        eventSink.success(intentAction);
      }
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
