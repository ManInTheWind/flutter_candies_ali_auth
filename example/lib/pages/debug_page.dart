import 'package:flutter/material.dart';
import 'package:flutter_candies_ali_auth/flutter_candies_ali_auth.dart';
import 'package:flutter_smart_dialog/flutter_smart_dialog.dart';

import '../auth_code/auth_code.dart';

class DebugPage extends StatefulWidget {
  const DebugPage({Key? key}) : super(key: key);

  @override
  State<DebugPage> createState() => _DebugPageState();
}

class _DebugPageState extends State<DebugPage> {
  String? _token;

  AliAuthClient _aliAuthClient = AliAuthClient();

  @override
  void reassemble() {
    super.reassemble();
  }

  final AuthConfig _authConfig = AuthConfig(
    iosSdk:
        "WejeYep8gUJhTydrTYaUo574CJx+UDbMKNU4p0thsNB1Jpy2zbKXNuYgN0di171vjsxp+Ndrgo6hx32UMMA1a82Ga+fyvGxicAg4zYEY0+rp0h6x9VDeD6nkDAxx0T/l6+0e7MA0oye5uBSWV1+pAb3kPrJh5TXMugGtcyRokjpsxsEa7z9wpbNl5cDc9ZoChJytHsKGLRd5jBRefMh6x+J5YqBKq6cAYAbbDujGozhACSWR3qXqd5qH323griA/cmnjYxPWj+4kORa3WZv8gISy2gwEw2Ya8d1ZjDYBSD50LNgza/NMpw==",
    androidSdk:
        "5xBnSnnTAi++0kZy+qrHX7YZC3GwUc5ELFj81fRldIA7cfRRtMZyw41r5grcMlUgkJuDrw+qIZJ3/Wqh7ysZRyV5SD1DTPlMo2P84y9FE5u6UzbUQzPVrsjZ/6tsgi0iHKbWt6bXwudkK7z4IH0qKFEsZXFBZwtWwuV2Z2Z43Rstg6TiYZdhIaqeJUNSlE6vIN88Umr3v/IKon8DJCbBzEP/jwIRxLRWE6Ng2IuniyqhudqhU6BSAahv+WeniD8uixr1a5nY+5mRXtZrFXhwNupDFX8qj9DWq3GBR+1lHwC/QmJS4GYNMfpXcKfQKvTr/KMMn6XkGuShAUv9K7Kcrw7/No1Xz1bd",
    authUIStyle: AuthUIStyle.fullScreen,
    authUIConfig: FullScreenUIConfig(
      navConfig: NavConfig(
        navColor: Colors.cyan.toHex,
      ),
      logoConfig: LogoConfig(
        logoImage: "images/flutter_candies_logo.png",
      ),
      sloganConfig: SloganConfig(sloganText: '欢迎登录FlutterCandies'),
    ),
  );

  /// 登录成功处理
  void _onEvent(dynamic event) async {
    //print("-------------成功分割线-------------");
    final responseModel = AuthResponseModel.fromJson(Map.from(event));
    print("responseModel:$responseModel");
    if (responseModel.resultCode == PNSCodeSuccess &&
        responseModel.token != null) {
      setState(() {
        _token = responseModel.token;
      });
    } else if (responseModel.resultCode == PNSCodeDecodeAppInfoFailed) {
      SmartDialog.showToast(responseModel.msg ?? '未初始化或者初始化失败');
    } else {
      SmartDialog.showToast(
          responseModel.msg ?? '未知错误，code:${responseModel.resultCode}');
    }
  }

