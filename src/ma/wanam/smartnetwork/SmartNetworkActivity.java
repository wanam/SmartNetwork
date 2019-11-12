package ma.wanam.smartnetwork;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import ma.wanam.smartnetwork.R.string;
import ma.wanam.smartnetwork.utils.Constants;
import ma.wanam.smartnetwork.utils.Utils;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

public class SmartNetworkActivity extends Activity {

	private final Context mContext = this;
	private ProgressDialog mDialog;
	private AlertDialog alertDialog;

	private final SettingsFragment settingsFragment = new SettingsFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		List<String> permsToRequest = new ArrayList<String>();

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
			permsToRequest.add(Manifest.permission.ACCESS_WIFI_STATE);
		}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
			permsToRequest.add(Manifest.permission.BLUETOOTH);
		}

		if (permsToRequest.size() > 0) {
			String[] permsList = new String[permsToRequest.size()];
			ActivityCompat.requestPermissions(this, permsToRequest.toArray(permsList), 1982);
		}

		initScreen();
		setContentView(R.layout.wanam_main);

		try {
			Utils.pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		new Handler().post(new Runnable() {
			public void run() {
				getFragmentManager().beginTransaction().replace(R.id.prefs, settingsFragment).commitAllowingStateLoss();
			}
		});

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

		switch (requestCode) {
		case 1982: {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0) {

				for (int i : grantResults) {
					if (i != PackageManager.PERMISSION_GRANTED) {
						Toast.makeText(this, R.string.a_required_permission_was_not_granted_, Toast.LENGTH_LONG).show();
					}
				}
			}
			return;
		}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		initScreen();
		super.onConfigurationChanged(newConfig);
	}

	private void initScreen() {

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Throwable t) {
			// Ignore
		}
	}

	@Override
	protected void onPause() {
		try {
			if (mDialog != null) {
				mDialog.dismiss();
			}
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Resources res = getResources();
		AlertDialog.Builder alertDialogBuilder;
		switch (item.getItemId()) {
		case R.id.action_credits:
			alertDialogBuilder = new AlertDialog.Builder(mContext);
			alertDialogBuilder.setTitle(res.getString(string.app_name) + " " + Utils.pInfo.versionName);

			TextView textViewAbout = new TextView(mContext);
			final SpannableString msg = new SpannableString(res.getString(string.about_info));
			Linkify.addLinks(msg, Linkify.WEB_URLS);
			textViewAbout.setText(msg);
			textViewAbout.setMovementMethod(LinkMovementMethod.getInstance());
			textViewAbout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

			alertDialogBuilder.setView(textViewAbout).setCancelable(true)
					.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATE_URL));
							startActivity(browserIntent);
							dialog.dismiss();
						}
					}).setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;

		default:
			break;
		}
		return true;

	}
}
