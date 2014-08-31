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

package com.appmanager.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.appmanager.android.entity.FileEntry;
import com.appmanager.android.util.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class FileEntryDao {
    private Context mContext;

    public FileEntryDao(final Context context) {
        mContext = context;
    }

    public List<FileEntry> findAll() {
        List<FileEntry> result = new ArrayList<FileEntry>();
        DbHelper helper = new DbHelper(getContext());
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query("file_entries", new String[]{
                    "id", "name", "url", "basic_auth_user", "basic_auth_password", "created_at", "updated_at", "header_last_modified", "header_etag", "header_content_length",
            }, null, null, null, null, "id asc", null);
            if (cursor != null) {
                for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {
                    FileEntry entity = new FileEntry();
                    int index = 0;
                    entity.id = cursor.getInt(index++);
                    entity.name = cursor.getString(index++);
                    entity.url = cursor.getString(index++);
                    entity.basicAuthUser = cursor.getString(index++);
                    entity.basicAuthPassword = cursor.getString(index++);
                    entity.createdAt = cursor.getString(index++);
                    entity.updatedAt = cursor.getString(index++);
                    entity.headerLastModified = cursor.getString(index++);
                    entity.headerEtag = cursor.getString(index++);
                    entity.headerContentLength = cursor.getString(index++);
                    result.add(entity);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return result;
    }

    public void create(final FileEntry entity) {
        if (entity == null) {
            return;
        }
        SQLiteDatabase db = null;
        SQLiteStatement statement = null;
        try {
            DbHelper helper = new DbHelper(getContext());
            db = helper.getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO file_entries (");
            sb.append(" name, ");
            sb.append(" url, ");
            sb.append(" basic_auth_user, ");
            sb.append(" basic_auth_password, ");
            sb.append(" created_at, ");
            sb.append(" updated_at");
            sb.append(") VALUES (");
            sb.append(" ?, "); // name
            sb.append(" ?, "); // url
            if (entity.basicAuthUser != null) {
                sb.append(" ?, "); // basic_auth_user
            }
            if (entity.basicAuthPassword != null) {
                sb.append(" ?, "); // basic_auth_password
            }
            sb.append("DATETIME('now', 'localtime'), "); // created_at
            sb.append("DATETIME('now', 'localtime')"); // updated_at
            sb.append(");");
            statement = db.compileStatement(sb.toString());
            int index = 1;
            statement.bindString(index++, entity.name);
            statement.bindString(index++, entity.url);
            if (entity.basicAuthUser != null) {
                statement.bindString(index++, entity.basicAuthUser);
            }
            if (entity.basicAuthPassword != null) {
                statement.bindString(index++, entity.basicAuthPassword);
            }
            statement.executeInsert();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


    public void update(final FileEntry entity) {
        if (entity == null) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            DbHelper helper = new DbHelper(getContext());
            db = helper.getWritableDatabase();

            ContentValues value = new ContentValues();
            value.put("name", entity.name);
            value.put("url", entity.url);
            value.put("basic_auth_user", entity.basicAuthUser);
            value.put("basic_auth_password", entity.basicAuthPassword);
            value.put("created_at", entity.createdAt);
            value.put("updated_at", entity.updatedAt); // 中身が入っているのかは見てない(=p)

            db.update("file_entries", value, "id = ?", new String[]{String.valueOf(entity.id)});
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void updateHeader(final FileEntry entity) {
        if (entity == null && 0 == entity.id) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            DbHelper helper = new DbHelper(getContext());
            db = helper.getWritableDatabase();

            ContentValues value = new ContentValues();
            value.put("header_last_modified", entity.headerLastModified);
            value.put("header_etag", entity.headerEtag);
            value.put("header_content_length", entity.headerContentLength);

            db.update("file_entries", value, "id = ?", new String[]{String.valueOf(entity.id)});
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void delete(final FileEntry entity) {
        if (entity == null) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            DbHelper helper = new DbHelper(getContext());
            db = helper.getWritableDatabase();
            db.delete("file_entries", "id = ?", new String[]{String.valueOf(entity.id)});
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }


    private Context getContext() {
        return mContext;
    }
}
