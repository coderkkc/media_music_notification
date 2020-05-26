import 'dart:async';

import 'package:flutter/services.dart';

class MediaMusicNotification {
  static const MethodChannel _channel = const MethodChannel('media_music_notification');
  static const EventChannel _eventChannel = const EventChannel("media_notification_event");
  static final StreamController _eventController = StreamController.broadcast();


  MediaMusicNotification(){
    _eventChannel.receiveBroadcastStream().listen((data) {
      _eventController.add(data);
    });
  }

  /*event*/
  Stream get onNotificationEvent => _eventController.stream;

  /*Play*/
  void play(String title, String author, String album){
    _channel.invokeMethod('Play',
        <String,dynamic>{
          'title': title,
          'author': author,
          'album': album
        }
    );
  }

  /*Pause*/
  void pause(){
    _channel.invokeMethod('Pause');
  }

  /*Resume*/
  void resume(){
     _channel.invokeMethod('Resume');
  }
}
