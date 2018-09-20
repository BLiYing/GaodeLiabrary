引入依赖库后
1.在manifest中注册后台服务
<service android:name="com.example.gaodelibrary.GaodeLibraryService"
            android:enabled="true"
            android:exported="false"
            />
2.注册wake_lock 权限
3.在基类application中初始化UtilsContextOfGaode.init(this);

在具体页面中使用：

第一步，初始化
（1）如果只是单纯使用高德地图的定位功能：
GaodeEntity gaodeEntity = new GaodeEntity（"上下文"）；
//初始化定位相关参数
gaodeEntity.initLocation();
//开始定位
gaodeEntity.startLocation()；
//关闭定位
gaodeEntity.closeLocation()；

（2）如果需要采集轨迹点来画线：
//参数说明：context上下文；startClass点击通知栏跳转的页面；resIdIcon通知栏图标；
//因为需要持续采集，所以用到了后台服务，必须告知用户；
GaodeEntity gaodeEntity = new GaodeEntity(Context context, Class<?> startClass,int resIdIcon)；
//开始采集轨迹,此方法中处理了手机休眠和切换应用后app可能被杀死导致应用无法持续采集轨迹问题；
gaodeEntity.startTrace();
//停止采集
gaodeEntity.stopTrace();

第二步，需要经纬度则实现相应回调监听OnGaodeLibraryListen.LocationListen得到AMapLocation对象；

如果需要画出连续的实时轨迹图：
获取AMapLocation后，声明一个List<LatLng>数组变量trackPoints来容纳轨迹点LatLng，可以过滤掉你不需要的点，比如
//速度为0时
float speed = aMapLocation.getSpeed();
                if (speed == 0) {
                    return;
                }
//例如精度大于100米的点不进行业务运算
float accuracy = aMapLocation.getAccuracy();
                if (accuracy > 100) {
                    return;
                }

画线方法：
trackPoints.add(currentLatLng);
//库中自带方法，也可以自己去实现
gaodeEntity.setUpMap(trackPoints);
//计算上一个经纬度和下一个经纬度距离然后叠加
float distance = AMapUtils.calculateLineDistance(lastLatLng, currentLatLng);
抽稀后上传
     /**
     * 道格拉斯-普克算法抽稀之后上传，可以显著压缩采集点数量同时不影响轨迹显示，
     * 节约一定的上传流量，保持手持端和web端显示一致
     */
    Douglas douglas = new Douglas(new ArrayList<>(trackPoints), 2);
    ArrayList<LatLng> latLngs_compress = douglas.compress();

