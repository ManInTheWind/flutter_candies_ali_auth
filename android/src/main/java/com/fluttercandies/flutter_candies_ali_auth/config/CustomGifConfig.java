package com.fluttercandies.flutter_candies_ali_auth.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.fluttercandies.flutter_candies_ali_auth.R;
import com.fluttercandies.flutter_candies_ali_auth.helper.CacheManage;
import com.fluttercandies.flutter_candies_ali_auth.helper.NativeBackgroundAdapter;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;

/**
 * xml文件方便预览
 * 可以通过addAuthRegisterXmlConfig一次性统一添加授权页的所有自定义view
 */
public class CustomGifConfig extends BaseUIConfig {
    private CacheManage mCacheManage;
    private ExecutorService mThreadExecutor;
    private NativeBackgroundAdapter nativeBackgroundAdapter;
    public CustomGifConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        super(activity, authHelper);
        mCacheManage=new CacheManage(activity.getApplication());
        mThreadExecutor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), new ThreadPoolExecutor.CallerRunsPolicy());
        nativeBackgroundAdapter =
            new NativeBackgroundAdapter(mCacheManage, mThreadExecutor, activity, "gifPath"
                , "background_gif.gif");
    }
    @Override
    public void configAuthPage() {
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        //sdk默认控件的区域是marginTop50dp
        int designHeight = mScreenHeightDp - 50;
        int unit = designHeight / 10;
        mAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.authsdk_widget_custom_layout, new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        final FrameLayout fly_container = view.findViewById(R.id.fly_container);
                        nativeBackgroundAdapter.solveView(fly_container, "#3F51B5");
                    }
                })
                .build());
        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setAppPrivacyOne("《自定义隐私协议》", "https://test.h5.app.tbmao.com/user")
                .setAppPrivacyTwo("《百度》", "https://www.baidu.com")
                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#FFFFFF"))
                .setNavHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(true)
                .setSwitchAccHidden(true)
                .setPrivacyState(false)
                .setCheckboxHidden(true)
                .setLoadingBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.dialog_page_background))
                //设置setLoadingImgPath图片会开启旋转动画，设置setLoadingImgDrawable则不会，方便自定义drawable动画
                .setLoadingImgPath("icon_notification")
                .setLightColor(true)
                .setNumFieldOffsetY(unit * 6)
                .setLogBtnOffsetY(unit * 7)
                .setWebViewStatusBarColor(Color.TRANSPARENT)
                .setStatusBarColor(Color.TRANSPARENT)
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .setWebNavTextSizeDp(20)
                .setNumberSizeDp(20)
                .setNumberColor(Color.parseColor("#FFFFFF"))
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setPageBackgroundPath("page_background_color")
                .setLogoImgPath("mytel_app_launcher")
                .setLogBtnBackgroundPath("login_btn_bg")
                .setScreenOrientation(authPageOrientation)
                .create());
    }
}
