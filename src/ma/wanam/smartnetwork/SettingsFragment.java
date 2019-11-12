package ma.wanam.smartnetwork;

import java.util.ArrayList;
import java.util.List;

import ma.wanam.smartnetwork.dialogs.BTWhiteListDialog;
import ma.wanam.smartnetwork.dialogs.GPSWhiteListDialog;
import ma.wanam.smartnetwork.dialogs.MobileWhiteListDialog;
import ma.wanam.smartnetwork.dialogs.WiFiWhiteListDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import de.robv.android.xposed.library.ui.TextViewPreference;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	// Fields
	private List<String> changesMade;
	private ProgressDialog mDialog;
	private TextViewPreference textViewInformationHeader;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			final SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_WORLD_READABLE);
			MainApplication.sharedPreferences = sharedPreferences;
			changesMade = new ArrayList<String>();

			getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
			getPreferenceManager().setSharedPreferencesName(SmartNetworkActivity.class.getSimpleName());

			addPreferencesFromResource(R.xml.wanam_settings);
			PreferenceScreen ps = (PreferenceScreen) findPreference("prefsRoot");
			textViewInformationHeader = (TextViewPreference) findPreference("wanamHeader");

			if (!XposedChecker.isActive()) {
				textViewInformationHeader.setTitle(R.string.smart_is_not_active);
				textViewInformationHeader.getTextView().setTextColor(Color.RED);
				textViewInformationHeader.setEnabled(false);
			} else {
				ps.removePreference(textViewInformationHeader);
			}

			findPreference("WiFiWhiteList").setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					new WiFiWhiteListDialog(sharedPreferences).show(getFragmentManager(), "WiFiWhiteList");
					return true;
				}
			});

			findPreference("MobileWhiteList").setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

					new MobileWhiteListDialog(sharedPreferences).show(getFragmentManager(), "MobileWhiteList");
					return true;
				}
			});

			findPreference("GPSWhiteList").setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

					new GPSWhiteListDialog(sharedPreferences).show(getFragmentManager(), "GPSWhiteList");
					return true;
				}
			});

			findPreference("BTWhiteList").setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

					new BTWhiteListDialog(sharedPreferences).show(getFragmentManager(), "BTWhiteList");
					return true;
				}
			});

			if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
				PreferenceScreen psGPS = (PreferenceScreen) findPreference("gpsPS");
				ps.removePreference(psGPS);
			}
			final CheckBoxPreference wifiDisconnect = (CheckBoxPreference) findPreference("wifiDisconnect");
			final CheckBoxPreference wifiToggle = (CheckBoxPreference) findPreference("wifiToggle");

			wifiDisconnect.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					wifiToggle.setEnabled(!((CheckBoxPreference) preference).isChecked());
					wifiToggle.setChecked(false);
					return false;
				}
			});

			wifiToggle.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					wifiDisconnect.setEnabled(!((CheckBoxPreference) preference).isChecked());
					wifiDisconnect.setChecked(false);
					return false;
				}
			});

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		try {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerPrefsReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterPrefsReceiver();
	}

	private void registerPrefsReceiver() {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
	}

	private void unregisterPrefsReceiver() {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		try {
			// No reboot notification required
			String[] litePrefs = new String[] { "drt", "drt_ts" };
			for (String string : litePrefs) {
				if (key.equalsIgnoreCase(string)) {
					return;
				}
			}

			// Add preference key to changed keys list
			if (!changesMade.contains(key)) {
				changesMade.add(key);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
