package com.haiyunshan.express.dataset;

import android.content.res.AssetManager;
import android.provider.ContactsContract;

import com.haiyunshan.express.App;
import com.haiyunshan.express.app.AssetUtils;
import com.haiyunshan.express.dataset.catalog.CatalogDataset;
import com.haiyunshan.express.dataset.note.Identifier;
import com.haiyunshan.express.dataset.note.NoteDataset;
import com.haiyunshan.express.dataset.note.page.Page;
import com.haiyunshan.express.dataset.note.style.Style;
import com.haiyunshan.express.dataset.note.Note;
import com.haiyunshan.express.dataset.template.TemplateDataset;
import com.haiyunshan.express.typeface.TypefaceDataset;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sanshibro on 20/11/2017.
 */

public class DataAccessor {

    public static final DataAccessor instance() {
        return DataManager.instance().accessor();
    }

    public boolean isNoteExist(String id) {
        File file = DataSource.getNote(id);
        boolean exist = file.exists();
        if (!exist) {
            return exist;
        }

        file = DataSource.getNoteFile(id);
        return file.exists();
    }

    public Note getNoteFile(String id) {
        JSONObject json = null;

        File file = DataSource.getNoteFile(id);
        if (file.exists()) {
            try {
                String text = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Note note = null;
        if (json != null) {
            note = new Note(json);
        }

        return note;
    }

    public void removeNote(String id) {
        File file = DataSource.getNote(id);
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {

        }
    }

    public Identifier getNoteId(String id) {
        JSONObject json = null;

        File file = DataSource.getNoteId(id);
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Identifier ds = new Identifier(id, json);
        return ds;
    }

    public Page getNotePage(String id) {
        JSONObject json = null;

        File file = DataSource.getNotePage(id);
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Page ds = new Page(json);
        return ds;
    }

    public Style getNoteStyle(String id) {
        JSONObject json = null;

        File file = DataSource.getNoteStyle(id);
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Style ds = new Style(json);
        return ds;
    }

    public void saveNoteId(Identifier id) {
        File file = DataSource.getNoteId(id.getId());

        try {
            JSONObject json = id.toJSON();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {

        }
    }

    public void saveNoteFile(String id, Note note) {
        File file = DataSource.getNoteFile(id);

        try {
            JSONObject json = note.toJSON();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {

        }
    }

    public void saveNotePage(String id, Page page) {
        File file = DataSource.getNotePage(id);

        try {
            JSONObject json = page.toJSON();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {

        }
    }

    public void saveNoteStyle(String id, Style style) {
        File file = DataSource.getNoteStyle(id);

        try {
            JSONObject json = style.toJSON();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {

        }
    }

    public boolean copyNote(String srcId, String destId) {
        File src = DataSource.getNote(srcId);
        if (!src.exists()) {
            return false;
        }

        File dest = DataSource.getNote(destId);
        if (dest.exists()) {
            return false;
        }

        boolean result = true;

        try {
            FileUtils.copyDirectory(src, dest);
        } catch (IOException e) {
            try {
                FileUtils.forceDelete(dest);
            } catch (IOException e1) {

            }

            result = false;
        }

        return result;
    }

    public boolean createNoteFromAsset(String noteId, String templateId) {
        boolean result = true;

        File outDir = DataSource.getNote(noteId);
        outDir.mkdirs();

        String template = DataSource.getAssetTemplate(templateId);

        try {

            // 数据
            {
                AssetManager assets = App.instance().getAssets();
                String[] array = assets.list(template);

                for (String name : array) {
                    InputStream is = assets.open(template + "/" + name);

                    FileUtils.copyInputStreamToFile(is, new File(outDir, name));

                    is.close();
                }
            }

            // 写入ID
            {
                Identifier id = new Identifier(noteId, templateId);
                JSONObject json = id.toJSON();
                String text = json.toString();
                FileUtils.writeStringToFile(DataSource.getNoteId(noteId), text, "utf-8", false);
            }

        } catch (Exception e) {
            try {
                FileUtils.forceDelete(outDir);
            } catch (IOException e1) {

            }

            result = false;
        }

        return result;
    }

    public TemplateDataset getAssetTemplate() {

        JSONObject json = AssetUtils.readAssetToJSONObject(App.instance(), DataSource.getAssetTemplate());
        TemplateDataset ds = new TemplateDataset(json);

        return ds;
    }

    public NoteDataset getNoteDs() {
        JSONObject json = null;

        File file = DataSource.getNoteDs();
        if (file.exists()) {
            try {
                String text = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NoteDataset ds = new NoteDataset(json);
        return ds;
    }

    public void saveNoteDataset(NoteDataset ds) {
        File file = DataSource.getNoteDs();

        try {
            JSONObject json = ds.toJSON();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {

        }
    }

    public CatalogDataset getAssetCatalog() {

        JSONObject json = AssetUtils.readAssetToJSONObject(App.instance(), DataSource.getAssetCatalog());
        CatalogDataset ds = new CatalogDataset(json);

        return ds;
    }

    public CatalogDataset getCatalog() {
        JSONObject json = null;

        File file = DataSource.getCatalog();
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CatalogDataset ds = new CatalogDataset(json);
        return ds;
    }

    public void saveCatalog(CatalogDataset ds) {
        if (ds == null) {
            return;
        }

        JSONObject json = ds.toJson();
        if (json == null) {
            return;
        }

        try {
            File file = DataSource.getCatalog();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TypefaceDataset getAssetTypeface() {

        JSONObject json = AssetUtils.readAssetToJSONObject(App.instance(), DataSource.getAssetTypeface());
        TypefaceDataset ds = new TypefaceDataset(json);

        return ds;
    }

    public TypefaceDataset getTypeface() {
        JSONObject json = null;

        File file = DataSource.getTypefaceFile();
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file, "utf-8");
                json = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TypefaceDataset ds = new TypefaceDataset(json);
        return ds;
    }

    public void saveTypefaceDataset(TypefaceDataset ds) {
        if (ds == null) {
            return;
        }

        JSONObject json = ds.toJson();
        if (json == null) {
            return;
        }

        try {
            File file = DataSource.getTypefaceFile();
            String text = json.toString();
            FileUtils.writeStringToFile(file, text, "utf-8", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
