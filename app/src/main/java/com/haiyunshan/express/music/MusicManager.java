package com.haiyunshan.express.music;

import android.os.Environment;
import android.text.TextUtils;

import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.DataManager;
import com.haiyunshan.express.dataset.DataSource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 音乐管理器
 *
 */
public class MusicManager {

    LocalMusicDataset mLocalMusicDs;

    public static final MusicManager instance() {
        return DataManager.instance().getMusicManager();
    }

    public MusicManager() {

    }

    public File getMusic(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        File file = DataSource.getMusic(uri);
        return file;
    }

    public void removeMusic(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }

        File file = DataSource.getMusic(uri);
        file.delete();
    }

    public String replaceMusic(String fromFile, String toUri) {
        if (TextUtils.isEmpty(toUri)) {
            toUri = UUIDUtils.next();
        }

        File file = DataSource.getMusic(toUri);

        try {
            file.delete();
            file.createNewFile();
            FileUtils.copyFile(new File(fromFile), file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toUri;
    }

    public LocalMusicDataset getLocalMusicDataset() {
        return mLocalMusicDs;
    }

    public void setLocalMusicDataset(LocalMusicDataset ds) {
        this.mLocalMusicDs = ds;
    }
}
