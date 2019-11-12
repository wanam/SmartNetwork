package ma.wanam.smartnetwork;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MainApplication extends Application {

	private static Context mContext;
	public static SharedPreferences sharedPreferences;

	public MainApplication() {
		super();
	}

	public static Context getAppContext() {
		return mContext;
	}

	public void onCreate() {

		super.onCreate();
		mContext = getApplicationContext();
	}

}