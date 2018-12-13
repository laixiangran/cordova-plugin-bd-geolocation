package com.lai.geolocation.baidu;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lai.geolocation.w3.PositionOptions;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BDGeolocation {
	public static final String COORD_BD09LL = "bd09ll";
	public static final String COORD_BD09 = "bd09";
	public static final String COORD_GCJ02 = "gcj02";

	private String TAG = "BDGeolocation";
	private LocationClient client;
    private NotificationUtils mNotificationUtils;
	private BDLocationListener listener;
	private Context context;
	private Activity activity;

	BDGeolocation(Context ctx, Activity act) {
	    context = ctx;
	    activity = act;
		client = new LocationClient(context);
	}

	private void setOptions(PositionOptions options) {
	    LocationClientOption bdoptions = new LocationClientOption();

		// 设置定位结果坐标系
		String coorType = options.getCoorType();
		if (coorType == null || coorType.trim().isEmpty()) {
			coorType = COORD_GCJ02;
		}

        // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        bdoptions.setCoorType(coorType);

		// 设置定位模式
		LocationMode locationMode = LocationMode.Battery_Saving;
		if (options.isEnableHighAccuracy()) {
			locationMode = LocationMode.Hight_Accuracy;
		}

		// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		bdoptions.setLocationMode(locationMode);

		// 设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
		bdoptions.setOpenAutoNotifyMode();

		// 可选，默认false, 设置是否需要地址信息
		bdoptions.setIsNeedAddress(true);
		client.setLocOption(bdoptions);
	}

	public boolean getCurrentPosition(PositionOptions options, final BDLocationListener callback) {
		listener = new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				callback.onReceiveLocation(location);
				clearWatch();
			}
		};
		setOptions(options);
		client.registerLocationListener(listener);
		client.start();
		return true;
	}

	public boolean watchPosition(PositionOptions options, BDLocationListener callback) {
		listener = callback;
		setOptions(options);
		client.registerLocationListener(listener);
		client.start();
		return true;
	}

	public boolean clearWatch() {
		client.stop();
		client.unRegisterLocationListener(listener);
		listener = null;
		return true;
	}

	/**
	 * 开启前台定位服务
	 * @return
	 */
	public boolean openFrontLocationService() {
	    Notification notification = null;
        if (Build.VERSION.SDK_INT >= 26) {
            mNotificationUtils = new NotificationUtils(activity);
            Notification.Builder builder2 = mNotificationUtils.getAndroidChannelNotification("后台定位通知", "正在进行后台定位");
            notification = builder2.build();
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            Intent nfIntent = new Intent(context,Activity.class);
            builder.setContentIntent(
                    PendingIntent.getActivity(activity, 0, nfIntent, 0)) // 设置PendingIntent
                    .setContentTitle("后台定位通知") // 设置下拉列表里的标题
			        .setSmallIcon(android.R.drawable.stat_notify_more) // 设置状态栏内的小图标
                    .setContentText("正在进行后台定位") // 设置上下文内容
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
            notification = builder.build();
        }
		notification.defaults = Notification.DEFAULT_SOUND; // 设置为默认的声音
		client.enableLocInForeground(1001, notification);// 调起前台定位
		return true;
	}

	/**
	 * 关闭前台定位服务，同时移除通知栏
	 * @return
	 */
	public boolean closeFrontLocationService() {
		client.disableLocInForeground(true);
		return true;
	}
}
