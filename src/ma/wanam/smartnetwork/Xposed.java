package ma.wanam.smartnetwork;

import ma.wanam.smartnetwork.utils.Constants;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Xposed implements IXposedHookZygoteInit, IXposedHookLoadPackage {

	public static final String ANDROID = "android";
	public static final String PHONE = "com.android.phone";
	private static XSharedPreferences prefs;

	@Override
	public void initZygote(StartupParam startupParam) {

		try {
			prefs = new XSharedPreferences(Constants.SMART_NETWORK, SmartNetworkActivity.class.getSimpleName());
		} catch (Throwable e) {
			XposedBridge.log(e);
		}

	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

		if (lpparam.packageName.equals(ANDROID)) {
			try {
				if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP_MR1) {
					XPM23.initZygote(lpparam.classLoader);
				} else if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
					XPM22.initZygote(lpparam.classLoader);
				} else {
					XPM21.initZygote(lpparam.classLoader);
				}
				XScreen.initZygote(lpparam.classLoader, prefs);

			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}

		if (lpparam.packageName.equals(PHONE)) {
			try {
				XScreen.doHookPhone(lpparam.classLoader, prefs);

			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}

		if (lpparam.packageName.equals(Constants.SMART_NETWORK)) {
			try {
				XposedHelpers.findAndHookMethod(Constants.SMART_NETWORK + ".XposedChecker", lpparam.classLoader,
						"isActive", XC_MethodReplacement.returnConstant(Boolean.TRUE));
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}

	}

}
