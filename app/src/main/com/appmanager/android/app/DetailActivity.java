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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appmanager.android.R;
import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.task.InstallTask;
import com.appmanager.android.util.InstallUtils;
import com.appmanager.android.validator.FileEntryValidator;
import com.simplealertdialog.SimpleAlertDialog;
import com.simplealertdialog.SimpleAlertDialogSupportFragment;

public class DetailActivity extends FragmentActivity implements InstallTask.InstallListener,
        SimpleAlertDialog.OnClickListener {

    public static final String EXTRA_FILE_ENTRY = "fileEntry";
    private static final String DEFAULT_URL = "https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true";
    private static final int DIALOG_REQUEST_CODE_DELETE = 1;
    private FileEntry mFileEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mFileEntry = null;

        Intent intent = getIntent();
        Button deleteButton = (Button) findViewById(R.id.delete);
        if (intent != null) {
            if (intent.hasExtra(EXTRA_FILE_ENTRY)) {
                mFileEntry = intent.getParcelableExtra(EXTRA_FILE_ENTRY);
                if (mFileEntry != null) {
                    ((EditText) findViewById(R.id.name)).setText(mFileEntry.name);
                    ((EditText) findViewById(R.id.url)).setText(mFileEntry.url);
                    ((EditText) findViewById(R.id.basicAuthUser)).setText(mFileEntry.basicAuthUser);
                    ((EditText) findViewById(R.id.basicAuthPassword)).setText(mFileEntry.basicAuthPassword);
                }
            } else {
                ((EditText) findViewById(R.id.url)).setText(DEFAULT_URL);
                deleteButton.setEnabled(false);
            }
        }
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDownload();
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });

    }

    private void confirmDownload() {
        FileEntry entry = getFileEntryFromScreen();
        FileEntryValidator validator = new FileEntryValidator(this, entry);
        if (!validator.isValid()) {
            Toast.makeText(this, validator.getErrors(), Toast.LENGTH_SHORT).show();
            return;
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            Toast.makeText(this, "Not connected to network.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(entry.name)) {
            entry.name = entry.url;
        }

        // Save name and url
        if (mFileEntry == null || mFileEntry.id == 0) {
            // Create
            new FileEntryDao(this).create(entry);
        } else {
            // Update
            new FileEntryDao(this).update(entry);
        }

        Toast.makeText(this, "Installing: " + entry.url, Toast.LENGTH_LONG).show();
        InstallTask task = new InstallTask(this);
        if (!TextUtils.isEmpty(entry.basicAuthUser) && !TextUtils.isEmpty(entry.basicAuthPassword)) {
            task.setBasicAuth(entry.basicAuthUser, entry.basicAuthPassword);
        }
        task.setListener(this);
        task.execute(entry.url);
    }

    private void save() {
        FileEntry entry = getFileEntryFromScreen();
        FileEntryValidator validator = new FileEntryValidator(this, entry);
        if (!validator.isValid()) {
            Toast.makeText(this, validator.getErrors(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mFileEntry == null || mFileEntry.id == 0) {
            // Create
            new FileEntryDao(this).create(entry);
        } else {
            // Update
            new FileEntryDao(this).update(entry);
        }
        finish();
    }

    private void confirmDelete() {
        new SimpleAlertDialogSupportFragment.Builder()
                .setMessage(R.string.msg_confirm_file_entry_delete)
                .setPositiveButton(android.R.string.ok)
                .setNegativeButton(android.R.string.cancel)
                .setRequestCode(DIALOG_REQUEST_CODE_DELETE)
                .create()
                .show(getSupportFragmentManager(), "dialog");
    }

    private void delete() {
        FileEntry entry = getFileEntryFromScreen();
        entry.id = mFileEntry.id;
        new FileEntryDao(this).delete(entry);
        finish();
    }

    private FileEntry getFileEntryFromScreen() {
        FileEntry entry;
        if (null == mFileEntry) {
            entry = new FileEntry();
        } else {
            entry = mFileEntry;
        }
        entry.url = ((EditText) findViewById(R.id.url)).getText().toString();
        entry.name = ((EditText) findViewById(R.id.name)).getText().toString();
        entry.basicAuthUser = ((EditText) findViewById(R.id.basicAuthUser)).getText().toString();
        entry.basicAuthPassword = ((EditText) findViewById(R.id.basicAuthPassword)).getText().toString();

        return entry;
    }

    @Override
    public void onComplete(final String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            Toast.makeText(this, "Download failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        InstallUtils.delegateInstall(this, apkPath);
    }

    @Override
    public void onDialogPositiveButtonClicked(SimpleAlertDialog simpleAlertDialog, int requestCode, View view) {
        if (requestCode == DIALOG_REQUEST_CODE_DELETE) {
            delete();
        }
    }

    @Override
    public void onDialogNegativeButtonClicked(SimpleAlertDialog simpleAlertDialog, int requestCode, View view) {
    }

}
