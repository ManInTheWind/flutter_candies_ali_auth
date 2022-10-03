import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_candies_ali_auth_method_channel.dart';

abstract class FlutterCandiesAliAuthPlatform extends PlatformInterface {
  /// Constructs a FlutterCandiesAliAuthPlatform.
  FlutterCandiesAliAuthPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterCandiesAliAuthPlatform _instance = MethodChannelFlutterCandiesAliAuth();

  /// The default instance of [FlutterCandiesAliAuthPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterCandiesAliAuth].
  static FlutterCandiesAliAuthPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterCandiesAliAuthPlatform] when
  /// they register themselves.
  static set instance(FlutterCandiesAliAuthPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
