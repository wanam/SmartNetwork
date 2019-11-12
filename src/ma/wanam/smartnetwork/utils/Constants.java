package ma.wanam.smartnetwork.utils;

import android.annotation.SuppressLint;

public class Constants {

	public static int DATA_OFF = 99; /*
									 * DATA OFF
									 */

	public static int NETWORK_MODE_WCDMA_PREF = 0; /*
													 * GSM/WCDMA (WCDMA
													 * preferred)
													 */
	public static int NETWORK_MODE_GSM_ONLY = 1; /* GSM only */
	public static int NETWORK_MODE_WCDMA_ONLY = 2; /* WCDMA only */
	public static int NETWORK_MODE_GSM_UMTS = 3; /*
												 * GSM/WCDMA (auto mode,
												 * according to PRL) AVAILABLE
												 * Application Settings menu
												 */
	public static int NETWORK_MODE_CDMA = 4; /*
											 * CDMA and EvDo (auto mode,
											 * according to PRL) AVAILABLE
											 * Application Settings menu
											 */
	public static int NETWORK_MODE_CDMA_NO_EVDO = 5; /* CDMA only */
	public static int NETWORK_MODE_EVDO_NO_CDMA = 6; /* EvDo only */
	public static int NETWORK_MODE_GLOBAL = 7; /*
												 * GSM/WCDMA, CDMA, and EvDo
												 * (auto mode, according to PRL)
												 * AVAILABLE Application
												 * Settings menu
												 */
	public static int NETWORK_MODE_LTE_CDMA_EVDO = 8; /* LTE, CDMA and EvDo */
	public static int NETWORK_MODE_LTE_GSM_WCDMA = 9; /* LTE, GSM/WCDMA */
	public static int NETWORK_MODE_LTE_CMDA_EVDO_GSM_WCDMA = 10; /*
																 * LTE, CDMA,
																 * EvDo,
																 * GSM/WCDMA
																 */
	public static int NETWORK_MODE_LTE_ONLY = 11; /* LTE Only mode. */
	public static int NETWORK_MODE_LTE_WCDMA = 12; /* LTE/WCDMA */

	int NETWORK_MODE_TDSCDMA_ONLY = 13; /* TD-SCDMA only */
	int NETWORK_MODE_TDSCDMA_WCDMA = 14; /* TD-SCDMA and WCDMA */
	int NETWORK_MODE_LTE_TDSCDMA = 15; /* TD-SCDMA and LTE */
	int NETWORK_MODE_TDSCDMA_GSM = 16; /* TD-SCDMA and GSM */
	int NETWORK_MODE_LTE_TDSCDMA_GSM = 17; /* TD-SCDMA,GSM and LTE */
	int NETWORK_MODE_TDSCDMA_GSM_WCDMA = 18; /* TD-SCDMA, GSM/WCDMA */
	int NETWORK_MODE_LTE_TDSCDMA_WCDMA = 19; /* TD-SCDMA, WCDMA and LTE */
	int NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA = 20; /* TD-SCDMA, GSM/WCDMA and LTE */
	int NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 21; /*
														 * TD-SCDMA,EvDo,CDMA,GSM
														 * /WCDMA
														 */
	int NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 22; /*
															 * TD-SCDMA/LTE/GSM/
															 * WCDMA, CDMA, and
															 * EvDo
															 */

	public static final String PREFERRED_NETWORK_MODE = "preferred_network_mode";

	public static final String ACTION_NETWORK = "wanam.intent.action.NETWORK";
	public static final String ACTION_MOBILE_DATA = "ma.wanam.alarm.MOBILE_DATA_CHANGE";
	public static final String ACTION_MOBILE_DATA_ON = "ma.wanam.alarm.MOBILE_DATA_CHANGE_ON";

	public static final String ACTION_SYNC = "wanam.intent.action.SYNC";
	public static final String ACTION_SYNC_DATA = "ma.wanam.alarm.SYNC_DATA_CHANGE";
	public static final String ACTION_SYNC_DATA_ON = "ma.wanam.alarm.SYNC_DATA_CHANGE_ON";

	public static final String ACTION_WIFI = "wanam.intent.action.WIFI";
	public static final String ACTION_WIFI_CHANGE = "ma.wanam.alarm.WIFI_CHANGE";
	public static final String ACTION_WIFI_CHANGE_ON = "ma.wanam.alarm.WIFI_CHANGE_ON";

	public static final String ACTION_NFC = "wanam.intent.action.NFC";
	public static final String ACTION_NFC_CHANGE = "ma.wanam.alarm.NFC_CHANGE";

	public static final String ACTION_GPS = "wanam.intent.action.GPS";
	public static final String ACTION_GPS_CHANGE = "ma.wanam.alarm.GPS_CHANGE";

	public static final String ACTION_BLUETOOTH = "wanam.intent.action.BLUETOOTH";
	public static final String ACTION_BLUETOOTH_CHANGE = "ma.wanam.alarm.BLUETOOTH_CHANGE";

	public static final String NETWORK_MODE = "networkMode";
	public static final String SCREEN_STATE = "screenState";
	public static final String NETWORK_MODE_OFF = "networkModeOff";
	public static final String NETWORK_MODE_ON = "networkModeON";

	public static final String SMART_NETWORK = "ma.wanam.smartnetwork";
	public static final String PREFS = SMART_NETWORK + "_preferences";
	public static final String DONATE_URL = "";

	public static final String ACCESS_WIFI_STATE = "android.permission.ACCESS_WIFI_STATE";
	public static final String CHANGE_WIFI_STATE = "android.permission.CHANGE_WIFI_STATE";
	public static final String CHANGE_NETWORK_STATE = "android.permission.CHANGE_NETWORK_STATE";

	public static final String AD_ID = "";

	public static final String XPOSED_PACKAGE_NAME = "de.robv.android.xposed.installer";
	@SuppressLint("SdCardPath")
	public static final String XPOSED_FRAMEWORK_PATH = "/data/data/de.robv.android.xposed.installer/bin/XposedBridge.jar";
	@SuppressLint("SdCardPath")
	public static final String MODULES_WHITELIST_23 = "/data/data/de.robv.android.xposed.installer/conf/modules.list";
	public static final String TRANSCOMMU_ID = "xEGMeEoypA";
}
