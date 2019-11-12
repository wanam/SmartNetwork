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
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XPM23 {
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
							final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");
							if (Constants.SMART_NETWORK.equals(pkgName)) {

								final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");

								final Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
								final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");

								final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

								if (!(Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", WAKE_LOCK)) {
									final Object pAccess = XposedHelpers.callMethod(permissions, "get", WAKE_LOCK);
									XposedHelpers.callMethod(ps, "grantInstallPermission", pAccess);
								}

								if (!(Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", GET_TASKS)) {
									final Object pAccess = XposedHelpers.callMethod(permissions, "get", GET_TASKS);
									XposedHelpers.callMethod(ps, "grantInstallPermission", pAccess);
								}

								if (!(Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", CHANGE_WIFI_STATE)) {
									final Object pAccess = XposedHelpers.callMethod(permissions, "get",
											CHANGE_WIFI_STATE);
									XposedHelpers.callMethod(ps, "grantInstallPermission", pAccess);
								}

								if (!(Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission",
										CHANGE_NETWORK_STATE)) {
									final Object pAccess = XposedHelpers.callMethod(permissions, "get",
											CHANGE_NETWORK_STATE);
									XposedHelpers.callMethod(ps, "grantInstallPermission", pAccess);
								}

								if (!(Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission",
										WRITE_SECURE_SETTINGS)) {
									final Object pAccess = XposedHelpers.callMethod(permissions, "get",
											WRITE_SECURE_SETTINGS);
									XposedHelpers.callMethod(ps, "grantInstallPermission", pAccess);
								}

								if (!(Boolean) XposedHelpers.callMethod(ps, "hasInstallPermission",
										ACCESS_FINE_LOCATION)) {
									final Object pAccess = XposedHelpers.callMethod(permissions, "get",
											ACCESS_FINE_LOCATION);
									XposedHelpers.callMethod(ps, "grantInstallPermission", pAccess);
								}

							}

						}
					});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}

	}

}