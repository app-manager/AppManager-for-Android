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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "app_manager.db";
    private static final int DB_VERSION = 1;

    public DbHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.beginTransaction();
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE file_entries (");
            sb.append(" id integer primary key autoincrement, ");
            sb.append(" name text, ");
            sb.append(" url text not null, ");
            sb.append(" basic_auth_user text, ");
            sb.append(" basic_auth_password text, ");
            sb.append(" created_at datetime not null, ");
            sb.append(" updated_at datetime not null, ");
            sb.append(" header_last_modified text, ");
            sb.append(" header_etag text, ");
            sb.append(" header_content_length text");
            sb.append(");");
            sqLiteDatabase.execSQL(sb.toString());
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
    }
}
