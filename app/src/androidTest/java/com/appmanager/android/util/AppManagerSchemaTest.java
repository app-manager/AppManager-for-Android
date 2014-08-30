package com.appmanager.android.util;

import com.appmanager.android.entity.FileEntry;

import junit.framework.TestCase;

import java.net.URLEncoder;

/**
 * Created by maimuzo on 2014/08/30.
 */
public class AppManagerSchemaTest extends TestCase {
    private static final String ORIGINAL_URL_HTTP = "http://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true";
    private static final String ORIGINAL_URL_HTTPS = "https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true";
    private static final String AUTH_USER = "testuser";
    private static final String AUTH_PASS = "authpass";
    private static final String NAME_ALP = "testName";
    private static final String NAME_JP = "テスト名";
    private static final String NAME_JP_ENCODED = "%E3%83%86%E3%82%B9%E3%83%88%E5%90%8D";
    private static final String ENCODED_URI_PATTERN = "appmanager-https://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#";

    public void testCanEncode(){
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, AUTH_PASS), "appmanager-https://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTP, NAME_ALP, AUTH_USER, AUTH_PASS), "appmanager-http://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_JP, AUTH_USER, AUTH_PASS), "appmanager-https://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_JP_ENCODED);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, "", AUTH_PASS), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, ""), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, "", ""), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, null, AUTH_PASS), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, null), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, null, null), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, "", null, null), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true");
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, null, null, null), "appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true");
    }

    public void testCanDecode(){
        assertNotNull(AppManagerSchema.decode("appmanager-https://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP));
        FileEntry fe1 = AppManagerSchema.decode("appmanager-https://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP);
        FileEntry fe2 = packFileEntry(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, AUTH_PASS);
        assertTrue(fe1.equalValues(fe2));
        assertTrue(AppManagerSchema.decode("appmanager-http://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP).equalValues(packFileEntry(ORIGINAL_URL_HTTP, NAME_ALP, AUTH_USER, AUTH_PASS)));
        assertTrue(AppManagerSchema.decode("appmanager-https://testuser:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_JP_ENCODED).equalValues(packFileEntry(ORIGINAL_URL_HTTPS, NAME_JP, AUTH_USER, AUTH_PASS)));
        assertNull(AppManagerSchema.decode("appmanager-https://testuser@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP));
        assertTrue(AppManagerSchema.decode("appmanager-https://:authpass@github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP).equalValues(packFileEntry(ORIGINAL_URL_HTTPS, NAME_ALP, null, null)));
        assertTrue(AppManagerSchema.decode("appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true#" + NAME_ALP).equalValues(packFileEntry(ORIGINAL_URL_HTTPS, NAME_ALP, null, null)));
        assertTrue(AppManagerSchema.decode("appmanager-https://github.com/ksoichiro/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true").equalValues(packFileEntry(ORIGINAL_URL_HTTPS, null, null, null)));
    }

    private FileEntry packFileEntry(String url, String name, String user, String pass){
        try{
            FileEntry fe = new FileEntry();
            fe.name = name;
            fe.url = url;
            fe.basicAuthUser = user;
            fe.basicAuthPassword = pass;
            return fe;
        } catch (Exception e){
            return null;
        }
    }
}
