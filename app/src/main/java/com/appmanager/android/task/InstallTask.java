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

package com.appmanager.android.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.util.AppDownloader;
import com.appmanager.android.util.LogUtils;

import java.io.IOException;

/**
 * Async task for downloading/installing app.
 *
 * @author Soichiro Kashima
 */
public class InstallTask extends AsyncTask<String, Void, String> {

    public interface InstallListener {
        void onComplete(final String apkPath);
    }

    private static final String TAG = InstallTask.class.getSimpleName();
    private InstallListener mListener;
    private Activity mActivity;
    private FileEntry mFileEntry;

    public void setListener(final InstallListener listener) {
        mListener = listener;
    }

    public InstallTask(final Activity activity, FileEntry entry) {
        mActivity = activity;
        mFileEntry = entry;
    }

    @Override
    protected String doInBackground(final String... strings) {
        try {
            AppDownloader downloader = new AppDownloader(mActivity, mFileEntry);
            return downloader.download(mActivity);
        } catch (IllegalArgumentException e) {
            LogUtils.e(TAG, "Failed to download", e);
        } catch (IOException e) {
            LogUtils.e(TAG, "Failed to download", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mListener != null) {
            mListener.onComplete(s);
        }
        mActivity.finish();
    }
}
