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
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appmanager.android.R;
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
        String url = ((EditText) findViewById(R.id.url)).getText().toString();
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "URL is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        if (TextUtils.isEmpty(name)) {
            name = url;
        }

        String basicAuthUser = ((EditText) findViewById(R.id.basicAuthUser)).getText().toString();
        String basicAuthPassword = ((EditText) findViewById(R.id.basicAuthPassword)).getText().toString();

        // TODO Save name and url

        Toast.makeText(this, "Installing: " + url, Toast.LENGTH_LONG).show();
        InstallTask task = new InstallTask(this);
        if (!TextUtils.isEmpty(basicAuthUser) && !TextUtils.isEmpty(basicAuthPassword)) {
            task.setBasicAuth(basicAuthUser, basicAuthPassword);
        }
        task.setListener(this);
        task.execute(url);
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
