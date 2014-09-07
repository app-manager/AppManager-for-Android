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
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import com.appmanager.android.R;
import com.appmanager.android.util.VersionUtils;

/**
 * @author Soichiro Kashima
 */
public class SettingsActivity extends FragmentActivity {

    private static final String PREF_KEY_ADMIN_PASSWORD = "admin_password";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();
        findViewById(R.id.set_admin_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdminPassword();
            }
        });
        findViewById(R.id.show_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setupActionBar() {
        if (VersionUtils.isEqualOrHigherThanHoneycomb()) {
            ActionBar ab = getActionBar();
            if (ab == null) {
                return;
            }
            if (VersionUtils.isEqualOrHigherThanIceCreamSandwich()) {
                ab.setHomeButtonEnabled(true);
            }
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdminPassword() {
    }

}
