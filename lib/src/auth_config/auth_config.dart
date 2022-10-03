import 'auth_ui_config.dart';
import 'part_ui_config.dart';

class AuthConfig {
  final String iosSdk;
  final String androidSdk;
  final AuthUIStyle authUIStyle;
  final AuthUIConfig? authUIConfig;

  const AuthConfig({
    required this.iosSdk,
    required this.androidSdk,
    this.authUIStyle = AuthUIStyle.fullScreen,
    this.authUIConfig,
  });

  MapWithStringKey toJson() {
    final Map<String, dynamic> json = <String, dynamic>{};
    json['iosSdk'] = iosSdk;
    json['androidSdk'] = androidSdk;
    json['authUIStyle'] = authUIStyle.index;
    if (authUIConfig != null) {
      json.addAll(authUIConfig!.toJson());
    }
    return json;
  }

  AuthConfig copyWith({
    String? iosSdk,
    String? androidSdk,
    AuthUIStyle? authUIStyle,
    AuthUIConfig? authUIConfig,
  }) {
    return AuthConfig(
      iosSdk: iosSdk ?? this.iosSdk,
      androidSdk: androidSdk ?? this.androidSdk,
      authUIStyle: authUIStyle ?? this.authUIStyle,
      authUIConfig: authUIConfig ?? this.authUIConfig,
    );
  }
}
