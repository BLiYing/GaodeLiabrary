package com.tepia.bliying.douglas;
import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;

/**
 * Created by BLiYing on 2018/5/16.
 */

public class LatLngPoint implements Comparable<LatLngPoint>{

    /**
     * 用于记录每个点的序号
     */
    public int id;
    /**
     * 每一个点的经纬度
     */
    public LatLng latLng;

    public LatLngPoint(int id, LatLng latLng){
        this.id = id;
        this.latLng = latLng;
    }


    @Override
    public int compareTo(@NonNull LatLngPoint latLngPoint) {
        if(this.id < latLngPoint.id){
            return -1;
        }else if(this.id > latLngPoint.id){
            return 1;
        }

        return 0;
    }
}
