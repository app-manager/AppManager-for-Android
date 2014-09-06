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

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class BitmapUtils {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static int getDensityOptimizedIconSize(final Context context) {
        int iconSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            iconSize = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                    .getLauncherLargeIconSize();
        } else {
            iconSize = (int) context.getResources().getDimension(
                    android.R.dimen.app_icon_size);
        }
        return iconSize;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static Drawable getDensityOptimizedIconSizeDrawable(final Context myAppContext,
                                                               final Context targetPackageContext, final int resId) {
        int adjustedResId = resId;
        if (adjustedResId <= 0) {
            adjustedResId = android.R.drawable.sym_def_app_icon;
        }
        int density;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            density = ((ActivityManager) myAppContext.getSystemService(Context.ACTIVITY_SERVICE))
                    .getLauncherLargeIconDensity();
        } else {
            Display display = ((WindowManager) myAppContext.getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            density = metrics.densityDpi;
        }
        Drawable d;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            try {
                d = targetPackageContext.getResources().getDrawableForDensity(
                        adjustedResId,
                        density);
            } catch (Resources.NotFoundException e) {
                return null;
            }
        } else {
            d = targetPackageContext.getResources().getDrawable(adjustedResId);
        }

        // サイズが大きすぎる場合はリサイズ
        int iconSize = BitmapUtils
                .getDensityOptimizedIconSize(myAppContext);
        if (d instanceof BitmapDrawable
                && (iconSize < d.getIntrinsicWidth() || iconSize < d.getIntrinsicHeight())) {
            Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) d).getBitmap(),
                    iconSize, iconSize, true);
            d = new BitmapDrawable(myAppContext.getResources(), bitmap);
        }

        return d;
    }

}
