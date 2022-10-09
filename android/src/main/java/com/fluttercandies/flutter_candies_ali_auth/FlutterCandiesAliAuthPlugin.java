package com.fluttercandies.flutter_candies_ali_auth;

import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.failedListeningMsg;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.fluttercandies.flutter_candies_ali_auth.model.AuthModel;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;

import java.lang.ref.WeakReference;
import java.util.Objects;

/** FlutterCandiesAliAuthPlugin */
public class FlutterCandiesAliAuthPlugin extends FlutterActivity  implements FlutterPlugin,
        MethodCallHandler, ActivityAware, EventChannel.StreamHandler {

  public static final String TAG = FlutterCandiesAliAuthPlugin.class.getSimpleName();


  private AuthClient authClient;

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    Log.i(TAG,"onAttachedToEngine");

    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_ali_auth");

    authClient = new AuthClient();

    EventChannel auth_event = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "auth_event");

    auth_event.setStreamHandler(this);

    channel.setMethodCallHandler(this);

    authClient.setContext(flutterPluginBinding.getApplicationContext());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    Log.w(TAG, "call.method：" + call.method);
    if(call.method.equals("getPlatformVersion")){
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      return;
    }else if (call.method.equals("getAliAuthVersion")){
      getAliAuthVersion(result);
      return;
    }
    if(Objects.isNull(authClient.eventSink)){
      AuthResponseModel authResponseModel = AuthResponseModel.initFailed(failedListeningMsg);
      result.success(authResponseModel.toJson());
      return;
    }
    switch (call.method) {
      case "init":
        authClient.initSdk(call.arguments);
        break;
      case "checkEnv":
        authClient.checkEnv();
        break;
      case "accelerateLoginPage":
        authClient.accelerateLoginPage();
      case "login":
        authClient.getLoginToken();
        break;
      case "loginWithConfig":
        authClient.getLoginToken(call.arguments);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void getAliAuthVersion(@NonNull Result result) {
    String version = PhoneNumberAuthHelper.getVersion();
    result.success("阿里云一键登录版本:"+version);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    Log.i(TAG,"onDetachedFromEngine");

    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {

    Log.i(TAG,"onAttachedToActivity:"+binding.getActivity().getClass().getSimpleName());

    Log.i(TAG,"orientation:"+binding.getActivity().getResources().getConfiguration().orientation);

    WeakReference<Activity> activityWeakReference = new WeakReference<>(binding.getActivity());

    authClient.setActivity(activityWeakReference.get());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    Log.i(TAG,"onDetachedFromActivityForConfigChanges");
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    //authClient.setActivity(binding.getActivity());
    Log.i(TAG,"onReattachedToActivityForConfigChanges");

  }

  @Override
  public void onDetachedFromActivity() {
    Log.i(TAG,"onDetachedFromActivity");
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    if(Objects.isNull(authClient.eventSink)){
      authClient.setEventSink(events);
    }
  }

  @Override
  public void onCancel(Object arguments) {
    if(Objects.nonNull(authClient.eventSink)){
      authClient.eventSink = null;
    }
  }

  @Override
  public void onBackPressed() {
    Log.i(TAG,"onBackPressed");
    super.onBackPressed();
  }

  //  @Override
//  protected void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    Log.i(TAG,"onCreate");
//
//  }
//
//  @Override
//  protected void onStart() {
//    super.onStart();
//    Log.i(TAG,"onStart");
//
//  }
//
//  @Override
//  protected void onResume() {
//    super.onResume();
//    Log.i(TAG,"onResume");
//
//  }
//
//  @Override
//  protected void onPause() {
//    super.onPause();
//    Log.i(TAG,"onPause");
//
//  }
//
//  @Override
//  protected void onStop() {
//    super.onStop();
//    Log.i(TAG,"onStop");
//
//  }
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    Log.i(TAG,"onDestroy");
//  }
}
