package com.haiyunshan.express.typeface;

import com.haiyunshan.express.music.LocalMusicDataset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LocalTypefaceDataset {

    ArrayList<LocalTypefaceEntry> mList;

    public LocalTypefaceDataset() {
        this.mList = new ArrayList<>();
    }

    public LocalTypefaceEntry put(String id, String name, File file) {
        LocalTypefaceEntry e = new LocalTypefaceEntry(id, name, file);
        mList.add(e);

        return e;
    }

    public List<LocalTypefaceEntry> getList() {
        return this.mList;
    }

    public int size() {
        return mList.size();
    }

}
