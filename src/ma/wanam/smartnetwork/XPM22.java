/*
 * Copyright (C) 2013 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ma.wanam.smartnetwork;

import ma.wanam.smartnetwork.utils.Constants;
import android.util.ArraySet;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XPM22 {
	public static final boolean DEBUG = false;

	private static final String CLASS_PACKAGE_MANAGER_SERVICE = "com.android.server.pm.PackageManagerService";
	private static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";

	private static final String WAKE_LOCK = "android.permission.WAKE_LOCK";
	private static final String GET_TASKS = "android.permission.GET_TASKS";
	private static final String CHANGE_WIFI_STATE = "android.permission.CHANGE_WIFI_STATE";
	private static final String CHANGE_NETWORK_STATE = "android.permission.CHANGE_NETWORK_STATE";
	private static final String WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS";
	private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

	public static void initZygote(final ClassLoader classLoader) {

		try {
			final Class<?> pmServiceClass = XposedHelpers.findClass(CLASS_PACKAGE_MANAGER_SERVICE, classLoader);

			XposedHelpers.findAndHookMethod(pmServiceClass, "grantPermissionsLPw", CLASS_PACKAGE_PARSER_PACKAGE,
					boolean.class, String.class, new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							hookPermissions(param);
						}

					});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}

	}

	public static void hookPermissions(MethodHookParam param) {
		final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");
		if (Constants.SMART_NETWORK.equals(pkgName)) {
			final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
			@SuppressWarnings("unchecked")
			final ArraySet<String> grantedPerms = (ArraySet<String>) XposedHelpers.getObjectField(extras,
					"grantedPermissions");
			final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
			final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

			// Add android.permission.WAKE_LOCK
			if (!grantedPerms.contains(WAKE_LOCK)) {
				final Object pWriteSettings = XposedHelpers.callMethod(permissions, "get", WAKE_LOCK);
				grantedPerms.add(WAKE_LOCK);
				int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
				int[] bpGids = (int[]) XposedHelpers.getObjectField(pWriteSettings, "gids");
				gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), "appendInts", gpGids,
						bpGids);

			}

			// Add android.permission.GET_TASKS
			if (!grantedPerms.contains(GET_TASKS)) {
				final Object pWriteSettings = XposedHelpers.callMethod(permissions, "get", GET_TASKS);
				grantedPerms.add(GET_TASKS);
				int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
				int[] bpGids = (int[]) XposedHelpers.getObjectField(pWriteSettings, "gids");
				gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), "appendInts", gpGids,
						bpGids);

			}

			// Add android.permission.CHANGE_WIFI_STATE
			if (!grantedPerms.contains(CHANGE_WIFI_STATE)) {
				final Object pWriteSettings = XposedHelpers.callMethod(permissions, "get", CHANGE_WIFI_STATE);
				grantedPerms.add(CHANGE_WIFI_STATE);
				int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
				int[] bpGids = (int[]) XposedHelpers.getObjectField(pWriteSettings, "gids");
				gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), "appendInts", gpGids,
						bpGids);

			}

			// Add android.permission.CHANGE_NETWORK_STATE
			if (!grantedPerms.contains(CHANGE_NETWORK_STATE)) {
				final Object pWriteSettings = XposedHelpers.callMethod(permissions, "get", CHANGE_NETWORK_STATE);
				grantedPerms.add(CHANGE_NETWORK_STATE);
				int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
				int[] bpGids = (int[]) XposedHelpers.getObjectField(pWriteSettings, "gids");
				gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), "appendInts", gpGids,
						bpGids);

			}

			// Add android.permission.WRITE_SECURE_SETTINGS
			if (!grantedPerms.contains(WRITE_SECURE_SETTINGS)) {
				final Object pWriteSettings = XposedHelpers.callMethod(permissions, "get", WRITE_SECURE_SETTINGS);
				grantedPerms.add(WRITE_SECURE_SETTINGS);
				int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
				int[] bpGids = (int[]) XposedHelpers.getObjectField(pWriteSettings, "gids");
				gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), "appendInts", gpGids,
						bpGids);

			}

			// Add android.permission.ACCESS_FINE_LOCATION
			if (!grantedPerms.contains(ACCESS_FINE_LOCATION)) {
				final Object pWriteSettings = XposedHelpers.callMethod(permissions, "get", ACCESS_FINE_LOCATION);
				grantedPerms.add(ACCESS_FINE_LOCATION);
				int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
				int[] bpGids = (int[]) XposedHelpers.getObjectField(pWriteSettings, "gids");
				gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), "appendInts", gpGids,
						bpGids);

			}

		}
	}
}