package com.example.gaodelibrary;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import java.util.List;

/**
 * 前台服务
 * Created by ly on 2018/5/7.
 */
public class GaodeLibraryService extends Service{
    public final static String Tag = "GaodeLibraryService";
    public static boolean isCheck = false;
    public static boolean isRunning = false;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        //保活机制之一，播放无声音乐
        mediaPlayer = MediaPlayer.create(UtilsContextOfGaode.getContext(), R.raw.slient);
        mediaPlayer.setWakeMode(UtilsContextOfGaode.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setLooping(true);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.e(Tag, "GaodeLibraryService onStartCommand");
        if (mediaPlayer != null) {
            mediaPlayer.start();

        }

        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (isCheck) {
                    try {
                        // TODO: 2018/5/18 如果无效则删除
                        if(isBackground(UtilsContextOfGaode.getContext())){
                            Log.e(Tag, "----------已退入后台----------");
                            Intent intent1 = new Intent();
                            intent1.setAction("LOCATION");
                            sendBroadcast(intent1);

                        }
                        Log.e(Tag, "----------发起一条新通知----------");
                        startForeground(1, GaodeEntity.notification);
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e(Tag, "thread sleep failed");
                    }


                }
            }

        }.start();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
    }

    private boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }



}
