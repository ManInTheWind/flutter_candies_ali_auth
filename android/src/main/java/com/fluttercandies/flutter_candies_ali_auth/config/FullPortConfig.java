package com.fluttercandies.flutter_candies_ali_auth.config;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;

//import com.fluttercandies.flutter_candies_ali_auth.R;
import com.mobile.auth.R;
import com.fluttercandies.flutter_candies_ali_auth.helper.CustomAuthUIControlClickListener;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;

import io.flutter.plugin.common.EventChannel;

public class FullPortConfig extends BaseUIConfig {
    private final String TAG = "全屏竖屏样式";

    public FullPortConfig(Activity activity, PhoneNumberAuthHelper authHelper, EventChannel.EventSink eventSink) {
        super(activity, authHelper, eventSink);
    }

    @Override
    public void configAuthPage() {

        CustomAuthUIControlClickListener customAuthUIControlClickListener = new CustomAuthUIControlClickListener(mAuthHelper, mContext, fEventSink);

        mAuthHelper.setUIClickListener(customAuthUIControlClickListener);

        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        //添加自定义切换其他登录方式
        mAuthHelper.addAuthRegistViewConfig("switch_msg", new AuthRegisterViewConfig.Builder()
                .setView(initSwitchView(350))
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
//                .setCustomInterface(new CustomInterface() {
//                    @Override
//                    public void onClick(Context context) {
//                        Toast.makeText(mContext, "切换到短信登录方式", Toast.LENGTH_SHORT).show();
//                        Intent pIntent = new Intent(mActivity, MessageActivity.class);
//                        mActivity.startActivityForResult(pIntent, 1002);
//                        mAuthHelper.quitLoginPage();
//                    }
//                })
                .build());
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                //沉浸式状态栏
                .setNavColor(Color.parseColor("#026ED2"))
                .setStatusBarColor(Color.parseColor("#026ED2"))
                .setWebViewStatusBarColor(Color.parseColor("#026ED2"))
                .setAuthPageActIn("out_activity", "out_activity")
                .setAuthPageActOut("out_activity", "out_activity")
                .create());
//        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
//                .setAppPrivacyOne("《自定义隐私协议》", "https://test.h5.app.tbmao.com/user")
//                .setAppPrivacyTwo("《百度》", "https://www.baidu.com")
//                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#002E00"))
//                //隐藏默认切换其他登录方式
//                .setSwitchAccHidden(true)
//                //隐藏默认Toast
//                .setLogBtnToastHidden(true)
//                //沉浸式状态栏
//                .setNavColor(Color.parseColor("#026ED2"))
//                .setStatusBarColor(Color.parseColor("#026ED2"))
//                .setWebViewStatusBarColor(Color.parseColor("#026ED2"))
//
//
//                .setLightColor(false)
//                .setWebNavTextSizeDp(20)
//                //图片或者xml的传参方式为不包含后缀名的全称 需要文件需要放在drawable或drawable-xxx目录下 in_activity.xml, mytel_app_launcher.png
//                .setAuthPageActIn("out_activity", "out_activity")
//                .setAuthPageActOut("out_activity", "out_activity")
//                .setProtocolShakePath("protocol_shake")
//                .setVendorPrivacyPrefix("《")
//                .setVendorPrivacySuffix("》")
//                .setPageBackgroundPath("page_background_color")
//                .setLogoImgPath("mytel_app_launcher")
//                //一键登录按钮三种状态背景示例login_btn_bg.xml
//                .setLogBtnBackgroundPath("login_btn_bg")
//                .setScreenOrientation(authPageOrientation)
//                .create());
    }


}
