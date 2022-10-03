import 'package:flutter/services.dart';
import 'package:flutter_candies_ali_auth/src/internal/flutter_candies_ali_auth_method_channel.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  MethodChannelFlutterCandiesAliAuth platform =
      MethodChannelFlutterCandiesAliAuth();
  const MethodChannel channel = MethodChannel('flutter_candies_ali_auth');

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
    expect(await platform.getPlatformVersion(), '42');
  });
}
