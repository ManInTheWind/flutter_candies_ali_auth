package com.fluttercandies.flutter_candies_ali_auth;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CalculationActivity extends Activity {

    protected static PhoneNumberAuthHelper authHelper;

    public static String TAG = CalculationActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        overridePendingTransition(R.anim.in_activity,R.anim.stay_animation);
        authHelper.getLoginToken(this.getBaseContext(),5000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"onStart");

        super.onStart();
    }
    boolean isPause = false;
    @Override
    protected void onPause() {
        Log.i(TAG,"onPause");
        isPause = true;
        super.onPause();
    }
    boolean isTopActivityBack = false;
    @Override
    protected void onResume() {
        Log.i(TAG,"onResume");
        if (isPause){
            isTopActivityBack = true;
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                   finish();
                }
            };
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.schedule(runnable,0, TimeUnit.MILLISECONDS);
        }
        super.onResume();
    }



    @Override
    protected void onRestart() {
        Log.i(TAG,"onRestart");

        super.onRestart();
    }



    @Override
    public void onBackPressed() {
        Log.i(TAG,"onBackPressed");

        super.onBackPressed();
        //overridePendingTransition(R.anim.in_activity,R.anim.out_activity);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        //overridePendingTransition(R.anim.in_activity,R.anim.out_activity);
    }

    /**
     *
     * 判断某activity是否处于栈顶
     * @return  true在栈顶 false不在栈顶
     */
    private boolean isActivityTop(Class cls,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }


    @Override
    public void finish() {
        super.finish();
    }


}
