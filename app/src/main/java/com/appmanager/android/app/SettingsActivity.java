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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appmanager.android.BuildConfig;
import com.appmanager.android.R;
import com.simplealertdialog.SimpleAlertDialog;
import com.simplealertdialog.SimpleAlertDialogSupportFragment;

/**
 * @author Soichiro Kashima
 */
public class SettingsActivity extends BaseActivity implements SimpleAlertDialog.OnClickListener,
        SimpleAlertDialog.ViewProvider {

    private static final String PREF_KEY_ADMIN_PASSWORD = "admin_password";
    private static final int DIALOG_REQUEST_CODE_ADMIN_PASSWORD = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        if (BuildConfig.FLAVOR.equals("development")) {
            findViewById(R.id.set_admin_password).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAdminPassword();
                }
            });
        } else {
            findViewById(R.id.category_general).setVisibility(View.GONE);
            findViewById(R.id.set_admin_password).setVisibility(View.GONE);
        }

        findViewById(R.id.show_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveButtonClicked(SimpleAlertDialog simpleAlertDialog, int requestCode, View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedPassword = prefs.getString(PREF_KEY_ADMIN_PASSWORD, "");
        TextView currentPassword = (TextView) view.findViewById(R.id.current_password);
        if (!savedPassword.equals(currentPassword.getText().toString())) {
            Toast.makeText(this, R.string.msg_password_not_matched, Toast.LENGTH_SHORT).show();
            return;
        }
        TextView newPassword = (TextView) view.findViewById(R.id.new_password);
        TextView retypePassword = (TextView) view.findViewById(R.id.retype_password);
        if (!newPassword.getText().toString().equals(retypePassword.getText().toString())) {
            Toast.makeText(this, R.string.msg_retyped_password_not_matched, Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_KEY_ADMIN_PASSWORD, newPassword.getText().toString());
        editor.commit();
    }

    @Override
    public void onDialogNegativeButtonClicked(SimpleAlertDialog simpleAlertDialog, int requestCode, View view) {
    }

    @Override
    public View onCreateView(SimpleAlertDialog simpleAlertDialog, int requestCode) {
        if (requestCode == DIALOG_REQUEST_CODE_ADMIN_PASSWORD) {
            return LayoutInflater.from(this).inflate(R.layout.dialog_admin_password, null);
        }
        return null;
    }

    private void setAdminPassword() {
        new SimpleAlertDialogSupportFragment.Builder()
                .setUseView(true)
                .setRequestCode(DIALOG_REQUEST_CODE_ADMIN_PASSWORD)
                .setTitle(R.string.pref_category_general_title_admin_password)
                .setPositiveButton(android.R.string.ok)
                .setNegativeButton(android.R.string.cancel)
                .create().show(getSupportFragmentManager(), "dialog");
    }

}
