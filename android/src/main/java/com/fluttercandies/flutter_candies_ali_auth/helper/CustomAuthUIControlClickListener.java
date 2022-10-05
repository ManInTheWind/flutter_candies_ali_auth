package com.fluttercandies.flutter_candies_ali_auth.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fluttercandies.flutter_candies_ali_auth.R;
import com.mobile.auth.gatewayauth.AuthUIControlClickListener;
import com.mobile.auth.gatewayauth.ResultCode;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomAuthUIControlClickListener implements AuthUIControlClickListener {
    @Override
    public void onClick(String code, Context context, String jsonString) {
        JSONObject jsonObj = null;
        try {
            if(!TextUtils.isEmpty(jsonString)) {
                jsonObj = new JSONObject(jsonString);
            }
        } catch (JSONException e) {
            jsonObj = new JSONObject();
        }
        switch (code) {
            //点击授权页默认样式的返回按钮
            case ResultCode.CODE_ERROR_USER_CANCEL:
                Log.e(TAG, "点击了授权页默认返回按钮");
                mAuthHelper.quitLoginPage();
               // mActivity.finish();
                break;
            //点击授权页默认样式的切换其他登录方式 会关闭授权页
            //如果不希望关闭授权页那就setSwitchAccHidden(true)隐藏默认的  通过自定义view添加自己的
            case ResultCode.CODE_ERROR_USER_SWITCH:
                Log.e(TAG, "点击了授权页默认切换其他登录方式");
                break;
            //点击一键登录按钮会发出此回调
            //当协议栏没有勾选时 点击按钮会有默认toast 如果不需要或者希望自定义内容 setLogBtnToastHidden(true)隐藏默认Toast
            //通过此回调自己设置toast
            case ResultCode.CODE_ERROR_USER_LOGIN_BTN:
                if (!jsonObj.optBoolean("isChecked")) {
                    Toast.makeText(mContext, R.string.custom_toast, Toast.LENGTH_SHORT).show();
                }
                break;
            //checkbox状态改变触发此回调
            case ResultCode.CODE_ERROR_USER_CHECKBOX:
                Log.e(TAG, "checkbox状态变为" + jsonObj.optBoolean("isChecked"));
                break;
            //点击协议栏触发此回调
            case ResultCode.CODE_ERROR_USER_PROTOCOL_CONTROL:
                Log.e(TAG, "点击协议，" + "name: " + jsonObj.optString("name") + ", url: " + jsonObj.optString("url"));
                break;
            case ResultCode.CODE_START_AUTH_PRIVACY:
                Log.e(TAG, "点击授权页一键登录按钮拉起了授权页协议二次弹窗");
                break;
            case ResultCode.CODE_AUTH_PRIVACY_CLOSE:
                Log.e(TAG, "授权页协议二次弹窗已关闭");
                break;
            case ResultCode.CODE_CLICK_AUTH_PRIVACY_CONFIRM:
                Log.e(TAG, "授权页协议二次弹窗点击同意并继续" );
                break;
            case ResultCode.CODE_CLICK_AUTH_PRIVACY_WEBURL:
                Log.e(TAG, "点击授权页协议二次弹窗协议" );
                break;
            default:
                break;

        }
    }
}
