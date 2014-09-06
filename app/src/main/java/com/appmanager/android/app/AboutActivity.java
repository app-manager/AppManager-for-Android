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

package com.appmanager.android.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.appmanager.android.R;
import com.appmanager.android.util.BitmapUtils;
import com.appmanager.android.util.VersionUtils;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_about);

        ((ImageView) findViewById(R.id.icon)).setImageDrawable(
                BitmapUtils.getDensityOptimizedIconSizeDrawable(getApplicationContext(),
                        getApplicationContext(), R.drawable.ic_launcher));
        ((TextView) findViewById(R.id.version_name)).setText(VersionUtils.getVersion(this));
        ((TextView) findViewById(R.id.copyright)).setText(Html
                .fromHtml(getString(R.string.label_copyright)));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem menu) {
        if (menu.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setupActionBar() {
        if (VersionUtils.isEqualOrHigherThanHoneycomb()) {
            getActionBar().setHomeButtonEnabled(true);
            if (VersionUtils.isEqualOrHigherThanIceCreamSandwich()) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

}
