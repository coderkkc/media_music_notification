import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:media_music_notification/media_music_notification.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  MediaMusicNotification mediaMusicNotification;

  @override
  void initState() {
    super.initState();
    mediaMusicNotification = new MediaMusicNotification();
    mediaMusicNotification.onNotificationEvent.listen((event) {
      print("收到event:"+event);
      if(event=="Previous"){
        mediaMusicNotification.play("公子向北走", "陆无双", "https://p1.music.126.net/EeCOXlqwFfuY4vHsS0foKA==/109951163876806880.jpg?param=320y320");
      }else if(event=="Next"){
        mediaMusicNotification.play("吻别", "张学友", "http://p2.music.126.net/636SSPpKW0avAqkK1QgzgQ==/43980465112096.jpg?param=320y320");
      }else{
        print(event);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              RaisedButton(
                child: Text('play'),
                onPressed: (){
//                  mediaMusicNotification.play("","","");
                  mediaMusicNotification.play('火葬场之歌', '番茄酱', 'http://p1.music.126.net/ex1TGAp2wKkhbEhqmImLBg==/18010000463081867.jpg?param=320y320');
                },
              ),RaisedButton(
                child: Text('pause'),
                onPressed: (){
                  mediaMusicNotification.pause();
                },
              ),RaisedButton(
                child: Text('resume'),
                onPressed: (){
                  mediaMusicNotification.resume();
                },
              ),RaisedButton(
                child: Text('pre'),
                onPressed: (){
                  mediaMusicNotification.play("公子向北走", "陆无双", "https://p1.music.126.net/EeCOXlqwFfuY4vHsS0foKA==/109951163876806880.jpg?param=320y320");
                },
              ),RaisedButton(
                child: Text('next'),
                onPressed: (){
                  mediaMusicNotification.play("吻别", "张学友", "http://p2.music.126.net/636SSPpKW0avAqkK1QgzgQ==/43980465112096.jpg?param=320y320");
                },
              ),
            ],
          )
        ),
      ),
    );
  }
}
