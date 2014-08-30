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

package com.appmanager.android.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Download app(apk) synchronously.
 */
public class AppDownloader {
    private static final int BUFFER_SIZE = 1024;
    private static final String BASE_DIR = "apk";
    private String mBasicAuthUser;
    private String mBasicAuthPassword;
    private URL mUrl;

    /**
     * Create a new downloader with URL.<br />
     * If a malformed URL is passed, this constructor
     * throws {@linkplain java.lang.IllegalArgumentException}.
     *
     * @param url Location string(must be valid URL) of the target APK file
     */
    public AppDownloader(final String url) {
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format", e);
        }
    }

    /**
     * Sets basic authentication information.
     * This is optional.
     *
     * @param user     User name for basic auth
     * @param password User password for basic auth
     */
    public void setBasicAuth(final String user, final String password) {
        mBasicAuthUser = user;
        mBasicAuthPassword = password;
    }

    /**
     * Download the specified file and save to app local storage.
     *
     * @param baseContext Base context to access app local storage
     * @return Downloaded APK file path
     * @throws java.io.IOException If downloading failed
     */
    public String download(final Context baseContext) throws IOException {
        HttpURLConnection c = (HttpURLConnection) mUrl.openConnection();

        // Optionally use basic auth
        if (!TextUtils.isEmpty(mBasicAuthPassword) && !TextUtils.isEmpty(mBasicAuthPassword)) {
            c.setRequestProperty("Authorization",
                    "Basic " + base64Encode(mBasicAuthUser + ":" + mBasicAuthPassword));
        }

        c.setRequestMethod("GET");
        c.connect();

        ContextWrapper cw = new ContextWrapper(baseContext);
        File dir = new File(cw.getExternalFilesDir(null), BASE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final String apkName = "app.apk";
        File outputFile = new File(dir, apkName);
        FileOutputStream fos = new FileOutputStream(outputFile);

        InputStream is = c.getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.flush();
        fos.close();
        is.close();

        return outputFile.getAbsolutePath();
    }

    private String base64Encode(final String in) {
        return Base64.encodeToString(in.getBytes(), Base64.DEFAULT);
    }
}
