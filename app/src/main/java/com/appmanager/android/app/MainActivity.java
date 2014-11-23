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
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.appmanager.android.BuildConfig;
import com.appmanager.android.R;
import com.appmanager.android.adapter.FileEntryAdapter;
import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.Configuration;
import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.task.GetConfigTask;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity
        implements FileEntryAdapter.OnClickListener, GetConfigTask.OnCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.add_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addApp();
            }
        });
        findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOnPlayStore("com.appmanager.android");
                hideInfo(true);
            }
        });
        hideInfo(false);
        loadFileEntries();
        checkLatestVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFileEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                loadFileEntries();
                break;
            case R.id.action_add_app:
                addApp();
                break;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return false;
    }

    private void loadFileEntries() {
        ListView listView = (ListView) findViewById(R.id.list);
        if (listView == null) {
            return;
        }
        List<FileEntry> list = new FileEntryDao(this).findAll();
        if (list.size() == 0) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        FileEntryAdapter adapter = new FileEntryAdapter(this, list);
        adapter.setOnClickListener(this);
        listView.setAdapter(adapter);
    }

    private void addApp() {
        startActivity(new Intent(this, EditActivity.class));
    }

    @Override
    public void onClick(final View view, final FileEntry fileEntry) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_FILE_ENTRY, fileEntry);
        startActivity(intent);
    }

    private void checkLatestVersion() {
        GetConfigTask task = new GetConfigTask(this);
        task.setOnCompleteListener(this);
        task.execute();
    }

    @Override
    public void onComplete(Map<String, Configuration> configs) {
        if (canUpdateApp(configs)) {
            ((TextView) findViewById(R.id.info_text)).setText(
                    getResources().getString(R.string.msg_info_update,
                            configs.get(BuildConfig.APPLICATION_ID).latestVersionName));
            showInfo(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideInfo(true);
                }
            }, 3000);
        }
    }

    private boolean canUpdateApp(Map<String, Configuration> configs) {
        if (configs == null || isFinishing()) {
            return false;
        }
        if (configs.containsKey(BuildConfig.APPLICATION_ID)) {
            Configuration config = configs.get(BuildConfig.APPLICATION_ID);
            if (BuildConfig.VERSION_CODE < config.latestVersion) {
                return true;
            }
        }
        return false;
    }

    private void showInfo(boolean animated) {
        View infoView = findViewById(R.id.info);
        if (ViewHelper.getTranslationY(infoView) == 0) {
            return;
        }
        if (animated) {
            ViewPropertyAnimator.animate(infoView).translationY(0).setDuration(1000).start();
        } else {
            ViewHelper.setTranslationY(infoView, 0);
        }
    }

    private void hideInfo(boolean animated) {
        View infoView = findViewById(R.id.info);
        if (ViewHelper.getTranslationY(infoView) == -getActionBarSize()) {
            return;
        }
        if (animated) {
            ViewPropertyAnimator.animate(infoView).translationY(-getActionBarSize()).setDuration(1000).start();
        } else {
            ViewHelper.setTranslationY(infoView, -getActionBarSize());
        }
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    private void viewOnPlayStore(final String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        startActivity(intent);
    }
}
