package ma.wanam.smartnetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ma.wanam.smartnetwork.utils.Constants;
import ma.wanam.smartnetwork.utils.Utils;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XScreen {

	private static Context mContext;
	private static boolean bootCompleted = false;
	private static XSharedPreferences xPrefs = null;
	private static List<RunningTaskInfo> list;
	private static WifiManager wifiManager = null;
	private static boolean isWiFiActivated = true;
	private static boolean isBTActivated = true;
	private static boolean isGPSActivated = true;
	private static boolean isNFCActivated = true;
	private static boolean isSyncActivated = true;
	private static boolean canUpdateStates = true;
	private static Boolean wifiPreviousState = null;

	private static Class<?> mPhoneFactory;
	private static Context mContextPhone;
	private static boolean DEBUG = BuildConfig.DEBUG;

	private static void log(String msg) {
		if (DEBUG)
			XposedBridge.log(msg);
	}

	public static Context getmContext() {
		return (mContextPhone == null ? mContext : mContextPhone);
	}

	private static boolean isDataActivated = true;

	private static Context smartContext = null;

	public static WifiManager getWifiManager() {
		try {
			if (wifiManager == null && getmContext() != null) {
				wifiManager = (WifiManager) getmContext().getSystemService(Context.WIFI_SERVICE);
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return wifiManager;
	}

	private static NfcAdapter nfcAdapter = null;

	public static NfcAdapter getNfcAdapter() {
		try {
			if (nfcAdapter == null && getmContext() != null) {
				nfcAdapter = NfcAdapter.getDefaultAdapter(getmContext());
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return nfcAdapter;
	}

	private static BluetoothAdapter bluetoothAdapter = null;

	public static BluetoothAdapter getBluetoothAdapter() {
		try {
			if (bluetoothAdapter == null) {
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return bluetoothAdapter;
	}

	private static ArrayList<BluetoothDevice> btDevices = new ArrayList<BluetoothDevice>();

	private static final BroadcastReceiver btReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();

				if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
					btDevices.add((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
				} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					if (btDevices.contains((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))) {
						btDevices.remove((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
					}
				}
			} catch (Throwable e) {
				XposedBridge.log(e);
			}
		}
	};

	@SuppressLint("InlinedApi")
	private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				XScreen.xPrefs.reload();
				if (XScreen.xPrefs.getBoolean("masterEnabled", true)) {

					if (intent.getAction().equalsIgnoreCase(Constants.ACTION_WIFI)) {

						boolean screenON = intent.getBooleanExtra(Constants.SCREEN_STATE, true);
						if (getWifiManager() != null) {

							if (isWiFiActivated || !xPrefs.getBoolean("keepManualSettings", false)) {

								String pkg = null;
								if (!XScreen.xPrefs.getString("enableWiFiWhiteList", "").trim().equalsIgnoreCase("")) {
									List<String> whiteList = Arrays.asList(XScreen.xPrefs
											.getString("enableWiFiWhiteList", "").trim().split(";"));
									ActivityManager am = null;
									try {
										am = (ActivityManager) getmContext().getSystemService(Context.ACTIVITY_SERVICE);
										list = am.getRunningTasks(Integer.MAX_VALUE);
										if (list != null && list.size() > 0) {
											for (RunningTaskInfo rti : list) {
												pkg = rti.topActivity.getPackageName();

												if (whiteList.contains(pkg)) {
													return;
												}
											}
										}
									} catch (Throwable e) {
										XposedBridge.log(e);
									}

									try {
										List<ActivityManager.RunningServiceInfo> rs = am
												.getRunningServices(Integer.MAX_VALUE);
										for (ActivityManager.RunningServiceInfo rsi : rs) {
											pkg = rsi.service.getPackageName();
											if (whiteList.contains(pkg)) {
												return;
											}
										}
									} catch (Throwable e) {
										XposedBridge.log(e);
									}

									try {
										am = (ActivityManager) getSmartContext().getSystemService(
												Context.ACTIVITY_SERVICE);
										list = am.getRunningTasks(Integer.MAX_VALUE);
										if (list != null && list.size() > 0) {
											for (RunningTaskInfo rti : list) {
												pkg = rti.topActivity.getPackageName();

												if (whiteList.contains(pkg)) {
													return;
												}
											}
										}
									} catch (Throwable e) {
										XposedBridge.log(e);
									}

									try {
										List<ActivityManager.RunningServiceInfo> rs = am
												.getRunningServices(Integer.MAX_VALUE);
										for (ActivityManager.RunningServiceInfo rsi : rs) {
											pkg = rsi.service.getPackageName();
											if (whiteList.contains(pkg)) {
												return;
											}
										}
									} catch (Throwable e) {
										XposedBridge.log(e);
									}

								}

								WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
								if (!XScreen.xPrefs.getString("wifi_ssid_whitelist", "").trim().equalsIgnoreCase("")
										&& getWifiManager().isWifiEnabled() && wifiInfo != null) {
									try {
										String wifiAP = wifiInfo.getSSID();
										String whiteList = XScreen.xPrefs.getString("wifi_ssid_whitelist", "");

										List<String> wlist = Arrays.asList(whiteList.trim().split(";"));

										// start adding the whitelisted
										// enstries
										for (String wp : wlist) {
											if (wp != null && !wp.equalsIgnoreCase("")) {
												String entry = Utils.stripLeadingAndTrailingQuotes(wp).trim();
												if (entry.equalsIgnoreCase(Utils.stripLeadingAndTrailingQuotes(wifiAP)
														.trim())) {
													return;
												}
											}
										}

									} catch (Throwable e) {
										XposedBridge.log(e);
									}
								}

								if (XScreen.xPrefs.getBoolean("wifiDisconnect", false)) {

									if (screenON) {
										getWifiManager().reconnect();
									} else {
										getWifiManager().disconnect();
									}
								} else if (XScreen.xPrefs.getBoolean("wifiToggle", false)) {
									getWifiManager().setWifiEnabled(screenON);
								}
							}

						}

					} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_NFC)) {

						if (isNFCActivated || !xPrefs.getBoolean("keepManualSettings", false)) {
							boolean screenON = intent.getBooleanExtra(Constants.SCREEN_STATE, true);

							if (getNfcAdapter() != null) {

								if (XScreen.xPrefs.getBoolean("NFCTurnOff", false)) {
									if (screenON) {
										XposedHelpers.callMethod(getNfcAdapter(), "enable");
									} else {
										XposedHelpers.callMethod(getNfcAdapter(), "disable");
									}
								}
							}
						}

					} else if ((isGPSActivated || !xPrefs.getBoolean("keepManualSettings", false))
							&& intent.getAction().equalsIgnoreCase(Constants.ACTION_GPS)) {

						if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2) {
							boolean screenON = intent.getBooleanExtra(Constants.SCREEN_STATE, true);

							if (screenON) {
								Settings.Secure.putInt(getmContext().getContentResolver(),
										Settings.Secure.LOCATION_MODE, XScreen.xPrefs.getInt("GPSScreenONMode",
												Settings.Secure.LOCATION_MODE_BATTERY_SAVING));
							} else {
								Settings.Secure.putInt(getmContext().getContentResolver(),
										Settings.Secure.LOCATION_MODE,
										XScreen.xPrefs.getInt("GPSScreenOffMode", Settings.Secure.LOCATION_MODE_OFF));
							}

							getmContext().sendBroadcast(new Intent("android.settings.GPS_CHANGED"));
						}
					} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_BLUETOOTH)) {
						boolean screenON = intent.getBooleanExtra(Constants.SCREEN_STATE, true);

						if ((isBTActivated || !xPrefs.getBoolean("keepManualSettings", false))
								&& getBluetoothAdapter() != null) {

							if (XScreen.xPrefs.getBoolean("BTTurnOff", false)) {

								if (btDevices.size() > 0
										&& !XScreen.xPrefs.getString("enableBTWhiteList", "").trim()
												.equalsIgnoreCase("") && getBluetoothAdapter().isEnabled()) {
									try {

										String whiteList = XScreen.xPrefs.getString("enableBTWhiteList", "");
										List<String> wlist = Arrays.asList(whiteList.trim().split(";"));

										for (BluetoothDevice bd : btDevices) {
											if (wlist.contains(bd.getName()))
												return;
										}

									} catch (Throwable e) {
										XposedBridge.log(e);
									}
								}

								if (screenON) {
									getBluetoothAdapter().enable();
								} else {
									getBluetoothAdapter().disable();
								}
							}
						}

					} else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_USER_PRESENT)) {
						if (XScreen.xPrefs.getBoolean("waitUntilUnlock", false)) {
							handleScreenState(null, null, true, true);
						}
					}
				}
			} catch (Throwable e) {
				XposedBridge.log(e);
			}

		}
	};

	private static AlarmManager am = null;

	public static AlarmManager getAm() {
		return (am == null ? am = (AlarmManager) getmContext().getSystemService(Context.ALARM_SERVICE) : am);
	}

	public static Context getSmartContext() {
		if (smartContext == null && getmContext() != null) {
			try {
				smartContext = getmContext().createPackageContext(Constants.SMART_NETWORK,
						Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
			} catch (Throwable e) {
				XposedBridge.log(e);
			}
		}
		return smartContext;
	}

	public static void initZygote(final ClassLoader classLoader, final XSharedPreferences prefs) {
		try {
			XScreen.xPrefs = prefs;

			try {

				if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {

					final Class<?> mPowerManagerService = XposedHelpers.findClass(
							"com.android.server.PowerManagerService", classLoader);

					final Class<?> mLightsService = XposedHelpers.findClass("com.android.server.LightsService",
							classLoader);

					final Class<?> mIActivityManager = XposedHelpers.findClass("android.app.IActivityManager",
							classLoader);

					final Class<?> mBatteryService = XposedHelpers.findClass("com.android.server.BatteryService",
							classLoader);

					XposedHelpers.findAndHookMethod(mPowerManagerService, "init", Context.class, mLightsService,
							mIActivityManager, mBatteryService, new XC_MethodHook() {

								protected void afterHookedMethod(MethodHookParam param) throws Throwable {
									initContext(param, 0);
								};
							});

					XposedHelpers.findAndHookMethod(mPowerManagerService, "setPowerState", int.class, boolean.class,
							int.class, new XC_MethodHook() {

								protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

									handleScreenState(methodHookParam, 0, null, false);
								}

							});

				} else if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {

					final Class<?> mDisplayPowerController = XposedHelpers.findClass(
							"com.android.server.power.DisplayPowerController", classLoader);
					XposedBridge.hookAllConstructors(mDisplayPowerController, new XC_MethodHook() {

						protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
							initContext(methodHookParam, 1);
						}

					});

					XposedHelpers.findAndHookMethod(mDisplayPowerController, "setScreenOn", boolean.class,
							new XC_MethodHook() {

								protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

									handleScreenState(methodHookParam, 1, null, false);
								}

							});

				} else if (VERSION.SDK_INT < VERSION_CODES.M) {
					final Class<?> mDisplayPowerController = XposedHelpers.findClass(
							"com.android.server.display.DisplayPowerController", classLoader);
					XposedBridge.hookAllConstructors(mDisplayPowerController, new XC_MethodHook() {

						protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
							initContext(methodHookParam, 0);
						}

					});

					XposedHelpers.findAndHookMethod(mDisplayPowerController, "setScreenState", int.class,
							new XC_MethodHook() {

								protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
									handleScreenState(methodHookParam, 2, null, false);
								}

							});
				} else {
					final Class<?> mDisplayPowerController = XposedHelpers.findClass(
							"com.android.server.display.DisplayPowerController", classLoader);
					XposedBridge.hookAllConstructors(mDisplayPowerController, new XC_MethodHook() {

						protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
							initContext(methodHookParam, 0);
						}

					});

					final Class<?> mDisplayPowerState = XposedHelpers.findClass(
							"com.android.server.display.DisplayPowerState", classLoader);
					XposedHelpers.findAndHookMethod(mDisplayPowerState, "setScreenState", int.class,
							new XC_MethodHook() {

								protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
									handleScreenState(methodHookParam, 2, null, false);
								}

							});
				}

			} catch (Throwable e1) {
				XposedBridge.log(e1);
			}

			try {
				final Class<?> mNfcAdapter = XposedHelpers.findClass("android.nfc.NfcAdapter", classLoader);
				XposedHelpers.findAndHookMethod(mNfcAdapter, "getDefaultAdapter", Context.class, new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						try {
							Context cn = (Context) param.args[0];
							if (cn == getmContext()) {
								NfcManager manager = (NfcManager) cn.getSystemService(Context.NFC_SERVICE);
								param.setResult((manager == null ? null : manager.getDefaultAdapter()));
								return;
							}
						} catch (Throwable e) {
							XposedBridge.log(e);
						}

					}
				});
			} catch (Throwable e1) {
				XposedBridge.log(e1);
			}

			try {
				final Class<?> mNfcManager = XposedHelpers.findClass("android.nfc.NfcManager", classLoader);
				XposedBridge.hookAllConstructors(mNfcManager, new XC_MethodHook() {

					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						try {
							Context cn = (Context) param.args[0];
							if (cn == getmContext()) {
								NfcAdapter adapter = (NfcAdapter) XposedHelpers.callStaticMethod(NfcAdapter.class,
										"getNfcAdapter", cn);
								XposedHelpers.setObjectField(param.thisObject, "mAdapter", adapter);
								param.setResult(adapter);
								return;
							}
						} catch (Throwable e) {
							XposedBridge.log(e);
						}

					}
				});
			} catch (Throwable e1) {
				XposedBridge.log(e1);
			}

		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	@SuppressLint("InlinedApi")
	private static void handleScreenState(MethodHookParam methodHookParam, Integer oldImpl, Boolean screenON,
			Boolean fromKeyGuard) {
		final int SCREEN_OFF = 0x00000000;
		XScreen.xPrefs.reload();
		boolean waitUntilUnlock = XScreen.xPrefs.getBoolean("waitUntilUnlock", false);

		try {
			XScreen.xPrefs.reload();

			boolean isScreeON = Boolean.FALSE;
			if (methodHookParam != null) {

				switch (oldImpl) {
				case 0:
					isScreeON = ((Integer) methodHookParam.args[0] != SCREEN_OFF);
					break;

				case 1:
					isScreeON = (Boolean) methodHookParam.args[0];
					break;
				case 2:
					isScreeON = ((Integer) methodHookParam.args[0] != Display.STATE_OFF);
					break;

				default:
					break;
				}

			} else {
				isScreeON = screenON.booleanValue();
			}

			if (XScreen.xPrefs.getBoolean("masterEnabled", true) && bootCompleted && getmContext() != null
					&& !Utils.isAirplaneModeOn(getmContext())) {

				if (!isScreeON || !waitUntilUnlock || (isScreeON && waitUntilUnlock && fromKeyGuard)) {
					new checkServicesTask().execute(isScreeON);
				}
			}

			if (isScreeON && XScreen.xPrefs.getBoolean("waitUntilUnlock", false) && !fromKeyGuard) {
				canUpdateStates = false;
			} else {
				canUpdateStates = true;
			}

		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	public static class checkServicesTask extends AsyncTask<Boolean, Void, Void> {
		private boolean isScreeON = true;

		@SuppressLint("InlinedApi")
		@Override
		protected Void doInBackground(Boolean... params) {

			try {
				isScreeON = params[0];
				if (xPrefs.getBoolean("keepManualSettings", false) && !isScreeON) {
					if (canUpdateStates) {
						int state = getWifiManager().getWifiState();
						isWiFiActivated = (state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING);
						log("state isWiFiActivated:" + isWiFiActivated);

						state = getBluetoothAdapter().getState();
						isBTActivated = (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_TURNING_ON);
						log("state isBTActivated:" + isBTActivated);

						isDataActivated = isMobileDataEnabled();
						log("state isDataActivated:" + isDataActivated);

						if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2) {
							isGPSActivated = Settings.Secure.getInt(getmContext().getContentResolver(),
									Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_OFF;
							log("state isGPSActivated:" + isGPSActivated);
						}

						isNFCActivated = (getNfcAdapter() != null) && getNfcAdapter().isEnabled();
						log("state isNFCActivated:" + isNFCActivated);

						isSyncActivated = ContentResolver.getMasterSyncAutomatically();
						log("state isSyncActivated:" + isSyncActivated);
					}
				}

			} catch (Throwable e) {
				XposedBridge.log(e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				if (XScreen.xPrefs.getBoolean("smart2GEnabled", false)) {
					checkAndSendMobileIntent(isScreeON);
				}

				if (XScreen.xPrefs.getBoolean("wifiDisconnect", false)
						|| XScreen.xPrefs.getBoolean("wifiToggle", false)) {
					checkAndSendWiFiIntent(isScreeON);
				}

				checkAndSendNFCIntent(isScreeON);

				if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2) {
					checkAndSendGPSIntent(isScreeON);
				}

				checkAndSendBTIntent(isScreeON);
			} catch (Throwable e) {
				XposedBridge.log(e);
			}
			super.onPostExecute(result);
		}

	}

	private static void initContext(MethodHookParam methodHookParam, int index) {
		try {
			mContext = (Context) methodHookParam.args[index];
			if (getmContext() != null) {
				getmContext().registerReceiver(new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
							bootCompleted = true;
							getmContext().unregisterReceiver(this);
						}

					}
				}, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));

				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(Constants.ACTION_WIFI);
				intentFilter.addAction(Constants.ACTION_NFC);
				intentFilter.addAction(Constants.ACTION_GPS);
				intentFilter.addAction(Constants.ACTION_BLUETOOTH);
				intentFilter.addAction(Intent.ACTION_USER_PRESENT);
				getmContext().registerReceiver(broadcastReceiver, intentFilter);

				IntentFilter iFilter = new IntentFilter();
				iFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
				iFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
				getmContext().registerReceiver(btReceiver, iFilter);
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static void checkAndSendBTIntent(boolean isScreeON) {
		int delay;
		try {
			XScreen.xPrefs.reload();
			if (XScreen.xPrefs.getBoolean("BTTurnOff", false) && getBluetoothAdapter() != null) {
				delay = XScreen.xPrefs.getInt("delayBeforeBTOff", 0);
				if ((delay == 0 && !isScreeON) || isScreeON) {
					Intent mIntent = new Intent(Constants.ACTION_BLUETOOTH);
					mIntent.putExtra(Constants.SCREEN_STATE, isScreeON);
					getSmartContext().sendBroadcast(mIntent);
				} else {
					SetAlarm(delay, Constants.ACTION_BLUETOOTH_CHANGE);
				}
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static void checkAndSendNFCIntent(boolean isScreeON) {
		int delay;
		try {
			XScreen.xPrefs.reload();
			if (XScreen.xPrefs.getBoolean("NFCTurnOff", false) && getNfcAdapter() != null) {
				delay = XScreen.xPrefs.getInt("delayBeforeNFCOff", 0);
				if ((delay == 0 && !isScreeON) || isScreeON) {
					Intent mIntent = new Intent(Constants.ACTION_NFC);
					mIntent.putExtra(Constants.SCREEN_STATE, isScreeON);
					getSmartContext().sendBroadcast(mIntent);

				} else {
					SetAlarm(delay, Constants.ACTION_NFC_CHANGE);
				}
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	@SuppressWarnings("deprecation")
	private static void checkAndSendGPSIntent(boolean isScreeON) {
		int delay;
		String pkg;
		try {
			XScreen.xPrefs.reload();
			if (XScreen.xPrefs.getBoolean("GPSTurnOff", true)) {

				if (!XScreen.xPrefs.getString("enableGPSWhiteList", "").trim().equalsIgnoreCase("")) {
					List<String> whiteList = Arrays.asList(XScreen.xPrefs.getString("enableGPSWhiteList", "").trim()
							.split(";"));

					ActivityManager am = ((ActivityManager) getmContext().getSystemService(Context.ACTIVITY_SERVICE));
					try {
						list = am.getRunningTasks(Integer.MAX_VALUE);
						if (list != null && list.size() > 0) {
							for (RunningTaskInfo rti : list) {

								pkg = rti.topActivity.getPackageName();
								if (whiteList.contains(pkg)) {
									return;
								}
							}
						}
					} catch (Throwable e) {
						XposedBridge.log(e);
					}

					try {
						List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
						for (ActivityManager.RunningServiceInfo rsi : rs) {
							pkg = rsi.service.getPackageName();
							if (whiteList.contains(pkg)) {
								return;
							}
						}
					} catch (Throwable e) {
						XposedBridge.log(e);
					}

					try {
						am = (ActivityManager) getSmartContext().getSystemService(Context.ACTIVITY_SERVICE);
						list = am.getRunningTasks(Integer.MAX_VALUE);
						if (list != null && list.size() > 0) {
							for (RunningTaskInfo rti : list) {
								pkg = rti.topActivity.getPackageName();
								if (whiteList.contains(pkg)) {
									return;
								}
							}
						}
					} catch (Throwable e) {
						XposedBridge.log(e);
					}

					try {
						List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
						for (ActivityManager.RunningServiceInfo rsi : rs) {
							pkg = rsi.service.getPackageName();
							if (whiteList.contains(pkg)) {
								return;
							}
						}
					} catch (Throwable e) {
						XposedBridge.log(e);
					}

				}

				delay = XScreen.xPrefs.getInt("delayBeforeGPSOff", 0);
				if ((delay == 0 && !isScreeON) || isScreeON) {
					Intent mIntent = new Intent(Constants.ACTION_GPS);
					mIntent.putExtra(Constants.SCREEN_STATE, isScreeON);
					getSmartContext().sendBroadcast(mIntent);

				} else {
					SetAlarm(delay, Constants.ACTION_GPS_CHANGE);
				}
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static void checkAndSendWiFiIntent(boolean isScreeON) {
		int delay;
		try {
			XScreen.xPrefs.reload();
			if ((getWifiManager() != null)
					&& (!Utils.isCharging(getmContext()) || !xPrefs.getBoolean("keepWiFiONCharging", false) || isScreeON)) {

				delay = XScreen.xPrefs.getInt("delayBeforeWiFiOff", 0);
				if ((delay == 0 && !isScreeON) || isScreeON) {
					Intent mIntent = new Intent(Constants.ACTION_WIFI);
					mIntent.putExtra(Constants.SCREEN_STATE, isScreeON);
					getSmartContext().sendBroadcast(mIntent);

					if (!isScreeON) {
						Utils.SetAlarm(getSmartContext(), xPrefs.getInt("enableWiFiAfter", 30),
								Constants.ACTION_WIFI_CHANGE_ON, TurnONScheduler.class, getIntentBundle());
					}
				} else {
					SetAlarm(delay, Constants.ACTION_WIFI_CHANGE);
				}

			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static Bundle getIntentBundle() {
		Intent intent = new Intent();
		try {
			XScreen.xPrefs.reload();
			intent.putExtra("enableDataTimer", XScreen.xPrefs.getBoolean("enableDataTimer", false));
			intent.putExtra("enableDataAfter", XScreen.xPrefs.getInt("enableDataAfter", 30));
			intent.putExtra("enableDataFor", XScreen.xPrefs.getInt("enableDataFor", 5));
			intent.putExtra("enableSyncTimer", XScreen.xPrefs.getBoolean("enableSyncTimer", false));
			intent.putExtra("enableSyncAfter", XScreen.xPrefs.getInt("enableSyncAfter", 30));
			intent.putExtra("enableSyncFor", XScreen.xPrefs.getInt("enableSyncFor", 5));
			intent.putExtra("enableWiFiTimer", XScreen.xPrefs.getBoolean("enableWiFiTimer", false));
			intent.putExtra("enableWiFiAfter", XScreen.xPrefs.getInt("enableWiFiAfter", 30));
			intent.putExtra("enableWiFiFor", XScreen.xPrefs.getInt("enableWiFiFor", 5));
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return intent.getExtras();
	}

	private static void checkAndSendMobileIntent(boolean isScreeON) {
		int delay;
		try {
			XScreen.xPrefs.reload();
			if (isDataActivated || !XScreen.xPrefs.getBoolean("keepManualSettings", false)) {
				if ((!XScreen.xPrefs.getBoolean("smart2GONWiFiEnabled", true) || !isScreeON || !Utils
						.isWiFiConnected(getmContext()))
						&& (!Utils.isCharging(getmContext()) || !XScreen.xPrefs.getBoolean("keepDataONCharging", false) || isScreeON)) {
					delay = XScreen.xPrefs.getInt("delayBeforeMobileOff", 0);
					if ((delay == 0 && !isScreeON) || isScreeON) {
						sendNetworkChange(isScreeON);

						if (!isScreeON) {
							Utils.SetAlarm(getSmartContext(), XScreen.xPrefs.getInt("enableDataAfter", 30),
									Constants.ACTION_MOBILE_DATA_ON, TurnONScheduler.class, getIntentBundle());
						}
					} else {
						SetAlarm(delay, Constants.ACTION_MOBILE_DATA);
					}
				}

				if (XScreen.xPrefs.getBoolean("disableSync", false)
						&& (!Utils.isCharging(getmContext()) || !XScreen.xPrefs.getBoolean("keepSyncONCharging", false) || isScreeON)) {

					if (isSyncActivated || !xPrefs.getBoolean("keepManualSettings", false)) {
						delay = XScreen.xPrefs.getInt("delayBeforeSyncOff", 0);
						if ((delay == 0 && !isScreeON) || isScreeON) {
							ContentResolver.setMasterSyncAutomatically(isScreeON);

							if (!isScreeON) {
								Utils.SetAlarm(getSmartContext(), XScreen.xPrefs.getInt("enableSyncAfter", 30),
										Constants.ACTION_SYNC_DATA_ON, TurnONScheduler.class, getIntentBundle());
							}
						} else {
							SetAlarm(delay, Constants.ACTION_SYNC_DATA);
						}
					}
				}
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static void sendNetworkChange(boolean isScreeON) {
		Intent mIntent = new Intent(Constants.ACTION_NETWORK);
		mIntent.putExtra(Constants.SCREEN_STATE, isScreeON);
		getSmartContext().sendBroadcast(mIntent);
	}

	@SuppressLint("NewApi")
	private static void SetAlarm(int delay, String action) {
		try {
			long nextTrigger = System.currentTimeMillis() + (delay * 60 * 1000);

			Intent intent = new Intent(getSmartContext(), TurnOffScheduler.class);
			intent.putExtras(getIntentBundle());
			intent.setAction(action);

			PendingIntent pi = PendingIntent.getBroadcast(getSmartContext(), 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP_MR1) {
				getAm().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTrigger, pi);
			} else if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2) {
				getAm().setExact(AlarmManager.RTC_WAKEUP, nextTrigger, pi);
			} else {
				getAm().set(AlarmManager.RTC_WAKEUP, nextTrigger, pi);
			}

		} catch (Throwable e) {
			XposedBridge.log(e);
		}
	}

	private static boolean isMobileDataEnabled() {
		boolean mobileDataEnabled = false;

		try {
			if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP_MR1) {
				ConnectivityManager cm = (ConnectivityManager) getmContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
				mobileDataEnabled = (Boolean) XposedHelpers.callMethod(cm, "getMobileDataEnabled");
			} else {
				TelephonyManager tm = (TelephonyManager) getmContext().getSystemService(Context.TELEPHONY_SERVICE);
				mobileDataEnabled = (Boolean) XposedHelpers.callMethod(tm, "getDataEnabled");
			}
		} catch (Throwable e) {
			XposedBridge.log(e);
		}
		return mobileDataEnabled;
	}

	private static BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				XScreen.xPrefs.reload();
				if (XScreen.xPrefs.getBoolean("masterEnabled", true)) {

					if (intent.getAction().equalsIgnoreCase(Constants.ACTION_NETWORK)) {
						boolean screenON = intent.getBooleanExtra(Constants.SCREEN_STATE, true);
						checkAndUpdateMobileData(context, screenON);
					} else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_SYNC)
							&& xPrefs.getBoolean("disableSync", false)) {
						boolean screenON = intent.getBooleanExtra(Constants.SCREEN_STATE, true);
						ContentResolver.setMasterSyncAutomatically(screenON);
					} else if (intent.getAction().equalsIgnoreCase(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
						NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
						if (info != null) {
							boolean isDisconnected = info.getState() != State.CONNECTED;
							if (wifiPreviousState != null && xPrefs.getBoolean("smart2GONWiFiEnabled", true)
									&& wifiPreviousState != isDisconnected) {
								if (Utils.getScreenState(context) || !xPrefs.getBoolean("smart2GEnabled", false)) {
									log("is wifi disconnected = " + Boolean.toString(isDisconnected));
									checkAndUpdateMobileData(context, isDisconnected);
								}
							}
							wifiPreviousState = isDisconnected;
						}
					}
				}

			} catch (Throwable e) {
				XposedBridge.log(e);
			}
		}

	};

	@SuppressWarnings("deprecation")
	private static void checkAndUpdateMobileData(Context context, boolean screenON) {
		String pkg;
		boolean isWiFiAPActive = false;

		XScreen.xPrefs.reload();

		if (isDataActivated || !XScreen.xPrefs.getBoolean("keepManualSettings", false)) {
			if (!XScreen.xPrefs.getString("enableMobileWhiteList", "").trim().equalsIgnoreCase("")) {
				List<String> whiteList = Arrays.asList(XScreen.xPrefs.getString("enableMobileWhiteList", "").trim()
						.split(";"));

				ActivityManager am = ((ActivityManager) getmContext().getSystemService(Context.ACTIVITY_SERVICE));
				try {
					list = am.getRunningTasks(Integer.MAX_VALUE);
					if (list != null && list.size() > 0) {
						for (RunningTaskInfo rti : list) {

							pkg = rti.topActivity.getPackageName();
							if (whiteList.contains(pkg)) {
								return;
							}
						}
					}
				} catch (Throwable e) {
					XposedBridge.log(e);
				}

				try {
					List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
					for (ActivityManager.RunningServiceInfo rsi : rs) {
						pkg = rsi.service.getPackageName();
						if (whiteList.contains(pkg)) {
							return;
						}
					}
				} catch (Throwable e) {
					XposedBridge.log(e);
				}

				try {
					am = (ActivityManager) getSmartContext().getSystemService(Context.ACTIVITY_SERVICE);
					list = am.getRunningTasks(Integer.MAX_VALUE);
					if (list != null && list.size() > 0) {
						for (RunningTaskInfo rti : list) {
							pkg = rti.topActivity.getPackageName();
							if (whiteList.contains(pkg)) {
								return;
							}
						}
					}
				} catch (Throwable e) {
					XposedBridge.log(e);
				}

				try {
					List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
					for (ActivityManager.RunningServiceInfo rsi : rs) {
						pkg = rsi.service.getPackageName();
						if (whiteList.contains(pkg)) {
							return;
						}
					}
				} catch (Throwable e) {
					XposedBridge.log(e);
				}
			}

			try {
				isWiFiAPActive = XScreen.xPrefs.getBoolean("keepONWiFiAP", true)
						&& (Boolean) XposedHelpers.callMethod(getWifiManager(), "isWifiApEnabled");
			} catch (Throwable e) {
				XposedBridge.log(e);
			}

			if (!isWiFiAPActive && !(XScreen.xPrefs.getBoolean("keepONCall", true) && Utils.isCallActive(context))) {
				if (XScreen.xPrefs.getBoolean("smart2GEnabled", false)
						|| XScreen.xPrefs.getBoolean("smart2GONWiFiEnabled", true)) {
					if ((XScreen.xPrefs.getInt(Constants.NETWORK_MODE_OFF, Constants.DATA_OFF) < 99)
							&& (XScreen.xPrefs.getInt(Constants.NETWORK_MODE_ON, Constants.NETWORK_MODE_LTE_GSM_WCDMA) < 99)) {
						setPreferredNetworkType((screenON ? (XScreen.xPrefs.getInt(Constants.NETWORK_MODE_ON,
								Constants.NETWORK_MODE_LTE_GSM_WCDMA)) : XScreen.xPrefs.getInt(
								Constants.NETWORK_MODE_OFF, Constants.DATA_OFF)));
					} else {
						try {
							if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP_MR1) {
								ConnectivityManager cm = (ConnectivityManager) getmContext().getSystemService(
										Context.CONNECTIVITY_SERVICE);
								XposedHelpers.callMethod(cm, "setMobileDataEnabled", screenON);
							} else {
								TelephonyManager tm = (TelephonyManager) getmContext().getSystemService(
										Context.TELEPHONY_SERVICE);
								XposedHelpers.callMethod(tm, "setDataEnabled", screenON);
							}
						} catch (Throwable e) {
							XposedBridge.log(e);
						} finally {
							if ((screenON && (XScreen.xPrefs.getInt(Constants.NETWORK_MODE_ON,
									Constants.NETWORK_MODE_LTE_GSM_WCDMA) < 99))
									|| (!screenON && XScreen.xPrefs.getInt(Constants.NETWORK_MODE_OFF,
											Constants.DATA_OFF) < 99)) {
								setPreferredNetworkType(XScreen.xPrefs.getInt(Constants.NETWORK_MODE_ON,
										Constants.NETWORK_MODE_LTE_GSM_WCDMA));
							}
						}

					}
				}

			}
		}

	}

	public static void doHookPhone(ClassLoader classLoader, XSharedPreferences prefs) {

		try {
			XScreen.xPrefs = prefs;
			mPhoneFactory = XposedHelpers.findClass("com.android.internal.telephony.PhoneFactory", classLoader);
			Class<?> PhoneGlobals = XposedHelpers.findClass("com.android.phone.PhoneGlobals", classLoader);
			XposedHelpers.findAndHookMethod(PhoneGlobals, "onCreate", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
					try {

						if (mContextPhone == null) {
							mContextPhone = (Context) param.thisObject;
							if (mContextPhone != null) {
								IntentFilter intentFilter = new IntentFilter();
								intentFilter.addAction(Constants.ACTION_NETWORK);
								intentFilter.addAction(Constants.ACTION_SYNC);
								intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
								mContextPhone.registerReceiver(mBroadcastReceiver, intentFilter);

							}
						}
					} catch (Throwable e) {
						XposedBridge.log(e);
					}
				}
			});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	@SuppressLint("NewApi")
	private static void setPreferredNetworkType(int networkMode) {
		try {

			if (VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN) {
				Settings.Global.putInt(mContextPhone.getContentResolver(), Constants.PREFERRED_NETWORK_MODE,
						networkMode);
			} else {
				Settings.Secure.putInt(mContextPhone.getContentResolver(), Constants.PREFERRED_NETWORK_MODE,
						networkMode);
			}

			Object defPhone = XposedHelpers.callStaticMethod(mPhoneFactory, "getDefaultPhone");
			if (defPhone != null) {

				if (Utils.hasGeminiSupport()) {
					int mSimSlot = (Integer) XposedHelpers.callMethod(defPhone, "get3GSimId");
					Class<?>[] paramArgs = new Class<?>[3];
					paramArgs[0] = int.class;
					paramArgs[1] = Message.class;
					paramArgs[2] = int.class;
					XposedHelpers.callMethod(defPhone, "setPreferredNetworkTypeGemini", paramArgs, networkMode, null,
							mSimSlot);

				} else {
					Class<?>[] paramArgs = new Class<?>[2];
					paramArgs[0] = int.class;
					paramArgs[1] = Message.class;
					XposedHelpers.callMethod(defPhone, "setPreferredNetworkType", paramArgs, networkMode, null);
				}
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

}