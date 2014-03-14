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

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InstallTask extends AsyncTask<String, Void, String> {

    public interface InstallListener {
        void onComplete(final String apkPath);
    }

    private static final int BUFFER_SIZE = 1024;
    private InstallListener mListener;

    public void setListener(final InstallListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(final String... strings) {
        String strUrl = strings[0];
        try {
            URL url = new URL(strUrl);

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            String basePath = "apk";
            File file = new File(basePath);
            file.mkdirs();

            final String apkName = "app.apk";
            File outputFile = new File(file, apkName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            is.close();

            return basePath + "/" + apkName;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mListener != null) {
            mListener.onComplete(s);
        }
    }
}
