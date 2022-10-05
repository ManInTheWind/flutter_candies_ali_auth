package com.fluttercandies.flutter_candies_ali_auth;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson2.JSON;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthModel;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthResponseModel;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

import java.util.Map;
import java.util.Objects;

import io.flutter.plugin.common.MethodChannel;

public class AuthClient {
    private static final String TAG = AuthClient.class.getSimpleName();

    private PhoneNumberAuthHelper authHelper;
    private TokenResultListener tokenResultListener;

    public Activity mActivity;
    public Context context;

    public AuthModel authModel;

    public AuthClient() {
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
        this.context = mActivity.getBaseContext();
    }

    public void setAuthModel(AuthModel authModel) {
        this.authModel = authModel;
    }

    public void initSdk(MethodChannel.Result result){
        tokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                TokenRet tokenRet = TokenRet.fromJson(s);
                result.success(JSON.to(Map.class,tokenRet));
            }

            @Override
            public void onTokenFailed(String s) {
                TokenRet tokenRet = TokenRet.fromJson(s);
                result.success(JSON.to(Map.class,tokenRet));
            }
        };

        authHelper = PhoneNumberAuthHelper.getInstance(context,tokenResultListener);
    }

    /**
     * 检查环境是否可用
     * @param result
     */
    public void checkEnv(MethodChannel.Result result){
        if(Objects.isNull(authHelper)){
            AuthResponseModel authResponseModel = AuthResponseModel.initFailed("请先初始化SDK");
            result.success(authResponseModel.toJson());
            return;
        }
        authHelper.
        authHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_AUTH);
    }

    public void accelerateLoginPage(){
    }
}
