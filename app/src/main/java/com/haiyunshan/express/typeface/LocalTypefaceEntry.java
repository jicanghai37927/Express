package com.haiyunshan.express.typeface;

import java.io.File;

/**
 * Created by sanshibro on 26/11/2017.
 */

public class LocalTypefaceEntry {

    String mId;

    String mName;
    String mPath;

    long mFileSize;
    long mModified;

    LocalTypefaceEntry(String id, String name, File file) {
        this.mId = id;

        this.mName = name;
        this.mPath = file.getAbsolutePath();

        this.mFileSize = file.length();
        this.mModified = file.lastModified();
    }

    public String getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public String getPath() {
        return this.mPath;
    }

    public long getFileSize() {
        return this.mFileSize;
    }

    public long getModified() {
        return this.mModified;
    }
}
