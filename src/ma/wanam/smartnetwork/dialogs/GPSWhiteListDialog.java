package ma.wanam.smartnetwork.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ma.wanam.smartnetwork.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

public class GPSWhiteListDialog extends DialogFragment {

	private AlertDialog dialog;
	private PackageManager packageManager;
	private List<ResolveInfo> appsList;
	private CharSequence[] appsLabelArray;
	private boolean[] appsCheckedArray;
	private SharedPreferences sharedPreferences;

	public GPSWhiteListDialog(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
		launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		packageManager = getActivity().getPackageManager();
		appsList = packageManager.queryIntentActivities(launcherIntent, 0);
		Collections.sort(appsList, new ResolveInfo.DisplayNameComparator(packageManager));
		appsLabelArray = new CharSequence[appsList.size() + 1];
		appsCheckedArray = new boolean[appsList.size() + 1];
		appsLabelArray[0] = getActivity().getString(R.string.select_all_none);
		appsCheckedArray[0] = true;
		String[] whiteArray = sharedPreferences.getString("enableGPSWhiteList", "").split(";");
		List<String> whiteList = new ArrayList<String>(whiteArray.length);
		whiteList.addAll(Arrays.asList(whiteArray));
		for (int i = 0; i < appsList.size(); i++) {
			appsLabelArray[i + 1] = appsList.get(i).loadLabel(packageManager);
			if (whiteList.contains(appsList.get(i).activityInfo.packageName)) {
				appsCheckedArray[i + 1] = true;
			} else {
				appsCheckedArray[i + 1] = false;
				appsCheckedArray[0] = false;
			}
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		OnMultiChoiceClickListener listener = new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				appsCheckedArray[which] = isChecked;
				if (which == 0) {
					for (int i = 1; i < appsCheckedArray.length; i++) {
						appsCheckedArray[i] = isChecked;
						((AlertDialog) dialog).getListView().setItemChecked(i, isChecked);
					}
				}
			}
		};

		dialog = builder.setMultiChoiceItems(appsLabelArray, appsCheckedArray, listener).setCancelable(true)
				.setTitle(R.string.white_list_applications)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						StringBuilder stringBuilder = new StringBuilder();
						for (int i = 1; i < appsCheckedArray.length; i++) {
							if (appsCheckedArray[i] == true) {
								stringBuilder.append(appsList.get(i - 1).activityInfo.packageName + ";");
							}
						}

						sharedPreferences
								.edit()
								.putString(
										"enableGPSWhiteList",
										((stringBuilder.length() > 1) ? stringBuilder.toString().substring(0,
												stringBuilder.length() - 1) : "")).commit();

					}
				}).setNegativeButton(android.R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();

		return dialog;
	}

}
