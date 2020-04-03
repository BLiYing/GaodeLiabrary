package com.example.gaodelibrary;

import android.app.Notification;

import androidx.annotation.Keep;

import com.amap.api.location.AMapLocation;

/**
 * Created by BLiYing on 2018/6/3.
 */
@Keep
public interface OnGaodeLibraryListen {
    @Keep
    interface LocationListen{
        @Keep
        void getCurrentGaodeLocation(AMapLocation aMapLocation);

    }

    @Keep
    interface DistanceListen{
        void getDistance(double distance);

    }

    @Keep
    interface NotificationListen{
        void getNotificationListen(Notification notification);
    }

    interface DrawTraceListen{
        void drawTrace();
    }

}
