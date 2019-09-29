package com.haiyunshan.express.music;

/**
 * Created by sanshibro on 2017/6/3.
 */

public interface OnMusicScanListener {

    void onSeek(LocalMusicScanner scanner, LocalMusicEntry entry);

    void onComplete(LocalMusicScanner scanner);

    void onCancelled(LocalMusicScanner scanner);
}
