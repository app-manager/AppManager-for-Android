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

package com.appmanager.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class FileEntry implements Parcelable {

    /**
     * Creator of this class objects from Parcelable.
     */
    public static final Parcelable.Creator<FileEntry> CREATOR =
            new Parcelable.Creator<FileEntry>() {
                public FileEntry createFromParcel(final Parcel source) {
                    return new FileEntry(source);
                }

                public FileEntry[] newArray(final int size) {
                    return new FileEntry[size];
                }
            };

    public int id;
    public String name;
    public String url;
    public String basicAuthUser;
    public String basicAuthPassword;
    public String createdAt;
    public String updatedAt;


    /**
     * Default constructor for normal creation.
     */
    public FileEntry() {
    }

    /**
     * Constructor for using parcels.
     *
     * @param source parcel of FileEntry
     */
    public FileEntry(final Parcel source) {
        id = source.readInt();
        name = source.readString();
        url= source.readString();
        basicAuthUser = source.readString();
        basicAuthPassword = source.readString();
        createdAt = source.readString();
        updatedAt = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flag) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(basicAuthUser);
        dest.writeString(basicAuthPassword);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }
}
