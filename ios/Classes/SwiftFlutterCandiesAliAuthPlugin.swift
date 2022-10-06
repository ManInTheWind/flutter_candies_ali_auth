import ATAuthSDK
import Flutter
import MBProgressHUD
import UIKit

public class SwiftFlutterCandiesAliAuthPlugin: NSObject, FlutterPlugin {
    var _eventSink: FlutterEventSink?

    let authUIBuilder: AuthUIBuilder = .init()

    var _authConfig: AuthConfig?

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_ali_auth", binaryMessenger: registrar.messenger())

        let eventChannel = FlutterEventChannel(name: "login_event", binaryMessenger: registrar.messenger())

        let instance = SwiftFlutterCandiesAliAuthPlugin()

        // method channel
        registrar.addMethodCallDelegate(instance, channel: channel)
        // event channel
        eventChannel.setStreamHandler(instance)
        // giving access to the plugin registrar and since it's static we can access variables from outer scope:
        instance.authUIBuilder.register = registrar
        // 验证网络是否可用
        instance.httpAuthority()
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "init":
            initSdk(arguments: call.arguments, result: result)
        case "login":
            let timeout: TimeInterval = (call.arguments as? TimeInterval) ?? 5.0
            login(timeout: timeout, result: result)
        case "checkEnv":
            checkEnvAvailable(result: result)
        case "accelerateLoginPage":
            accelerateLoginPage(result)
        case "loginWithConfig":
            login(arguments: call.arguments, result: result)
        case "cancelListen":
            cancelListenLoginEvent(result: result)
        case "getPlatformVersion":
            result("iOS " + UIDevice.current.systemVersion)
        case "getAliAuthVersion":
            getVersion(result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    // MARK: - 检查联网

    public func httpAuthority() {
        if let testUrl = URL(string: "https://www.baidu.com") {
            let urlRequest = URLRequest(url: testUrl)
            let config = URLSessionConfiguration.default
            config.httpAdditionalHeaders = ["Content-type": "application/json"]
            config.timeoutIntervalForRequest = 20
            config.requestCachePolicy = .reloadIgnoringLocalCacheData
            let session = URLSession(configuration: config)
            session.dataTask(with: urlRequest) { _, _, error in
                if error == nil {
                    print("联网成功")
                } else {
                    print("联网失败")
                }
            }.resume()
        } else {
            // testUrl == nil
            print("联网失败")
            return
        }
    }

    // MARK: - 获取SDK版本号（getVersion）

    public func getVersion(result: @escaping FlutterResult) {
        let version = TXCommonHandler.sharedInstance().getVersion()

        result("阿里云一键登录版本:\(version)")
    }

    // MARK: - 设置SDK密钥（setAuthSDKInfo）

    public func initSdk(arguments: Any?, result: @escaping FlutterResult) {
        var responseMoedel: ResponseModel

        TXCommonHandler.sharedInstance().getReporter().setConsolePrintLoggerEnable(true)

        guard let params = arguments as? [String: Any] else {
            // 参数有问题
            responseMoedel = ResponseModel(code: PNSCodeFailed, msg: "初始化失败，AuthConfig不能为空")
            // onSend(responseMoedel)
            result(responseMoedel.json)
            return
        }
        guard let sdk = params["iosSdk"] as? String else {
            // 没有sdk
            responseMoedel = ResponseModel(code: PNSCodeFailed, msg: "初始化失败，sdk为空")
            result(responseMoedel.json)
            return
        }

        // 设置参数
        _authConfig = AuthConfig(params: params)

        TXCommonHandler.sharedInstance().setAuthSDKInfo(sdk) { resultDict in
            guard let dict = resultDict as? [String: Any] else {
                let _responseMoedel = ResponseModel(resultDict)
                // self.onSend(_responseMoedel)
                result(_responseMoedel.json)
                return
            }
            guard let code = dict["resultCode"] as? String else {
                // self.onSend(ResponseModel(dict))
                result(ResponseModel(dict).json)
                return
            }

            if code == PNSCodeSuccess {
                // 初始化成功：{msg: AppID、Appkey解析成功, resultCode: 600000, requestId: 481a2c9b50264cf3}
                self.checkEnvAvailable(result: result)
            } else {
                // 初始化失败
                self.onSend(ResponseModel(dict))
            }
        }
    }

    // MARK: - 检查认证环境（checkEnvAvailableWithComplete）

    public func checkEnvAvailable(result: @escaping FlutterResult) {
        TXCommonHandler.sharedInstance().checkEnvAvailable(with: PNSAuthType.loginToken) { (resultDict: [AnyHashable: Any]?) in
            guard let dict = resultDict as? [String: Any] else {
                result(ResponseModel(resultDict).json)
                return
            }
            guard let code = dict["resultCode"] as? String else {
                result(ResponseModel(dict).json)
                return
            }
            /// 当前环境可以进行验证：{resultCode: 600000, requestId: afa404b4fba34103, msg: }
            if code == PNSCodeSuccess {
                let response = ResponseModel(code: PNSCodeSuccess, msg: "当前环境可以进行一键登录")
                result(response.json)
                // 开始一键登录预取号
                self.accelerateLoginPage(nil)
            } else {
                result(ResponseModel(dict).json)
            }
        }
    }

    // MARK: - 一键登录预取号（accelerateLoginPageWithTimeout）

    public func accelerateLoginPage(_ result: FlutterResult?) {
        print("开始一键登录预取号")
        TXCommonHandler.sharedInstance().accelerateLoginPage(withTimeout: 5.0) { resultDict in
            guard let dict = resultDict as? [String: Any] else {
                if result != nil {
                    result!(ResponseModel(resultDict).json)
                }

                return
            }
            guard let code = dict["resultCode"] as? String else {
                if result != nil {
                    result!(ResponseModel(dict).json)
                }

                return
            }
            /// 加速成功：{msg: , carrierFailedResultData: {}, requestId: 143bd2c7e5044e86, innerCode: , resultCode: 600000, innerMsg: }
            if code == PNSCodeSuccess {
                if result != nil {
                    result!(ResponseModel(dict).json)
                }

            } else {
                if result != nil {
                    result!(ResponseModel(dict).json)
                }
            }
        }
    }

    // MARK: - 一键登录获取Token（getLoginTokenWithTimeout）

    public func login(timeout: TimeInterval, result: @escaping FlutterResult) {
        assert(_authConfig != nil, "AuthConfig不能为空")

        if _eventSink == nil {
            let responseMoedel = ResponseModel(code: PNSCodeFailed, msg: "初始化失败，请先对插件进行监听")
            result(responseMoedel.json)
            return
        }

        let model = authUIBuilder.buildUIModel(authUIStyle: _authConfig!.authUIStyle, authUIConfig: _authConfig!.authUIConfig)

        guard let viewController = getCurrentViewController() else {
            let responseModel = ResponseModel(code: PNSCodeLoginControllerPresentFailed, msg: "拉起授权页面失败,无法找到当前视图")
            onSend(responseModel)
            return
        }

        TXCommonHandler.sharedInstance().checkEnvAvailable(with: PNSAuthType.loginToken) { (resultDict: [AnyHashable: Any]?) in
            guard let dict = resultDict as? [String: Any] else {
                let _responseMoedel = ResponseModel(resultDict)
                self.onSend(_responseMoedel)
                return
            }
            guard let code = dict["resultCode"] as? String else {
                self.onSend(ResponseModel(dict))
                return
            }
            /// 当前环境可以进行验证：{resultCode: 600000, requestId: afa404b4fba34103, msg: }
            if code != PNSCodeSuccess {
                self.onSend(ResponseModel(dict))
                return
            }
            // print("开始一键登录预取号")
            TXCommonHandler.sharedInstance().accelerateLoginPage(withTimeout: 5.0) { resultDict in
                guard let dict = resultDict as? [String: Any] else {
                    let _responseMoedel = ResponseModel(resultDict)
                    self.onSend(_responseMoedel)
                    return
                }
                guard let code = dict["resultCode"] as? String else {
                    self.onSend(ResponseModel(dict))
                    return
                }
                /// 加速成功：{msg: , carrierFailedResultData: {}, requestId: 143bd2c7e5044e86, innerCode: , resultCode: 600000, innerMsg: }
                if code != PNSCodeSuccess {
                    self.onSend(ResponseModel(dict))
                    return
                }
                // print("拉起授权页面")
                TXCommonHandler.sharedInstance().getLoginToken(withTimeout: timeout, controller: viewController, model: model) { resultDict in
                    var _responseModel: ResponseModel
                    guard let dict = resultDict as? [String: Any] else {
                        _responseModel = ResponseModel(resultDict)
                        self.onSend(_responseModel)
                        return
                    }
                    guard let resultCode = dict["resultCode"] as? String else {
                        _responseModel = ResponseModel(resultDict)
                        self.onSend(_responseModel)
                        return
                    }

                    if resultCode == PNSCodeLoginControllerClickLoginBtn {
                        print("用户点击一键登录：\(dict)")
                        var isChecked = 0
                        if let checked = dict["isChecked"] as? Int {
                            isChecked = checked
                        }
                        if isChecked == 0 {
                            guard let currentViewController = self.getCurrentViewController() else {
                                return
                            }
                            let hud = MBProgressHUD.showAdded(to: currentViewController.view, animated: true)
                            hud.mode = MBProgressHUDMode.text
                            hud.offset.y = 180
                            hud.label.text = "请先同意相关开发协议哦"
                            hud.hide(animated: true, afterDelay: 2) // 2秒钟后自动隐藏
                        }
                    }
                    let shouldCancelLoginVC: Bool =
                        resultCode == PNSCodeSuccess || resultCode == PNSCodeLoginControllerClickCancel ||
                        resultCode == PNSCodeLoginControllerClickChangeBtn
                    if shouldCancelLoginVC {
                        TXCommonHandler.sharedInstance().cancelLoginVC(animated: true, complete: nil)
                    }
                    // 发送消息到flutter，Flutter侧边自行判断
                    self.onSend(ResponseModel(dict))
                }
            }
        }
    }

    // MARK: - 一键登陆，debug专用, 用新的配置去验证，耗时会比较久

    func login(arguments: Any?, result: @escaping FlutterResult) {
        if _eventSink == nil {
            let responseMoedel = ResponseModel(code: PNSCodeFailed, msg: "拉起授权页面之前请对页面进行监听")
            result(responseMoedel.json)
            return
        }

        var _config: AuthConfig?

        if let rawConfig = arguments as? [String: Any] {
            _config = AuthConfig(params: rawConfig)
        }

        _config = _config ?? _authConfig

        let model: TXCustomModel = authUIBuilder.buildUIModel(authUIStyle: _config!.authUIStyle, authUIConfig: _config!.authUIConfig)

        guard let viewController = getCurrentViewController() else {
            let responseModel = ResponseModel(code: PNSCodeLoginControllerPresentFailed, msg: "拉起授权页面失败,无法找到当前视图")
            onSend(responseModel)
            return
        }

        TXCommonHandler.sharedInstance().checkEnvAvailable(with: PNSAuthType.loginToken) { (resultDict: [AnyHashable: Any]?) in
            guard let dict = resultDict as? [String: Any] else {
                let _responseMoedel = ResponseModel(resultDict)
                self.onSend(_responseMoedel)
                return
            }
            guard let code = dict["resultCode"] as? String else {
                self.onSend(ResponseModel(dict))
                return
            }
            /// 当前环境可以进行验证：{resultCode: 600000, requestId: afa404b4fba34103, msg: }
            if code != PNSCodeSuccess {
                self.onSend(ResponseModel(dict))
                return
            }
            print("开始一键登录预取号")
            TXCommonHandler.sharedInstance().accelerateLoginPage(withTimeout: 5.0) { resultDict in
                guard let dict = resultDict as? [String: Any] else {
                    let _responseMoedel = ResponseModel(resultDict)
                    self.onSend(_responseMoedel)
                    return
                }
                guard let code = dict["resultCode"] as? String else {
                    self.onSend(ResponseModel(dict))
                    return
                }
                /// 加速成功：{msg: , carrierFailedResultData: {}, requestId: 143bd2c7e5044e86, innerCode: , resultCode: 600000, innerMsg: }
                if code != PNSCodeSuccess {
                    self.onSend(ResponseModel(dict))
                    return
                }

                print("拉起授权页面")
                TXCommonHandler.sharedInstance().getLoginToken(withTimeout: 3.0, controller: viewController, model: model) { resultDict in
                    var _responseModel: ResponseModel
                    guard let dict = resultDict as? [String: Any] else {
                        _responseModel = ResponseModel(resultDict)
                        self.onSend(_responseModel)
                        return
                    }
                    guard let resultCode = dict["resultCode"] as? String else {
                        _responseModel = ResponseModel(resultDict)
                        self.onSend(_responseModel)
                        return
                    }

                    if resultCode == PNSCodeLoginControllerClickLoginBtn {
                        print("用户点击一键登录：\(dict)")
                        var isChecked = 0
                        if let checked = dict["isChecked"] as? Int {
                            isChecked = checked
                        }
                        if isChecked == 0 {
                            print("MBProgressHUD")
                            guard let currentViewController = self.getCurrentViewController() else {
                                return
                            }
                            let hud = MBProgressHUD.showAdded(to: currentViewController.view, animated: true)
                            hud.mode = MBProgressHUDMode.text
                            hud.offset.y = 200
                            hud.label.text = "请先同意相关开发协议哦"
                            hud.hide(animated: true, afterDelay: 3) // 2秒钟后自动隐藏
                        }
                    }
                    let shouldCancelLoginVC: Bool =
                        resultCode == PNSCodeSuccess || resultCode == PNSCodeLoginControllerClickCancel ||
                        resultCode == PNSCodeLoginControllerClickChangeBtn
                    if shouldCancelLoginVC {
                        TXCommonHandler.sharedInstance().cancelLoginVC(animated: true, complete: nil)
                    }
                    // 发送消息到flutter，Flutter侧边自行判断
                    self.onSend(ResponseModel(dict))
                }
            }
        }
    }

    public func cancelListenLoginEvent(result: @escaping FlutterResult) {
        let flutterError: FlutterError? = onCancel(withArguments: nil)
        result(flutterError)
    }
}