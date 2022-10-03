import 'package:flutter_candies_ali_auth/flutter_candies_ali_auth.dart';
import 'package:flutter_candies_ali_auth/src/internal/flutter_candies_ali_auth_method_channel.dart';
import 'package:flutter_candies_ali_auth/src/internal/flutter_candies_ali_auth_platform_interface.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterCandiesAliAuthPlatform
    with MockPlatformInterfaceMixin
    implements FlutterCandiesAliAuthPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterCandiesAliAuthPlatform initialPlatform =
      FlutterCandiesAliAuthPlatform.instance;

  test('$MethodChannelFlutterCandiesAliAuth is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterCandiesAliAuth>());
  });

  test('getPlatformVersion', () async {
    AliAuthClient authClient = AliAuthClient();
    MockFlutterCandiesAliAuthPlatform fakePlatform =
        MockFlutterCandiesAliAuthPlatform();
    FlutterCandiesAliAuthPlatform.instance = fakePlatform;

    expect(await AliAuthClient.getPlatformVersion(), '42');
  });
}
