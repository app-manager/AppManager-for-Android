package com.appmanager.android.util;

import android.util.Log;

import com.appmanager.android.BuildConfig;

/**
 * Logging utility.
 *
 * @author Soichiro Kashima
 */
public class LogUtils {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static void e(String tag, String msg, Throwable cause) {
        if (DEBUG) {
            Log.e(tag, msg, cause);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

}
