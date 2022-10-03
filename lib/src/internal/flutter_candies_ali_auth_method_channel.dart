import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_candies_ali_auth_platform_interface.dart';

/// An implementation of [FlutterCandiesAliAuthPlatform] that uses method channels.
class MethodChannelFlutterCandiesAliAuth extends FlutterCandiesAliAuthPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_candies_ali_auth');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
