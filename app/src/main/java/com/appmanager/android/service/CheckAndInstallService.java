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

package com.appmanager.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.appmanager.android.dao.FileEntryDao;
import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.util.AppDownloader;
import com.appmanager.android.util.InstallUtils;

import java.util.List;

/**
 * Created by maimuzo on 2014/08/30.
 */
public class CheckAndInstallService extends IntentService {
    private static final String TAG = "CheckAndInstallService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CheckAndInstallService(String name) {
        super(name);
    }

    public CheckAndInstallService() {
        super("CheckAndInstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "update check started.");
        List<FileEntry> list = new FileEntryDao(this).findAll();

        for (FileEntry fe : list) {
            AppDownloader downloader = new AppDownloader(getApplicationContext(), fe);
            try {
                Log.d(TAG, "check: " + fe.url);
                // TODO: for debug
                Log.d(TAG, "dump: " + fe.toString());
                if (downloader.needToUpdate(getApplicationContext(), fe)) {
                    Log.d(TAG, "downloading... " + fe.url);
                    String apkPath = downloader.download(getApplicationContext());
                    Log.d(TAG, "download complete. kick com.android.packageinstaller: " + fe.url);
                    InstallUtils.delegateInstall(this, apkPath);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "update check finished.");
    }
}
