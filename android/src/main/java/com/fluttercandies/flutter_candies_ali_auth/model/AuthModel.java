package com.fluttercandies.flutter_candies_ali_auth.model;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mobile.auth.gatewayauth.AuthUIConfig;

import java.util.Map;

public class AuthModel {
   public String androidSdk;
   public AuthUIStyle authUIStyle;
   //public AuthUIConfig authUIConfig;

   @NonNull
   static public AuthModel fromJson(Object params){
      JSONObject o = (JSONObject) JSON.toJSON(params);
      Integer authUIStyleIndex = (Integer) o.get("authUIStyle");
      AuthUIStyle authUIStyle  = AuthUIStyle.values()[authUIStyleIndex];
      AuthModel authModel = new AuthModel();
      authModel.setAndroidSdk(o.getString("androidSdk"));
      authModel.setAuthUIStyle(authUIStyle);
      return authModel;
   }

   public void setAndroidSdk(String androidSdk) {
      this.androidSdk = androidSdk;
   }

   public void setAuthUIStyle(AuthUIStyle authUIStyle) {
      this.authUIStyle = authUIStyle;
   }

   @NonNull
   @Override
   public String toString() {
      return "AuthModel{" +
              "androidSdk='" + androidSdk + '\'' +
              ", authUIStyle=" + authUIStyle +
              '}';
   }
}
