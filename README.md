# Baidu Geolocation for Cordova

[百度 Android 定位SDK](http://lbsyun.baidu.com/index.php?title=android-locsdk)

Baidu 全量定位 SDK 版本：v7.3（全量定位：包含离线定位、室内高精度定位能力，同时提供更人性化的位置描述服务；）

Cordova 百度定位插件，兼容 W3C 的 geolocation 标准，解决中国大陆手机无法定位的问题。

基于[ETENG-OSP](https://github.com/ETENG-OSP/cordova-plugin-baidu-geolocation)的成果进行修改，非常感谢！

## 安装

在控制台里，进入 cordova 项目目录，执行以下命令：

```bash
cordova plugin add cordova-plugin-bd-geolocation --variable API_KEY=百度分配的AK --save
```

如果需要同时在 iOS 里和 Android 里使用，请在 `config.xml` 里分别配置：

```xml
...
  <!-- android 使用本插件 -->
  <platform name="android">
    <plugin name="cordova-plugin-bd-geolocation" spec="^8.0.0">
        <variable name="API_KEY" value="百度分配的AK" />
    </plugin>
  </platform>

  <!-- iOS 使用官方插件 -->
  <platform name="ios">
    <plugin name="cordova-plugin-geolocation" spec="^4.0.1" />
  </platform>
...
```

## 卸载

在控制台里，进入 cordova 项目目录，执行以下命令：

```bash
cordova plugins remove cordova-plugin-bd-geolocation
```

## 关于 API_KEY

使用前需要在百度申请应用，获取 API_KEY。填错了的话仅能使用 GPS 定位，无法使用基站与 WIFI 定位。


## 使用方法

### navigator.geolocation.getCurrentPosition(success, [error], [options]);

获取当前位置

#### options

```
var options = {
  enableHighAccuracy: true,  // 是否使用 GPS
  maximumAge: 30000,         // 缓存时间
  timeout: 27000,            // 超时时间
  coorType: 'bd09ll'         // 默认是 gcj02，可填 bd09ll 以获取百度经纬度用于访问百度 API
}
```

#### success

```
function success(position, [extra]) {
}
```

##### position

```
{
  "coords": {
    "latitude": "number", // 十进制数的纬度
    "longitude": "number", // 十进制数的经度
    "altitude": "number", // 海拔，海平面以上以米计
    "accuracy": "number", // 位置精度
    "altitudeAccuracy": "number", // 位置的海拔精度
    "heading": "number", // 方向，从正北开始以度计
    "speed": "number" // 速度，以米/每秒计
  },
  "timestamp": "number" // 响应的日期/时间
}
```

##### extra

```
{
  "type": "number", // 定位类型。161：网络定位结果，61：GPS定位结果，66：离线定位结果
  "gpsAccuracyStatus": "number", // GPS质量。0：GPS质量判断未知，1：GPS质量判断好，2：GPS质量判断中等，3：GPS质量判断差
  "addr": "string" // 详细地址信息
}
```

注意：由于 W3C 的 geolocation 标准中getCurrentPosition的success回调函数其实是返回一个参数position，因此如果你在typescript中使用，直接写成接收两个参数则会编译出错，因此你可以这样写：

```typescript
navigator.geolocation.watchPosition((...args: any[]) => {
    const position: Position = args[0];
    const extra: any = args[1];
    console.log(position);
    console.log(extra);
}, () => {}, options);
```

### navigator.geolocation.watchPosition(success, [error], [options]);

持续追踪位置变更，参数与getCurrentPosition一致。

返回值：watchId

### navigator.geolocation.clearWatch(watchId);

清除位置追踪。

### navigator.geolocation.openFrontLocationService(watchId)

开启前台定位服务。适配 android 8.0 以上对后台定位的限制

### navigator.geolocation.closeFrontLocationService(watchId)

关闭前台定位服务

## 关于坐标系

由于 Baidu 定位的限制，这个插件仅能获取中国偏移坐标系 GCJ02 与 BD09LL（LL 指代经纬度）。如果需要坐标系的转换，请使用第三方服务。

如果期望离线转换坐标系，可以使用这个算法：https://github.com/googollee/eviltransform

这个插件不对转换的结果负责。
