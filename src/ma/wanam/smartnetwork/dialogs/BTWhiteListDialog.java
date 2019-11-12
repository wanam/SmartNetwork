package ma.wanam.smartnetwork.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import ma.wanam.smartnetwork.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class BTWhiteListDialog extends DialogFragment {

	private AlertDialog dialog;
	private List<String> btDevicessList;
	private CharSequence[] btDevicessLabelArray;
	private boolean[] btDevicessCheckedArray;
	private SharedPreferences sharedPreferences;

	public BTWhiteListDialog(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(getActivity(), R.string.please_enable_bluetooth, Toast.LENGTH_LONG).show();
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		btDevicessList = new ArrayList<String>();
		for (BluetoothDevice bt : pairedDevices)
			btDevicessList.add(bt.getName());

		if (btDevicessList.size() == 0) {
			return;
		}
		btDevicessLabelArray = new CharSequence[btDevicessList.size() + 1];
		btDevicessCheckedArray = new boolean[btDevicessList.size() + 1];
		btDevicessLabelArray[0] = getActivity().getString(R.string.select_all_none);
		btDevicessCheckedArray[0] = true;
		String[] whiteArray = sharedPreferences.getString("enableBTWhiteList", "").split(";");
		List<String> whiteList = new ArrayList<String>(whiteArray.length);
		whiteList.addAll(Arrays.asList(whiteArray));
		for (int i = 0; i < btDevicessList.size(); i++) {
			btDevicessLabelArray[i + 1] = btDevicessList.get(i);
			if (whiteList.contains(btDevicessList.get(i))) {
				btDevicessCheckedArray[i + 1] = true;
			} else {
				btDevicessCheckedArray[i + 1] = false;
				btDevicessCheckedArray[0] = false;
			}
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		OnMultiChoiceClickListener listener = new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				btDevicessCheckedArray[which] = isChecked;
				if (which == 0) {
					for (int i = 1; i < btDevicessCheckedArray.length; i++) {
						btDevicessCheckedArray[i] = isChecked;
						((AlertDialog) dialog).getListView().setItemChecked(i, isChecked);
					}
				}
			}
		};

		dialog = builder.setMultiChoiceItems(btDevicessLabelArray, btDevicessCheckedArray, listener)
				.setCancelable(true).setTitle(R.string.white_list_devices)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						StringBuilder stringBuilder = new StringBuilder();
						for (int i = 1; i < btDevicessCheckedArray.length; i++) {
							if (btDevicessCheckedArray[i] == true) {
								stringBuilder.append(btDevicessList.get(i - 1) + ";");
							}
						}

						sharedPreferences
								.edit()
								.putString(
										"enableBTWhiteList",
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
