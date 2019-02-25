package com.example.gaodelibrary;

import android.app.Notification;

import com.amap.api.location.AMapLocation;

/**
 * Created by BLiYing on 2018/6/3.
 */

public interface OnGaodeLibraryListen {
    interface LocationListen{
        void getCurrentGaodeLocation(AMapLocation aMapLocation);

    }
    interface DistanceListen{
        void getDistance(double distance);

    }

    interface NotificationListen{
        void getNotificationListen(Notification notification);
    }

    interface DrawTraceListen{
        void drawTrace();
    }

}
