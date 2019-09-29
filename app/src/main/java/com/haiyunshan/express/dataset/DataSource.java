package com.haiyunshan.express.dataset;

import android.os.Environment;

import java.io.File;

/**
 *
 */
public class DataSource {

    public static final File getShareCacheDir() {
        File file = new File(getDocumentDir(), "cache");
        file = new File(file, "share");
        ensureDir(file);

        return file;
    }

    public static final File getNote(String id) {
        File file = new File(getDocumentDir(), "note");
        file = new File(file, id + ".note");

        return file;
    }

    public static final File getNoteId(String id) {
        File file = getNote(id);
        file = new File(file, "id.json");
        return file;
    }

    public static final File getNoteFile(String id) {
        File file = getNote(id);
        file = new File(file, "note.json");
        return file;
    }

    public static final File getNoteStyle(String id) {
        File file = getNote(id);
        file = new File(file, "style.json");
        return file;
    }

    public static final File getNotePage(String id) {
        File file = getNote(id);
        file = new File(file, "page.json");
        return file;
    }

    public static final String getAssetTemplate() {
        return "template/template_ds.json";
    }

    public static final String getAssetTemplate(String id) {
        return "template/" + id + ".template";
    }

    public static final String getAssetTypeface() {
        return "typeface/typeface_ds.json";
    }

    public static final String getAssetTypeface(String uri) {
        return "typeface/file/" + uri;
    }

    public static final File getTypefaceFile() {
        File file = new File(getDocumentDir(), "typeface/typeface_ds.json");
        ensureFile(file);

        return file;
    }

    public static final File getTypefaceFile(String uri) {
        File file = new File(getDocumentDir(), "typeface/file");
        file = new File(file, uri);
        ensureFile(file);

        return file;
    }

    public static final File getNoteDs() {
        File file = new File(getDocumentDir(), "note/note_ds.json");
        ensureFile(file);

        return file;
    }

    public static final File getPicture(String uri) {
        File file = new File(getDocumentDir(), "picture/file");
        file = new File(file, uri);
        ensureFile(file);

        return file;
    }

    public static final File getPictureDs() {
        File file = new File(getDocumentDir(), "picture/picture_ds.json");
        ensureFile(file);

        return file;
    }

    public static final File getMusic(String uri) {
        File file = new File(getDocumentDir(), "music/file");
        file = new File(file, uri);
        ensureFile(file);

        return file;
    }

    public static final File getMusicDs() {
        File file = new File(getDocumentDir(), "music/music_ds.json");
        ensureFile(file);

        return file;
    }

    public static final String getAssetCatalog() {
        return "catalog/catalog_ds.json";
    }

    public static final File getCatalog() {
        File file = new File(getDocumentDir(), "catalog/catalog_ds.json");
        ensureFile(file);

        return file;
    }

    private static File getDocumentDir() {
        File dir = Environment.getExternalStorageDirectory();
        dir = new File(dir, "Express");
        ensureDir(dir);

        return dir;
    }

    private static File ensureDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    private static File ensureFile(File file) {
        if (!file.exists()) {
            File dir = file.getParentFile();
            ensureDir(dir);
        }

        return file;
    }
}
