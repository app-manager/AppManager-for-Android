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

package com.appmanager.android.validator;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.appmanager.android.R;
import com.appmanager.android.entity.FileEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Soichiro Kashima
 */
public class FileEntryValidator {

    private static final String NEWLINE = "\n";
    private Context mContext;
    private FileEntry mEntry;
    private List<String> mErrors;

    public FileEntryValidator(final Context context, final FileEntry entry) {
        mContext = context;
        mEntry = entry;
    }

    public boolean isValid() {
        mErrors = new ArrayList<String>();
        Resources res = mContext.getResources();
        if (mEntry == null) {
            mErrors.add(res.getString(R.string.error_file_entry_empty));
            return false;
        }
        if (TextUtils.isEmpty(mEntry.url)) {
            mErrors.add(res.getString(R.string.error_file_entry_url_required));
            return false;
        }
        return true;
    }

    public String getErrors() {
        if (mErrors == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String error : mErrors) {
            if (sb.length() > 0) {
                sb.append(NEWLINE);
            }
            sb.append(error);
        }
        return sb.toString();
    }
}
