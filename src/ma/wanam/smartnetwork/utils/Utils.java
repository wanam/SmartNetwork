package ma.wanam.smartnetwork.utils;

import java.util.ArrayList;
import java.util.List;

import ma.wanam.smartnetwork.XSystemProp;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import de.robv.android.xposed.XposedBridge;

public class Utils {

	public static PackageInfo pInfo;
	private static Boolean mHasGeminiSupport = null;

	private static PowerManager pm = null;

	public static PowerManager getPm(Context mContext) {
		return (pm == null ? (PowerManager) mContext.getSystemService(Context.POWER_SERVICE) : pm);
	}

	private static AlarmManager am = null;

	public static AlarmManager getAm(Context mContext) {
		return (am == null ? am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE) : am);
	}

	public static boolean isPackageInstalled(Context context, String targetPackage) {
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = context.getPackageManager();
		packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage))
				return true;
		}
		return false;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static boolean getScreenState(Context context) {
		boolean screenON = false;
		try {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			screenON = false;
			if (VERSION.SDK_INT > VERSION_CODES.KITKAT) {
				screenON = pm.isInteractive();
			} else {
				screenON = pm.isScreenOn();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return screenON;
	}

	public static boolean isCallActive(Context context) {
		try {
			AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (manager.getMode() == AudioManager.MODE_IN_CALL
					|| manager.getMode() == AudioManager.MODE_IN_COMMUNICATION
					|| manager.getMode() == AudioManager.MODE_RINGTONE) {
				return true;
			} else {
				return false;
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return false;
	}

	public static boolean isWiFiConnected(Context mContext) {
		try {
			ConnectivityManager connManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return (connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI);
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return false;

	}

	@SuppressLint("InlinedApi")
	public static boolean isCharging(Context context) {
		boolean isCharging = false;
		try {
			isCharging = false;
			Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			isCharging = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
			if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN) {
				isCharging = isCharging || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return isCharging;
	}

	public static boolean hasGeminiSupport() {
		if (mHasGeminiSupport != null)
			return mHasGeminiSupport;
		mHasGeminiSupport = XSystemProp.getBoolean("ro.mediatek.gemini_support", false);
		return mHasGeminiSupport;
	}

	public static final List<String> getConfiguredAccessPoints(Context ctx) {
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		ArrayList<String> myList = new ArrayList<String>();
		List<WifiConfiguration> myConfiguredAccessPoints = wifiManager.getConfiguredNetworks();
		if (myConfiguredAccessPoints != null) {
			for (int i = 0; i < myConfiguredAccessPoints.size(); i++) {
				myList.add(myConfiguredAccessPoints.get(i).SSID);
			}
		}
		return myList;
	}

	public static String stripLeadingAndTrailingQuotes(String str) {
		int length = str.length();
		if (length > 1 && str.startsWith("\"") && str.endsWith("\"") && str.substring(1, length - 1).indexOf('"') == -1) {
			str = str.substring(1, length - 1);
		}
		return str;
	}

	@SuppressLint({ "NewApi", "InlinedApi" })
	@SuppressWarnings("deprecation")
	public static boolean isAirplaneModeOn(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		} else {
			return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
		}
	}

	@SuppressLint("NewApi")
	public static void SetAlarm(Context context, int delay, String action, Class<?> scheduler, Bundle sharedPreferences) {
		long nextTrigger = System.currentTimeMillis() + (delay * 60 * 1000);
		Intent intent = new Intent(context, scheduler);
		intent.putExtras(sharedPreferences);
		intent.setAction(action);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		getAm(context).setExact(AlarmManager.RTC_WAKEUP, nextTrigger, pi);

		if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP_MR1) {
			getAm(context).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTrigger, pi);
		} else if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2) {
			getAm(context).setExact(AlarmManager.RTC_WAKEUP, nextTrigger, pi);
		} else {
			getAm(context).set(AlarmManager.RTC_WAKEUP, nextTrigger, pi);
		}

	}

}
