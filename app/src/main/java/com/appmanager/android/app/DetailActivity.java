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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appmanager.android.R;
import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.task.InstallTask;
import com.appmanager.android.util.InstallUtils;
import com.appmanager.android.util.VersionUtils;
import com.appmanager.android.validator.FileEntryValidator;

/**
 * @author Soichiro Kashima
 */
public class DetailActivity extends FragmentActivity implements InstallTask.InstallListener {

    public static final String EXTRA_FILE_ENTRY = "fileEntry";
    protected FileEntry mFileEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        setContentView(R.layout.activity_detail);
        setupActionBar();

        mFileEntry = null;

        if (hasFileEntryInIntent()) {
            mFileEntry = getFileEntryFromIntent();
            if (mFileEntry != null && !TextUtils.isEmpty(mFileEntry.name)) {
                setTitle(mFileEntry.name);
                restoreValues(mFileEntry);
            }
        }
        findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                install();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit:
                edit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(final String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            Toast.makeText(this, "Download failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        InstallUtils.delegateInstall(this, apkPath);
    }

    protected void restoreValues(FileEntry entry) {
        ((TextView) findViewById(R.id.name)).setText(entry.name);
        ((TextView) findViewById(R.id.url)).setText(entry.url);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void setupActionBar() {
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

    protected void install() {
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

    protected FileEntry getFileEntryFromScreen() {
        FileEntry entry = new FileEntry();
        if (null != mFileEntry) {
            mFileEntry.copyMetaDataTo(entry);
        }
        entry.url = ((TextView) findViewById(R.id.url)).getText().toString();
        entry.name = ((TextView) findViewById(R.id.name)).getText().toString();
        return entry;
    }

    protected boolean hasFileEntryInIntent() {
        Intent intent = getIntent();
        return intent != null && intent.hasExtra(EXTRA_FILE_ENTRY);
    }

    protected FileEntry getFileEntryFromIntent() {
        return getIntent().getParcelableExtra(EXTRA_FILE_ENTRY);
    }

    private void edit() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_FILE_ENTRY, mFileEntry);
        startActivity(intent);
        finish();
    }

}
