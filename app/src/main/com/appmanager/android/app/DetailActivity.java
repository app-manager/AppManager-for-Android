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

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appmanager.android.R;
import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.task.InstallTask;

import java.io.File;

public class DetailActivity extends Activity implements InstallTask.InstallListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDownload();
            }
        });

        ((EditText) findViewById(R.id.url)).setText("https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true");
    }

    private void confirmDownload() {
        FileEntry entry = new FileEntry();
        entry.url = ((EditText) findViewById(R.id.url)).getText().toString();
        if (TextUtils.isEmpty(entry.url)) {
            Toast.makeText(this, "URL is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            Toast.makeText(this, "Not connected to network.", Toast.LENGTH_SHORT).show();
            return;
        }

        entry.name = ((EditText) findViewById(R.id.name)).getText().toString();
        if (TextUtils.isEmpty(entry.name)) {
            entry.name = entry.url;
        }

        entry.basicAuthUser = ((EditText) findViewById(R.id.basicAuthUser)).getText().toString();
        entry.basicAuthPassword = ((EditText) findViewById(R.id.basicAuthPassword)).getText().toString();

        // Save name and url
        new FileEntryDao(this).create(entry);

        Toast.makeText(this, "Installing: " + entry.url, Toast.LENGTH_LONG).show();
        InstallTask task = new InstallTask(this);
        if (!TextUtils.isEmpty(entry.basicAuthUser) && !TextUtils.isEmpty(entry.basicAuthPassword)) {
            task.setBasicAuth(entry.basicAuthUser, entry.basicAuthPassword);
        }
        task.setListener(this);
        task.execute(entry.url);
    }

    @Override
    public void onComplete(final String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            Toast.makeText(this, "Download failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
