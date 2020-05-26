import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:media_music_notification/media_music_notification.dart';

void main() {
  const MethodChannel channel = MethodChannel('media_music_notification');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MediaMusicNotification.platformVersion, '42');
  });
}
