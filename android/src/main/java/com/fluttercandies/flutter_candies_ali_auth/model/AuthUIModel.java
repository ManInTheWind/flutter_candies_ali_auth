package com.fluttercandies.flutter_candies_ali_auth.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class AuthUIModel {
    // gloabal config both fullscreen and alert

    public String backgroundColor;  // 十六进制的颜色

    public String backgroundImage;

    public String alertContentViewColor;  // 十六进制的颜色

    public String alertBlurViewColor;  // 底部蒙层背景颜色，默认黑色

    public Double alertBlurViewAlpha;  // 底部蒙层背景透明度，默认0.5

    public Double alertBorderRadius;  // 四个角的圆角，默认为10

    public Double alertWindowWidth;

    public Double alertWindowHeight;

    // status bar
    public Boolean prefersStatusBarHidden;

    // nav
    public Boolean navIsHidden;
    public String navTitle;
    public String navTitleColor;
    public Integer navTitleSize;
    public Double navFrameOffsetX;
    public Double navFrameOffsetY;
    public String navColor;

    // nav backItem
    public Boolean hideNavBackItem;

    public String navBackImage;
    public Double navBackButtonOffsetX;
    public Double navBackButtonOffsetY;

    // alert bar
    public Boolean alertBarIsHidden;
    public Boolean alertCloseItemIsHidden;
    public String alertTitleBarColor;


    public String alertTitleText;
    public String alertTitleTextColor;
    public Integer alertTittleTextSize;

    public String alertCloseImage;
    public Double alertCloseImageOffsetX;
    public Double alertCloseImageOffsetY;

    // logo
    public Boolean logoIsHidden;
    public String logoImage;
    public Double logoWidth;
    public Double logoHeight;

    public Double logoFrameOffsetX;
    public Double logoFrameOffsetY;


    // slogan
    public Boolean sloganIsHidden;
    public String sloganText;
    public String sloganTextColor;
    public Integer sloganTextSize;

    public Double sloganFrameOffsetX;
    public Double sloganFrameOffsetY;

    // number
    public String numberColor;
    public Integer numberFontSize;

    public Double numberFrameOffsetX;
    public Double numberFrameOffsetY;


    // login button
    public String loginBtnText;
    public String loginBtnTextColor;
    public Integer loginBtnTextSize;

    public String loginBtnNormalImage;
    public String loginBtnUnableImage;
    public String loginBtnPressedImage;

    public Double loginBtnFrameOffsetX;
    public Double loginBtnFrameOffsetY;

    public Double loginBtnWidth;
    public Double loginBtnHeight;

    public Double loginBtnLRPadding;

    // change button
    public Boolean changeBtnIsHidden;
    public String changeBtnTitle;
    public String changeBtnTextColor;
    public Integer changeBtnTextSize;
    public Double changeBtnFrameOffsetX;
    public Double changeBtnFrameOffsetY;


    // checkBox
    public Boolean checkBoxIsChecked;
    public Boolean checkBoxIsHidden;
    public Double checkBoxWH;

    public String checkedImage;
    public String uncheckImage;

    // priavacy
    public String privacyOneName;
    public String privacyOneUrl;
    public String privacyTwoName;
    public String privacyTwoUrl;
    public String privacyThreeName;
    public String privacyThreeUrl;

    public Integer privacyFontSize;
    public String privacyFontColor;

    public Double privacyFrameOffsetX;

    public Double privacyFrameOffsetY;

    public String privacyConnectTexts;

    public String privacyPreText;

    public String privacySufText;

    public String privacyOperatorPreText;

    public String privacyOperatorSufText;

    public Integer privacyOperatorIndex;

}
