![轨迹回放动画](https://github.com/BLiYing/GaodeLiabrary/blob/master/GIF.gif)
### 二维码下载
![二维码下载](https://github.com/BLiYing/GaodeLiabrary/blob/master/下载二维码.png)
### v2.0.0版本使用说明

优势：解决使用高德地图，百度地图等，无法在app处于后台时持续定位（比如service中）或者在后台定位一段时间后app被杀死的问题。主要思路就是循环播放非常短的一段无声音乐(由音乐app受启发)。

### 引入依赖库后
```
implementation 'com.github.BLiYing:GaodeLiabrary:v2.0.0'
```

#### 1.在manifest中注册后台服务

```
<service
android:name="com.example.gaodelibrary.GaodeLibraryService"
android:enabled="true"
android:exported="false"
/>
```
#### 2.注册wake_lock权限(位置权限也需要自己动态申请)

```
<uses-permission android:name="android.permission.WAKE_LOCK" />
```
#### 3.在基类application中初始化

```
UtilsContextOfGaode.init(this);
```
### 在具体页面中使用：

##### 备注：v2.0.0版本去掉了2D和3D地图包，只保留轨迹点采集功能，实时画线功能需要自己去实现了。库中自带setUpMap(trackPoints)方法可参考

#### 第一步，初始化

##### 如果只是单纯使用高德地图的定位功能：

```
GaodeEntity gaodeEntity = new GaodeEntity（"上下文"）；
//初始化定位相关参数
gaodeEntity.initLocation();
//开始定位
gaodeEntity.startLocation()；
//关闭定位
gaodeEntity.closeLocation()；

```

##### 如果需要采集轨迹点：

```
//参数说明：context上下文；startClass点击通知栏跳转的页面；resIdIcon通知栏图标；
//因为需要持续采集，所以用到了后台服务，必须告知用户；
GaodeEntity gaodeEntity = new GaodeEntity(Context context, Class<?> startClass,int resIdIcon)；
//开始采集轨迹,此方法中处理了手机休眠和切换应用后app可能被杀死导致应用无法持续采集轨迹问题；
gaodeEntity.startTrace();
//停止采集
gaodeEntity.stopTrace();
```

#### 第二步，需要经纬度则实现相应回调监听

```
OnGaodeLibraryListen.LocationListen得到AMapLocation对象；

```
#### 如果需要画出连续的实时轨迹图：

```
获取AMapLocation后，可以过滤掉你不需要的点，比如
//速度为0时不进行业务运算
float speed = aMapLocation.getSpeed();
                if (speed == 0) {
                    return;
                }
//精度大于100米的点不进行业务运算
float accuracy = aMapLocation.getAccuracy();
                if (accuracy > 100) {
                    return;
                }
                
如果需要实时画线，声明一个List<LatLng>数组变量trackPoints来容纳轨迹点LatLng；

画线方法：
trackPoints.add(currentLatLng);
//库中自带setUpMap(trackPoints)方法可参考，由于v2.0.0版本去掉了2d和3d地图包，只能自己单独实现；

距离方法：
//计算上一个经纬度和下一个经纬度距离然后叠加
float distance = AMapUtils.calculateLineDistance(lastLatLng, currentLatLng);


```

#### 抽稀后上传：

```
/**
* 道格拉斯-普克算法抽稀之后上传，可以显著压缩采集点数量同时不影响轨迹显示，
* 节约一定的上传流量，保持手持端和web端显示一致
*/
Douglas douglas = new Douglas(new ArrayList<>(trackPoints), 2);
ArrayList<LatLng> latLngs_compress = douglas.compress();
```
