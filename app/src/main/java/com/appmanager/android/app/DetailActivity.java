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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appmanager.android.R;
import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.task.InstallTask;
import com.appmanager.android.util.AppManagerSchema;
import com.appmanager.android.util.InstallUtils;
import com.appmanager.android.util.VersionUtils;
import com.appmanager.android.validator.FileEntryValidator;
import com.simplealertdialog.SimpleAlertDialog;
import com.simplealertdialog.SimpleAlertDialogSupportFragment;

public class DetailActivity extends FragmentActivity implements InstallTask.InstallListener,
        SimpleAlertDialog.OnClickListener {

    public static final String EXTRA_FILE_ENTRY = "fileEntry";
    private static final String DEFAULT_URL = "https://github.com/app-manager/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true";
    private static final int DIALOG_REQUEST_CODE_DELETE = 1;
    private static final int DIALOG_REQUEST_CODE_FINISH = 2;
    private FileEntry mFileEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setupActionBar();

        mFileEntry = null;

        Intent intent = getIntent();
        Button deleteButton = (Button) findViewById(R.id.delete);
        if (intent != null) {
            if (intent.hasExtra(EXTRA_FILE_ENTRY)) {
                mFileEntry = intent.getParcelableExtra(EXTRA_FILE_ENTRY);
                if (mFileEntry != null) {
                    setTitle(R.string.activity_title_edit_app);
                    restoreValues(mFileEntry);
                }
            } else {
                ((EditText) findViewById(R.id.name)).setText(extractNameFromUrl(DEFAULT_URL));
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (AppManagerSchema.canDecode(intent)) {
            FileEntry fe = AppManagerSchema.decode(intent.getData().toString());
            if (null != fe) {
                restoreValues(fe);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            confirmFinish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        confirmFinish();
    }

    private void restoreValues(FileEntry entry) {
        String name = TextUtils.isEmpty(entry.name) ? extractNameFromUrl(entry) : entry.name;
        ((EditText) findViewById(R.id.name)).setText(name);
        ((EditText) findViewById(R.id.url)).setText(entry.url);
        ((EditText) findViewById(R.id.basicAuthUser)).setText(entry.basicAuthUser);
        ((EditText) findViewById(R.id.basicAuthPassword)).setText(entry.basicAuthPassword);
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
        InstallTask task = new InstallTask(this, entry);
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
        if (mFileEntry == null) {
            return;
        }
        mFileEntry.copyMetaDataTo(entry);
        new FileEntryDao(this).delete(entry);
        finish();
    }

    private void confirmFinish() {
        if (!contentChanged()) {
            finish();
            return;
        }
        new SimpleAlertDialogSupportFragment.Builder()
                .setMessage(R.string.msg_confirm_file_entry_finish)
                .setPositiveButton(android.R.string.ok)
                .setNegativeButton(android.R.string.cancel)
                .setRequestCode(DIALOG_REQUEST_CODE_FINISH)
                .create()
                .show(getSupportFragmentManager(), "dialog");
    }

    private FileEntry getFileEntryFromScreen() {
        FileEntry entry = new FileEntry();
        if (null != mFileEntry) {
            mFileEntry.copyMetaDataTo(entry);
        }
        entry.url = ((EditText) findViewById(R.id.url)).getText().toString();
        entry.name = ((EditText) findViewById(R.id.name)).getText().toString();
        entry.basicAuthUser = ((EditText) findViewById(R.id.basicAuthUser)).getText().toString();
        entry.basicAuthPassword = ((EditText) findViewById(R.id.basicAuthPassword)).getText().toString();

        return entry;
    }

    private String extractNameFromUrl(FileEntry entry) {
        return extractNameFromUrl(entry.url);
    }

    private String extractNameFromUrl(String entryUrl) {
        if (TextUtils.isEmpty(entryUrl)) {
            return "";
        }
        String url = entryUrl.contains("?") ? entryUrl.replaceAll("\\?.*$", "") : entryUrl;
        return url.replaceAll("^.*/", "").replaceAll("\\.apk", "");
    }

    private boolean contentChanged() {
        FileEntry before = mFileEntry;
        FileEntry after = getFileEntryFromScreen();
        if (before == null) {
            before = new FileEntry();
        }
        return !before.contentEqualsTo(after);
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
        switch (requestCode) {
            case DIALOG_REQUEST_CODE_DELETE:
                delete();
                break;
            case DIALOG_REQUEST_CODE_FINISH:
                finish();
                break;
        }
    }

    @Override
    public void onDialogNegativeButtonClicked(SimpleAlertDialog simpleAlertDialog, int requestCode, View view) {
    }

}
