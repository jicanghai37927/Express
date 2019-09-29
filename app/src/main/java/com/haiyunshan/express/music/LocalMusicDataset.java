package com.haiyunshan.express.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanshibro on 2017/9/1.
 */

public class LocalMusicDataset {

    ArrayList<LocalMusicEntry> mList;

    public LocalMusicDataset() {
        this.mList = new ArrayList<>();
    }

    public LocalMusicEntry put(String id, String name, File file) {
        LocalMusicEntry e = new LocalMusicEntry(id, name, file);
        mList.add(e);

        return e;
    }

    public List<LocalMusicEntry> getList() {
        return this.mList;
    }

    public int size() {
        return mList.size();
    }

}
