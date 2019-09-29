package com.haiyunshan.express.typeface;

/**
 *
 */
public interface OnTypefaceScanListener {

    void onSeek(LocalTypefaceScanner scanner, LocalTypefaceEntry entry);

    void onComplete(LocalTypefaceScanner scanner);

    void onCancelled(LocalTypefaceScanner scanner);
}
