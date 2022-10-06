package com.fluttercandies.flutter_candies_ali_auth;

import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.MSG_GET_MASK_SUCCESS;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.errorArgumentsMsg;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.failedListeningMsg;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.initFailedMsg;
import static com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel.preLoginSuccessMsg;
import static com.fluttercandies.flutter_candies_ali_auth.utils.AppUtils.dp2px;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson2.JSON;
import com.fluttercandies.flutter_candies_ali_auth.config.BaseUIConfig;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthModel;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.CustomInterface;
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

    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener tokenResultListener;

    public Activity mActivity;
    public Context mContext;

    public AuthModel authModel;

    public EventChannel.EventSink eventSink;

    public BaseUIConfig baseUIConfig;

    private ProgressDialog mProgressDialog;


    public AuthClient() {
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
        this.mContext = mActivity.getApplicationContext();
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
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };

        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(mContext,tokenResultListener);
        mPhoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        mPhoneNumberAuthHelper.setAuthSDKInfo(authModel.androidSdk);
        checkEnv();
    }

    /**
     * 检查环境是否可用
     */
    public void checkEnv(){
        if(Objects.isNull(mPhoneNumberAuthHelper)){
            AuthResponseModel authResponseModel = AuthResponseModel.initFailed(initFailedMsg);
            eventSink.success(authResponseModel.toJson());
            return;
        }
        mPhoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_AUTH);
    }
    /**
     * 在不是一进app就需要登录的场景 建议调用此接口 加速拉起一键登录页面
     * 等到用户点击登录的时候 授权页可以秒拉
     * 预取号的成功与否不影响一键登录功能，所以不需要等待预取号的返回。
     * @param
     */
    public void accelerateLoginPage(){
        if(Objects.isNull(mPhoneNumberAuthHelper)){
            AuthResponseModel authResponseModel = AuthResponseModel.initFailed(initFailedMsg);
            eventSink.success(authResponseModel.toJson());
            return;
        }
        int timeout = 5000;
        mPhoneNumberAuthHelper.accelerateLoginPage(timeout, new PreLoginResultListener() {
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
        baseUIConfig = BaseUIConfig.init(0,mActivity,mPhoneNumberAuthHelper,eventSink);
        assert baseUIConfig != null;
        baseUIConfig.configAuthPage();
        tokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
               // Log.w(TAG,"获取Token成功:"+s);
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    AuthResponseModel authResponseModel = AuthResponseModel.fromTokenRect(tokenRet);
                    eventSink.success(authResponseModel.toJson());
                    if(ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())){
                        mPhoneNumberAuthHelper.quitLoginPage();
                        mPhoneNumberAuthHelper.setAuthListener(null);
                    }
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
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper.setAuthListener(tokenResultListener);
        mPhoneNumberAuthHelper.getLoginToken(mContext,5000);
    }
}
