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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.appmanager.android.R;
import com.appmanager.android.adapter.FileEntryAdapter;
import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.FileEntry;

import java.util.List;

public class MainActivity extends Activity
        implements FileEntryAdapter.OnClickListener {

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
        loadFileEntries();
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
        startActivity(new Intent(this, DetailActivity.class));
    }

    @Override
    public void onClick(final View view, final FileEntry fileEntry) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_FILE_ENTRY, fileEntry);
        startActivity(intent);
    }
}
