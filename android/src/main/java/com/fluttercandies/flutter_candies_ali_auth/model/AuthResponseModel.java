package com.fluttercandies.flutter_candies_ali_auth.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson2.JSON;
import com.mobile.auth.gatewayauth.ResultCode;

import java.util.Map;
import java.util.Objects;

public class AuthResponseModel {
   public String resultCode;
   public String msg;
   public String requestId;
   public String token;
   public String innerMsg;
   public String innerCode;

    public AuthResponseModel(@NonNull String resultCode, @NonNull  String msg,
                             @NonNull  String requestId, @Nullable String token,
                             @Nullable String innerMsg, @Nullable String innerCode) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.requestId = requestId;
        this.token = token;
        this.innerMsg = innerMsg;
        this.innerCode = innerCode;
    }

   public static AuthResponseModel initFailed(@Nullable String msg){
        String now = Long.toString(System.currentTimeMillis());
        return new AuthResponseModel(ResultCode.CODE_ERROR_ANALYZE_SDK_INFO,"初始化失败或未初始化",now,null,null,null);
    }

    public static AuthResponseModel NullSdkError(){
        String now = Long.toString(System.currentTimeMillis());
        return new AuthResponseModel(ResultCode.CODE_ERROR_ANALYZE_SDK_INFO,"初始化失败，sdk为空",now,null,null,null);
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setInnerMsg(String innerMsg) {
        this.innerMsg = innerMsg;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getToken() {
        return token;
    }

    public String getInnerMsg() {
        return innerMsg;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public Map toJson( ){
        return JSON.to(Map.class,this);
    }
}
