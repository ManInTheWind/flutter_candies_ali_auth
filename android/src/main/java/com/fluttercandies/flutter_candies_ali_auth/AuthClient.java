package com.fluttercandies.flutter_candies_ali_auth;

import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.MSG_GET_MASK_SUCCESS;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.errorArgumentsMsg;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.failedListeningMsg;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.initFailedMsg;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.preLoginSuccessMsg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.fluttercandies.flutter_candies_ali_auth.config.BaseUIConfig;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthModel;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.PreLoginResultListener;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;
import java.util.Map;
import java.util.Objects;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class AuthClient {
    private static final String TAG = AuthClient.class.getSimpleName();

    private PhoneNumberAuthHelper authHelper;
    private TokenResultListener tokenResultListener;

    public Activity mActivity;
    public Context context;

    public AuthModel authModel;

    public EventChannel.EventSink eventSink;

    public BaseUIConfig baseUIConfig;

    private ProgressDialog mProgressDialog;


    public AuthClient() {
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
        this.context = mActivity.getBaseContext();
    }

    public void setEventSink(EventChannel.EventSink eventSink) {
        this.eventSink = eventSink;
    }

    public EventChannel.EventSink getEventSink() {
        return eventSink;
    }

    public void setAuthModel(AuthModel authModel) {
        this.authModel = authModel;
    }

    public void initSdk(Object arguments){
        try {
            authModel = AuthModel.fromJson(arguments);
        }catch (Exception e){
            AuthResponseModel authResponseModel = AuthResponseModel.initFailed(errorArgumentsMsg);
            eventSink.success(authResponseModel.toJson());
        }

        if(Objects.isNull(authModel.androidSdk) || TextUtils.isEmpty(authModel.androidSdk)){
            AuthResponseModel authResponseModel = AuthResponseModel.nullSdkError();
            eventSink.success(authResponseModel.toJson());
            return;
        }
        Log.i(TAG,"authModel:"+authModel);
        tokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                System.out.println("onTokenSuccess");
                //eventSink.success(JSON.parseObject(tokenRet.toJsonString(),Map.class));
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    Log.w(TAG,"tokenRet:"+tokenRet);

                    if(ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(tokenRet.getCode())){
                        //终端支持认证 当前环境可以进行一键登录
                        AuthResponseModel authResponseModel = AuthResponseModel.fromTokenRect(tokenRet);
                        eventSink.success(authResponseModel.toJson());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.e(TAG, "获取token失败：" + s);
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    AuthResponseModel authResponseModel = AuthResponseModel.fromTokenRect(tokenRet);
                    eventSink.success(authResponseModel.toJson());
                }catch (Exception e){
                    AuthResponseModel authResponseModel = AuthResponseModel.tokenDecodeFailed();
                    eventSink.success(authResponseModel.toJson());
                    e.printStackTrace();
                }
                authHelper.setAuthListener(null);
            }
        };

        authHelper = PhoneNumberAuthHelper.getInstance(context,tokenResultListener);
        authHelper.getReporter().setLoggerEnable(true);
        authHelper.setAuthSDKInfo(authModel.androidSdk);
        checkEnv();
    }

    /**
     * 检查环境是否可用
     */
    public void checkEnv(){
        if(Objects.isNull(authHelper)){
            AuthResponseModel authResponseModel = AuthResponseModel.initFailed(initFailedMsg);
            eventSink.success(authResponseModel.toJson());
            return;
        }
        authHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_AUTH);
    }

    public void accelerateLoginPage(){
        if(Objects.isNull(authHelper)){
            AuthResponseModel authResponseModel = AuthResponseModel.initFailed(initFailedMsg);
            eventSink.success(authResponseModel.toJson());
            return;
        }
        int timeout = 5000;
        authHelper.accelerateLoginPage(timeout, new PreLoginResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                Log.i(TAG, "预取号失败：" + s);
                try {
                    AuthResponseModel authResponseModel = AuthResponseModel.customModel(
                            MSG_GET_MASK_SUCCESS,preLoginSuccessMsg
                    );
                    eventSink.success(authResponseModel.toJson());
                }catch (Exception e){
                    AuthResponseModel authResponseModel = AuthResponseModel.tokenDecodeFailed();
                    eventSink.success(authResponseModel.toJson());
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s, String s1) {
                Log.e(TAG, "预取号失败：" + s);
                try {
                    AuthResponseModel authResponseModel = AuthResponseModel.customModel(
                            ResultCode.CODE_GET_MASK_FAIL,ResultCode.MSG_GET_MASK_FAIL
                    );
                    eventSink.success(authResponseModel.toJson());
                }catch (Exception e){
                    AuthResponseModel authResponseModel = AuthResponseModel.tokenDecodeFailed();
                    eventSink.success(authResponseModel.toJson());
                    e.printStackTrace();
                }
            }
        });
    }

    public void getLoginToken(){
        baseUIConfig = BaseUIConfig.init(1,mActivity,authHelper);
        assert baseUIConfig != null;
        baseUIConfig.configAuthPage();
        tokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                Log.w(TAG,"获取Token成功:"+s);
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    Log.i(TAG,"tokenRet:"+tokenRet);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.w(TAG,"获取Token失败:"+s);
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    Log.i(TAG,"tokenRet:"+tokenRet);
                }catch (Exception e){
                    e.printStackTrace();
                }
                authHelper.setAuthListener(null);
            }
        };
        authHelper.setAuthListener(tokenResultListener);
        authHelper.getLoginToken(context,5000);
    }

    public void showLoadingDialog(String hint) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setMessage(hint);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
