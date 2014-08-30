package com.appmanager.android.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.appmanager.android.entity.FileEntry;

import java.net.URLEncoder;

/**
 * an original schema for importing from email or hyper-link.
 * Created by maimuzo on 2014/08/30.
 */
public class AppManagerSchema {
    private static final String TAG = "AppManagerSchema";
    public static final String SCHEMA_HEADER = "appmanager-";

    /**
     * "appmanager-https://{basicAuthUser}:{basicAuthPassword}@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#{name}"
     * to
     * "https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true"
     * @param uri
     * @return decoded FileEntry, or null if it was not able to decode uri.
     */
    public static FileEntry decode(String uri) {
        FileEntry entry;
        // validate url
        try {
            Uri encodedUri = Uri.parse(uri);

            String schema = encodedUri.getScheme();
            if(!schema.startsWith(SCHEMA_HEADER)){
                throw new UnsupportedOperationException("uri must start '" + SCHEMA_HEADER + "'");
            }
            entry = new FileEntry();
            entry.name = encodedUri.getFragment(); // null if not include
            String userInfo = encodedUri.getUserInfo();
            if(null != userInfo){
                String[] parts = userInfo.split(":");
                String basicAuthUser = parts[0];
                String basicAuthPassword = parts[1];
                if(!TextUtils.isEmpty(basicAuthUser) && !TextUtils.isEmpty(basicAuthPassword)){
                    entry.basicAuthUser = basicAuthUser;
                    entry.basicAuthPassword = basicAuthPassword;
                }
            }

            String originSchema = schema.replace(SCHEMA_HEADER, "");
            String host = encodedUri.getHost();
            String path = encodedUri.getPath();
            String query = encodedUri.getQuery();
            if(TextUtils.isEmpty(query)){
                entry.url = originSchema + "://" + host + path;;
            } else {
                entry.url = originSchema + "://" + host + path + "?" + query;
            }


            return entry;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * "https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true"
     * to
     * "appmanager-https://{basicAuthUser}:{basicAuthPassword}@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#{name}"
     * @param url
     * @param name
     * @param basicAuthUser
     * @param basicAuthPassword
     * @return encoded uri, or null if it was not able to encode url.
     */
    public static String encode(String url, String name, String basicAuthUser, String basicAuthPassword){
        // validate url
        try{
            Uri uri = Uri.parse(url);
            StringBuilder sb = new StringBuilder();
            sb.append(SCHEMA_HEADER).append(uri.getScheme()).append("://");
            if(!TextUtils.isEmpty(basicAuthUser) && !TextUtils.isEmpty(basicAuthPassword)){
                sb.append(basicAuthUser).append(":").append(basicAuthPassword).append("@");
            }
            sb.append(uri.getHost());
            sb.append(uri.getPath());
            if(!TextUtils.isEmpty(uri.getEncodedQuery())){
                sb.append("?").append(uri.getEncodedQuery());
            }
            if(!TextUtils.isEmpty(name)){
                sb.append("#").append(URLEncoder.encode(name, "UTF-8"));
            }
            return sb.toString();
        } catch (Exception e){
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
