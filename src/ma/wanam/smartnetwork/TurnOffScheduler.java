package ma.wanam.smartnetwork;

import ma.wanam.smartnetwork.utils.Constants;
import ma.wanam.smartnetwork.utils.Utils;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.PowerManager;

public class TurnOffScheduler extends BroadcastReceiver {

	private static PowerManager pm = null;

	public static PowerManager getPm(Context mContext) {
		return (pm == null ? (PowerManager) mContext.getSystemService(Context.POWER_SERVICE) : pm);
	}

	@SuppressLint({ "Wakelock", "WorldReadableFiles" })
	@Override
	public void onReceive(Context context, Intent intent) {

		PowerManager.WakeLock wl = null;

		try {

			if (!Utils.getScreenState(MainApplication.getAppContext())) {
				wl = getPm(MainApplication.getAppContext()).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						MainApplication.getAppContext().getClass().getName());
				wl.acquire();

				Bundle sharedPreferences = intent.getExtras();

				if (intent.getAction().equalsIgnoreCase(Constants.ACTION_MOBILE_DATA)) {
					Intent mIntent = new Intent(Constants.ACTION_NETWORK);
					mIntent.putExtra(Constants.SCREEN_STATE, false);
					MainApplication.getAppContext().sendBroadcast(mIntent);

					if (sharedPreferences.getBoolean("enableDataTimer", false)) {
						Utils.SetAlarm(MainApplication.getAppContext(),
								sharedPreferences.getInt("enableDataAfter", 30), Constants.ACTION_MOBILE_DATA_ON,
								TurnONScheduler.class, sharedPreferences);
					}
				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_SYNC_DATA)) {
					Intent mIntent = new Intent(Constants.ACTION_SYNC);
					mIntent.putExtra(Constants.SCREEN_STATE, false);
					MainApplication.getAppContext().sendBroadcast(mIntent);

					if (sharedPreferences.getBoolean("enableSyncTimer", false)) {
						Utils.SetAlarm(MainApplication.getAppContext(),
								sharedPreferences.getInt("enableSyncAfter", 30), Constants.ACTION_SYNC_DATA_ON,
								TurnONScheduler.class, sharedPreferences);
					}
				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_WIFI_CHANGE)) {
					Intent mIntent = new Intent(Constants.ACTION_WIFI);
					mIntent.putExtra(Constants.SCREEN_STATE, false);
					MainApplication.getAppContext().sendBroadcast(mIntent);

					if (sharedPreferences.getBoolean("enableWiFiTimer", false)) {
						Utils.SetAlarm(MainApplication.getAppContext(),
								sharedPreferences.getInt("enableWiFiAfter", 30), Constants.ACTION_WIFI_CHANGE_ON,
								TurnONScheduler.class, sharedPreferences);
					}
				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_NFC_CHANGE)) {
					Intent mIntent = new Intent(Constants.ACTION_NFC);
					mIntent.putExtra(Constants.SCREEN_STATE, false);
					MainApplication.getAppContext().sendBroadcast(mIntent);

				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_GPS_CHANGE)) {
					if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2) {
						Intent mIntent = new Intent(Constants.ACTION_GPS);
						mIntent.putExtra(Constants.SCREEN_STATE, false);
						MainApplication.getAppContext().sendBroadcast(mIntent);
					}
				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_BLUETOOTH_CHANGE)) {
					Intent mIntent = new Intent(Constants.ACTION_BLUETOOTH);
					mIntent.putExtra(Constants.SCREEN_STATE, false);
					MainApplication.getAppContext().sendBroadcast(mIntent);

				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (wl != null && wl.isHeld()) {
				wl.release();
			}
		}

	}

}