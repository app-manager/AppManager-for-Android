package com.appmanager.android.util;

import com.appmanager.android.entity.FileEntry;

import junit.framework.TestCase;

import java.net.URLEncoder;

/**
 * Created by maimuzo on 2014/08/30.
 */
public class AppManagerSchemaTest extends TestCase {
    private static final String APK_PATH = "github.com/app-manager/AppManager-for-Android/blob/master/tests/apk/dummy.apk?raw=true";
    private static final String ORIGINAL_URL_HTTP = "http://" + APK_PATH;
    private static final String ORIGINAL_URL_HTTPS = "https://" + APK_PATH;
    private static final String AUTH_USER = "testuser";
    private static final String AUTH_PASS = "authpass";
    private static final String NAME_ALP = "testName";
    private static final String NAME_JP = "テスト名";
    private static final String NAME_JP_ENCODED = "%E3%83%86%E3%82%B9%E3%83%88%E5%90%8D";
    private static final String ENCODED_URI_PATTERN = "https://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH;

    public void testCanEncode(){
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, AUTH_PASS), "https://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTP, NAME_ALP, AUTH_USER, AUTH_PASS), "http://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_JP, AUTH_USER, AUTH_PASS), "https://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_JP_ENCODED);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, "", AUTH_PASS), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, ""), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, "", ""), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, null, AUTH_PASS), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, null), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, NAME_ALP, null, null), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, "", null, null), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH);
        assertEquals(AppManagerSchema.encode(ORIGINAL_URL_HTTPS, null, null, null), "https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH);
    }

    public void testCanDecode(){
        assertNotNull(AppManagerSchema.decode("https://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP));
        FileEntry fe1 = AppManagerSchema.decode("https://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP);
        FileEntry fe2 = packFileEntry(ORIGINAL_URL_HTTPS, NAME_ALP, AUTH_USER, AUTH_PASS);
        assertTrue(fe1.contentEqualsTo(fe2));
        assertTrue(AppManagerSchema.decode("http://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP).contentEqualsTo(packFileEntry(ORIGINAL_URL_HTTP, NAME_ALP, AUTH_USER, AUTH_PASS)));
        assertTrue(AppManagerSchema.decode("https://testuser:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_JP_ENCODED).contentEqualsTo(packFileEntry(ORIGINAL_URL_HTTPS, NAME_JP, AUTH_USER, AUTH_PASS)));
        assertNull(AppManagerSchema.decode("https://testuser@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP));
        assertTrue(AppManagerSchema.decode("https://:authpass@" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP).contentEqualsTo(packFileEntry(ORIGINAL_URL_HTTPS, NAME_ALP, null, null)));
        assertTrue(AppManagerSchema.decode("https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH + "#" + NAME_ALP).contentEqualsTo(packFileEntry(ORIGINAL_URL_HTTPS, NAME_ALP, null, null)));
        assertTrue(AppManagerSchema.decode("https://" + AppManagerSchema.SPECIAL_HOST + "/" + APK_PATH).contentEqualsTo(packFileEntry(ORIGINAL_URL_HTTPS, null, null, null)));
        assertNull(AppManagerSchema.decode("https://sonnanonaiyo/" + APK_PATH));
        assertNull(AppManagerSchema.decode("https://" + APK_PATH));
    }

    private FileEntry packFileEntry(String url, String name, String user, String pass){
        FileEntry fe = new FileEntry();
        fe.name = name;
        fe.url = url;
        fe.basicAuthUser = user;
        fe.basicAuthPassword = pass;
        return fe;
    }
}
