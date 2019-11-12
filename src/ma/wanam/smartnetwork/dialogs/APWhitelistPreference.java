package ma.wanam.smartnetwork.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ma.wanam.smartnetwork.MainApplication;
import ma.wanam.smartnetwork.R.string;
import ma.wanam.smartnetwork.utils.Utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

public class APWhitelistPreference extends MultiSelectListPreference {

	private static final String SEPARATOR = ";";

	public APWhitelistPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setTitle(string.wifi_ap_whitelist);
		// retrieve the list of configured access points
		List<String> whitelistedSsids = new ArrayList<String>();

		List<String> ssids = Utils.getConfiguredAccessPoints(context);

		// retrieve the list of witelisted accesspoints
		String whitelist = MainApplication.sharedPreferences.getString("wifi_ssid_whitelist", "");

		String[] wl = whitelist.split(SEPARATOR);

		// start adding the whitelisted enstries
		for (int i = 0; i < wl.length; i++) {
			if ((!wl[i].equals("")) && (wl[i] != null)) {
				String entry = Utils.stripLeadingAndTrailingQuotes(wl[i]).trim();
				whitelistedSsids.add(entry);
			}
		}

		// next add the available ssid not yet listed
		if (ssids != null) {
			for (int i = 0; i < ssids.size(); i++) {
				if (ssids.get(i) != null) {
					String availableSSID = Utils.stripLeadingAndTrailingQuotes(ssids.get(i).trim());

					if ((!ssids.get(i).equals("")) && (!whitelistedSsids.contains(availableSSID))) {
						whitelistedSsids.add(availableSSID);

					}
				}
			}
		}
		CharSequence[] entries = new CharSequence[whitelistedSsids.size()];
		CharSequence[] entryValues = new CharSequence[whitelistedSsids.size()];

		for (int i = 0; i < whitelistedSsids.size(); i++) {
			entries[i] = whitelistedSsids.get(i);
			entryValues[i] = whitelistedSsids.get(i);
		}
		setEntries(entries);
		setEntryValues(entryValues);

	}

	public APWhitelistPreference(Context context) {
		super(context, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (!positiveResult) {
			return;
		}

		CharSequence[] entryValues = getEntryValues();
		Set selectedValues = getValues();
		if (positiveResult && entryValues != null) {
			StringBuffer value = new StringBuffer();
			Iterator<String> it = selectedValues.iterator();
			while (it.hasNext()) {
				String val = it.next();
				value.append(val).append(SEPARATOR);
			}

			String pref = value.toString();
			if (pref.length() > 0) {
				pref = pref.substring(0, pref.length() - SEPARATOR.length());
			}

			SharedPreferences.Editor editor = MainApplication.sharedPreferences.edit();
			editor.putString("wifi_ssid_whitelist", pref);
			editor.commit();

		}
	}

}