  /// 登录错误处理
  void _onError(Object error) {
    print("-------------失败分割线------------");
    print(error);
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _divider('初始化操作'),
            ElevatedButton(
              child: const Text('初始化SDK'),
              onPressed: () async {
                final responseModel = await AliAuthClient.initSdk(
                  authConfig: _authConfig,
                );
                print("onTap:$responseModel");
                if (responseModel.resultCode == PNSCodeSuccess) {
                  SmartDialog.showToast(responseModel.msg ?? '初始化失败');
                } else if (responseModel.resultCode == PNSCodeFailed ||
                    responseModel.resultCode == PNSCodeDecodeAppInfoFailed) {
                  SmartDialog.showToast(responseModel.msg ?? '初始化失败');
                }
              },
            ),
            ElevatedButton(
              child: const Text('检查环境是否支持认证'),
              onPressed: () async {
                final responseModel = await AliAuthClient.checkVerifyEnable();
                print(responseModel);

                if (responseModel.resultCode == PNSCodeSuccess) {
                  SmartDialog.showToast(responseModel.msg ?? '当前环境可以进行一键登录');
                } else if (responseModel.resultCode == PNSCodeFailed) {
                  SmartDialog.showToast(responseModel.msg ?? '当前环境不支持一键登录');
                }
              },
            ),
            ElevatedButton(
              child: const Text('加速一键登录授权页弹起'),
              onPressed: () async {
                final responseModel = await AliAuthClient.accelerateLoginPage();
                print(responseModel);
                if (responseModel.resultCode == PNSCodeSuccess) {
                  SmartDialog.showToast('预取号成功');
                } else if (responseModel.resultCode == PNSCodeFailed) {
                  SmartDialog.showToast(responseModel.msg ?? '预取号失败');
                }
              },
            ),
            ElevatedButton(
              child: const Text('添加授权页面的监听'),
              onPressed: () async {
                SmartDialog.showLoading();
                await AliAuthClient.onListen(_onEvent, onError: _onError);
                SmartDialog.dismiss(status: SmartStatus.loading);
              },
            ),
            _divider('授权页面操作'),
            Container(
              height: 150,
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: GridView.count(
                physics: const NeverScrollableScrollPhysics(),
                crossAxisCount: 3,
                crossAxisSpacing: 20,
                children: [
                  OutlinedButton(
                    child:
                        _iconWithLabel(icon: Icons.phone_android, label: '全屏'),
                    onPressed: () async {
                      final responseModel = await AliAuthClient.loginWithConfig(
                        _authConfig.copyWith(
                          authUIConfig: FullScreenUIConfig(
                            logoConfig:
                                LogoConfig(logoImage: "images/app_icon.png"),
                          ),
                        ),
                      );
                      print(responseModel);
                      SmartDialog.showToast(responseModel.msg ?? '拉起授权页面失败');
                    },
                  ),
                  OutlinedButton(
                    child: _iconWithLabel(
                        icon: Icons.call_to_action_outlined, label: '底部弹窗'),
                    onPressed: () async {
                      final responseModel = await AliAuthClient.loginWithConfig(
                          _authConfig.copyWith(
                        authUIStyle: AuthUIStyle.bottomSheet,
                      ));
                      print(responseModel);

                      if (responseModel.resultCode == PNSCodeFailed) {
                        SmartDialog.showToast(responseModel.msg ?? '拉起授权页面失败');
                      }
                    },
                  ),
                  OutlinedButton(
                    child: _iconWithLabel(icon: Icons.video_label, label: '弹窗'),
                    onPressed: () async {
                      final responseModel = await AliAuthClient.loginWithConfig(
                          _authConfig.copyWith(
                        authUIStyle: AuthUIStyle.alert,
                      ));
                      print(responseModel);

                      if (responseModel.resultCode == PNSCodeFailed) {
                        SmartDialog.showToast(responseModel.msg ?? '拉起授权页面失败');
                      }
                    },
                  ),
                ],
              ),
            ),
            if (_token != null) _tokenWidget()
          ],
        ),
      ),
    );
  }

  Widget _divider(String text) {
    const dividerGap = Flexible(
      flex: 2,
      child: Divider(),
    );
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 20),
      child: Flex(
        direction: Axis.horizontal,
        mainAxisSize: MainAxisSize.min,
        children: [
          dividerGap,
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 15.0),
            child: Text(
              text,
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: Colors.grey.shade700,
                  ),
            ),
          ),
          dividerGap,
        ],
      ),
    );
  }

  Widget _tokenWidget() {
    return Column(
      children: [
        _divider("Token"),
        Text(_token!),
      ],
    );
  }

  Widget _iconWithLabel({required IconData icon, required String label}) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon),
        const SizedBox(height: 8.0),
        Text(
          label,
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
