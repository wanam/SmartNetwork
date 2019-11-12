package ma.wanam.smartnetwork;

import ma.wanam.smartnetwork.utils.Constants;
import ma.wanam.smartnetwork.utils.Utils;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

public class TurnONScheduler extends BroadcastReceiver {

	@SuppressLint({ "Wakelock", "WorldReadableFiles" })
	@Override
	public void onReceive(Context context, Intent intent) {

		PowerManager.WakeLock wl = null;

		try {

			if (!Utils.getScreenState(MainApplication.getAppContext())) {
				wl = Utils.getPm(MainApplication.getAppContext()).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						MainApplication.getAppContext().getClass().getName());
				wl.acquire();

				Bundle sharedPreferences = intent.getExtras();

				if (intent.getAction().equalsIgnoreCase(Constants.ACTION_MOBILE_DATA_ON)
						&& sharedPreferences.getBoolean("enableDataTimer", false)) {
					Intent mIntent = new Intent(Constants.ACTION_NETWORK);
					mIntent.putExtra(Constants.SCREEN_STATE, true);
					MainApplication.getAppContext().sendBroadcast(mIntent);

					Utils.SetAlarm(MainApplication.getAppContext(), sharedPreferences.getInt("enableDataFor", 5),
							Constants.ACTION_MOBILE_DATA, TurnOffScheduler.class, sharedPreferences);

				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_SYNC_DATA_ON)
						&& sharedPreferences.getBoolean("enableSyncTimer", false)) {

					Intent mIntent = new Intent(Constants.ACTION_SYNC);
					mIntent.putExtra(Constants.SCREEN_STATE, true);
					MainApplication.getAppContext().sendBroadcast(mIntent);

					Utils.SetAlarm(MainApplication.getAppContext(), sharedPreferences.getInt("enableSyncFor", 5),
							Constants.ACTION_SYNC_DATA, TurnOffScheduler.class, sharedPreferences);

				} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_WIFI_CHANGE_ON)
						&& sharedPreferences.getBoolean("enableWiFiTimer", false)) {

					Intent mIntent = new Intent(Constants.ACTION_WIFI);
					mIntent.putExtra(Constants.SCREEN_STATE, true);
					MainApplication.getAppContext().sendBroadcast(mIntent);

					Utils.SetAlarm(MainApplication.getAppContext(), sharedPreferences.getInt("enableWiFiFor", 5),
							Constants.ACTION_WIFI_CHANGE, TurnOffScheduler.class, sharedPreferences);

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