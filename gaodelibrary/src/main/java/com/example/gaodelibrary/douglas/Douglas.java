package com.example.gaodelibrary.douglas;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 道格拉斯-普克算法抽稀少
 * Created by BLiYing on 2018/5/16.
 */

public class Douglas {
    private int start;
    private int end;
    private double dMax;
    private ArrayList<LatLngPoint> mLineInit = new ArrayList<>();
    private ArrayList<LatLngPoint> mLineFilter = new ArrayList<>();

    public Douglas(ArrayList<LatLng> mLineInit, double dmax){
        if(mLineInit == null){
            throw  new IllegalArgumentException("传入的经纬度坐标list为空");
        }
        this.dMax = dmax;
        this.start = 0;
        this.end = mLineInit.size() - 1;
        for (int i = 0; i < mLineInit.size(); i++) {
            this.mLineInit.add(new LatLngPoint(i,mLineInit.get(i)));
        }
    }

    public ArrayList<LatLng> compress(){
        int size = mLineInit.size();
        ArrayList<LatLngPoint> latLngPoints = compressLine(mLineInit.toArray(new LatLngPoint[size]),mLineFilter,start,end,dMax);
        latLngPoints.add(mLineInit.get(0));
        latLngPoints.add(mLineInit.get(size - 1));
        //对抽稀之后的点进行排序
        Collections.sort(latLngPoints, new Comparator<LatLngPoint>() {
            @Override
            public int compare(LatLngPoint latLngPoint, LatLngPoint t1) {

                return latLngPoint.compareTo(t1);
            }
        });
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (LatLngPoint latLngPoint:latLngPoints) {
            latLngs.add(latLngPoint.latLng);
        }
        return latLngs;
    }

    /**
     *
     * @param start  起始点
     * @param end    终止点
     * @param center 中间点
     * @return
     */
    private double disToSegment(LatLngPoint start, LatLngPoint end, LatLngPoint center) {
        double a = Math.abs(AMapUtils.calculateLineDistance(start.latLng, end.latLng));
        double b = Math.abs(AMapUtils.calculateLineDistance(start.latLng, center.latLng));
        double c = Math.abs(AMapUtils.calculateLineDistance(center.latLng, end.latLng));
        double p = (a + b + c) / 2.0;
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        double h = s * 2.0 / a;
        return h;
    }

    private ArrayList<LatLngPoint> compressLine(LatLngPoint[] originalLatLngs, ArrayList<LatLngPoint> endLatLngs, int start, int end, double dMax) {

        int centerIndex = 0;
        double max = 0;
        LatLngPoint startLng = originalLatLngs[start];
        LatLngPoint endLng = originalLatLngs[end];

        for (int j = start + 1; j < end; j++) {
            double dis = disToSegment(startLng, endLng, originalLatLngs[j]);
            if (dis > max) {
                centerIndex = j;
                max = dis;
            }
        }


        if (max > dMax) {
            endLatLngs.add(originalLatLngs[centerIndex]);
            compressLine(originalLatLngs, endLatLngs, start, centerIndex, dMax);
            compressLine(originalLatLngs, endLatLngs, centerIndex, end, dMax);
        }
        return endLatLngs;
    }
}
