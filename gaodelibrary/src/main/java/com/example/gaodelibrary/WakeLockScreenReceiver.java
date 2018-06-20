package com.example.gaodelibrary;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * 锁屏通知
 */
public class WakeLockScreenReceiver extends BroadcastReceiver {

    private WakeLock wakeLock = null;
    public static boolean hasScreenOn;


    public WakeLockScreenReceiver(WakeLock wakeLock) {
        super();
        this.wakeLock = wakeLock;

    }

    @SuppressLint("Wakelock")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            if (null != wakeLock && !(wakeLock.isHeld())) {
                Log.e("WakeLockScreenReceiver","锁屏执行wakeLock.acquire()");
                wakeLock.acquire();

                hasScreenOn = true;
                AlarmUtil.createAlarmIntent();

            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_PRESENT.equals(action)) {
            if (null != wakeLock && wakeLock.isHeld()) {
                Log.e("WakeLockScreenReceiver","开屏执行release");
                wakeLock.release();

                hasScreenOn = false;
                AlarmUtil.stopAlarm();

            }
        }

    }





}
