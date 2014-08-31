package com.appmanager.android.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

    public CheckAndInstallService(){
        super("CheckAndInstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "update check started.");
        List<FileEntry> list = new FileEntryDao(this).findAll();

        for(FileEntry fe: list){
            AppDownloader downloader = new AppDownloader(getApplicationContext(), fe);
            try{
                Log.d(TAG, "check: " + fe.url);
                // TODO: for debug
                Log.d(TAG, "dump: " + fe.toString());
                if(downloader.needToUpdate(getApplicationContext(), fe)){
                    Log.d(TAG, "downloading... " + fe.url);
                    String apkPath = downloader.download(getApplicationContext());
                    Log.d(TAG, "download complete. kick com.android.packageinstaller: " + fe.url);
                    InstallUtils.delegateInstall(this, apkPath);
                }
            } catch (Exception e){
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "update check finished.");
    }
}
