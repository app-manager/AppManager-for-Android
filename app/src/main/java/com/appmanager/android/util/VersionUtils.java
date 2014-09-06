/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appmanager.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * Utilities for handling app versions.
 *
 * @author Soichiro Kashima
 */
public class VersionUtils {

    /**
     * Check if the API level is higher than Honeycomb(API level 11).
     *
     * @return true if API level is equal to or higher than 11
     */
    public static boolean isEqualOrHigherThanHoneycomb() {
        return isEqualOrHigherThan(Build.VERSION_CODES.HONEYCOMB);
    }

    /**
     * Check if the API level is higher than Ice cream sandwich(API level 14).
     *
     * @return true if API level is equal to or higher than 14
     */
    public static boolean isEqualOrHigherThanIceCreamSandwich() {
        return isEqualOrHigherThan(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    public static boolean isEqualOrHigherThan(final int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    /**
     * Returns the version string to the preference.
     *
     * @return version name
     */
    public static String getVersion(final Context context) {
        final PackageManager manager = context.getPackageManager();
        String versionName;
        try {
            final PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

}
