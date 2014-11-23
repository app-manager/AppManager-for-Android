package com.appmanager.android.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.appmanager.android.BuildConfig;
import com.appmanager.android.entity.Configuration;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class GetConfigTask extends AsyncTask<Void, Void, Map<String, Configuration>> {
    private static final int BUFFER_SIZE = 1024;

    public interface OnCompleteListener {
        void onComplete(Map<String, Configuration> configs);
    }

    private Context mContext;
    private OnCompleteListener mListener;

    public GetConfigTask(Context context) {
        mContext = context;
    }

    public void setOnCompleteListener(final OnCompleteListener listener) {
        mListener = listener;
    }

    @Override
    protected Map<String, Configuration> doInBackground(Void... params) {
        String configString = getConfigString();
        return convertToConfigs(configString);
    }

    @Override
    protected void onPostExecute(Map<String, Configuration> result) {
        super.onPostExecute(result);
        if (mListener != null) {
            mListener.onComplete(result);
        }
    }

    private String getConfigString() {
        URLConnection c = null;
        InputStream is;
        try {
            String url = BuildConfig.CONFIG_URL;
            if (url.startsWith("file:///android_asset")) {
                is = mContext.getAssets().open(BuildConfig.CONFIG_URL.replaceFirst("file:\\/\\/\\/android_asset\\/", ""));
            } else {
                c = new URL(BuildConfig.CONFIG_URL).openConnection();
                is = c.getInputStream();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
            baos.close();
            is.close();
            return baos.toString(HTTP.UTF_8);
        } catch (IOException ignore) {
            ignore.printStackTrace();
        } finally {
            if (c != null && c instanceof HttpURLConnection) {
                ((HttpURLConnection) c).disconnect();
            }
        }
        return null;
    }

    private Map<String, Configuration> convertToConfigs(String configString) {
        Map<String, Configuration> result = new HashMap<>();
        if (TextUtils.isEmpty(configString)) {
            return result;
        }
        try {
            JSONObject json = new JSONObject(configString);
            JSONArray configs = json.getJSONArray("configurations");
            for (int i = 0; i < configs.length(); i++) {
                JSONObject jsonConfig = configs.getJSONObject(i);
                Configuration config = new Configuration();
                config.id = jsonConfig.getString("id");
                config.publishUrl = jsonConfig.getString("publish_url");
                config.latestVersion = jsonConfig.getInt("latest_version");
                config.latestVersionName = jsonConfig.getString("latest_version_name");
                result.put(config.id, config);
            }
        } catch (JSONException ignore) {
        }
        return result;
    }
}
