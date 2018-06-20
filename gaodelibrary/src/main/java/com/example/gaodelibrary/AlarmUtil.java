package com.example.gaodelibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;


/**
 * 定时唤醒功能
 * Created by BLiYing on 2018/5/16.
 */

public class AlarmUtil {
    private static Intent alarmIntent = null;
    private static PendingIntent alarmPi = null;
    private static AlarmManager alarm = null;

    /**
     * 开启定时唤醒
     */
    public static void createAlarmIntent() {
        // 创建Intent对象，action为LOCATION
        if(alarmIntent == null) {
            alarmIntent = new Intent();
            alarmIntent.setAction("LOCATION");
            // 定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
            // 也就是发送了action 为"LOCATION"的intent
            alarmPi = PendingIntent.getBroadcast(UtilsContextOfGaode.getContext(), 0, alarmIntent, 0);
            // AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
            alarm = (AlarmManager) UtilsContextOfGaode.getContext().getSystemService(UtilsContextOfGaode.getContext().ALARM_SERVICE);

            if (null != alarm) {
                int alarmInterval = 5;
                //设置一个闹钟，1秒之后每隔一段时间执行启动一次定位程序
                alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1 * 1000,
                        alarmInterval * 1000, alarmPi);
            }
        }
    }

    /**
     * 关闭定时唤醒
     */
    public static void stopAlarm(){
        if (null != alarm) {
            alarm.cancel(alarmPi);
            alarm = null;
            alarmPi = null;
            alarmIntent = null;
        }
    }
}
