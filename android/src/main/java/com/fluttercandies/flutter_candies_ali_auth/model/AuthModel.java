package com.fluttercandies.flutter_candies_ali_auth.model;

import com.mobile.auth.gatewayauth.AuthUIConfig;

public class AuthModel {
   public String androidSdk;
   public AuthUIStyle authUIStyle;
   public AuthUIConfig authUIConfig;

   static public AuthModel fromJson(Object params){
      return new AuthModel();
   }
}
