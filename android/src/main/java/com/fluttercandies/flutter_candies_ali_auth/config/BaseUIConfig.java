package com.fluttercandies.flutter_candies_ali_auth.config;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fluttercandies.flutter_candies_ali_auth.Constant;
import com.fluttercandies.flutter_candies_ali_auth.model.AuthUIModel;
import com.fluttercandies.flutter_candies_ali_auth.utils.AppUtils;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import static com.fluttercandies.flutter_candies_ali_auth.utils.AppUtils.dp2px;
import com.fluttercandies.flutter_candies_ali_auth.R;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;


public abstract class BaseUIConfig {
    public Activity mActivity;
    public Context mContext;
    public PhoneNumberAuthHelper mAuthHelper;
    public int mScreenWidthDp;
    public int mScreenHeightDp;
    public EventChannel.EventSink mEventSink;

    public static BaseUIConfig init(int type, Activity activity, PhoneNumberAuthHelper authHelper, EventChannel.EventSink eventSink) {
        switch (type) {
            case Constant.FULL_PORT:
                return new FullPortConfig(activity, authHelper,eventSink);
            case Constant.DIALOG_BOTTOM:
                return new DialogBottomConfig(activity, authHelper,eventSink);
            case Constant.DIALOG_PORT:
                return new DialogPortConfig(activity, authHelper,eventSink);
            default:
                return null;
        }
    }

    public BaseUIConfig(Activity activity, PhoneNumberAuthHelper authHelper,EventChannel.EventSink eventSink) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mAuthHelper = authHelper;
        mEventSink = eventSink;
    }

    protected View initSwitchView(int marginTop) {
        TextView switchTV = new TextView(mActivity);
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dp2px(mActivity, 50));
        //????????????????????????marginTop 270dp
        mLayoutParams.setMargins(0, dp2px(mContext, marginTop), 0, 0);
        mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        switchTV.setText(R.string.switch_msg);
        switchTV.setTextColor(Color.BLACK);
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.0F);
        switchTV.setLayoutParams(mLayoutParams);
        return switchTV;
    }

    protected void updateScreenSize(int authPageScreenOrientation) {
        int screenHeightDp = AppUtils.px2dp(mContext, AppUtils.getPhoneHeightPixels(mContext));
        int screenWidthDp = AppUtils.px2dp(mContext, AppUtils.getPhoneWidthPixels(mContext));
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_BEHIND) {
            authPageScreenOrientation = mActivity.getRequestedOrientation();
        }
        if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE) {
            rotation = Surface.ROTATION_90;
        } else if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT) {
            rotation = Surface.ROTATION_180;
        }
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                mScreenWidthDp = screenWidthDp;
                mScreenHeightDp = screenHeightDp;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                mScreenWidthDp = screenHeightDp;
                mScreenHeightDp = screenWidthDp;
                break;
            default:
                break;
        }
    }

    public abstract void configAuthPage(FlutterPlugin.FlutterPluginBinding flutterPluginBinding,AuthUIModel authUIModel);

    /**
     *  ?????????APP???????????????????????????????????????APP???????????????????????????????????????
     *  Android8.0????????????SCREEN_ORIENTATION_BEHIND?????????Activity
     */
    public void onResume() {

    }
}
