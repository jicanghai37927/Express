package com.haiyunshan.express.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.haiyunshan.express.App;
import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.DataManager;
import com.haiyunshan.express.dataset.DataSource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 *
 */
public class TypefaceManager {

    HashMap<String, WeakReference<Typeface>> mTypefaceMap;

    TypefaceDataset mAsset;     // 内置字体
    TypefaceDataset mDataset;   // 用户字体

    LocalTypefaceDataset mLocal;

    public static final TypefaceManager instance() {
        return DataManager.instance().getTypefaceManager();
    }

    public TypefaceManager() {
        this.mTypefaceMap = new HashMap<>();
    }

    public TypefaceDataset getAsset() {
        if (mAsset == null) {
            this.mAsset = DataAccessor.instance().getAssetTypeface();
        }

        return this.mAsset;
    }

    public TypefaceDataset getDataset() {
        if (mDataset == null) {
            this.mDataset = DataAccessor.instance().getTypeface();
        }

        return this.mDataset;
    }

    public LocalTypefaceDataset getLocalTypefaceDataset() {
        return mLocal;
    }

    public void setLocalTypefaceDataset(LocalTypefaceDataset ds) {
        this.mLocal = ds;
    }

    /**
     *
     * @param id
     * @return
     */
    public Typeface getTypeface(String id, boolean bold, boolean italic) {
        Typeface tf = this.createTypeface(id, bold, italic);
        if (tf == null) {
            tf = Typeface.DEFAULT;
        }

        return tf;
    }

    public Typeface getTypeface(TypefaceEntry entry, boolean bold, boolean italic) {
        if (entry == null) {
            return Typeface.DEFAULT;
        }

        String id = entry.getId();
        Typeface tf = this.createTypeface(id, bold, italic);
        if (tf == null) {
            tf = Typeface.DEFAULT;
        }

        return tf;
    }

    public TypefaceEntry obtainBySource(String source) {
        TypefaceEntry e = this.getDataset().obtainBySource(source);
        if (e != null) {
            return e;
        }

        e = this.getAsset().obtainBySource(source);
        return e;
    }

    public TypefaceEntry obtain(String id) {
        TypefaceEntry e = this.getDataset().obtain(id);
        if (e != null) {
            return e;
        }

        e = getAsset().obtain(id);
        return e;
    }

    public boolean add(String name, String path) {
        TypefaceEntry e = this.obtainBySource(path);
        if (e != null) {
            return true;
        }

        this.mDataset = this.getDataset();
        e = mDataset.add(name);
        File file = DataSource.getTypefaceFile(e.getUri());
        try {
            FileUtils.copyFile(new File(path), file);
        } catch (IOException e1) {
            file = null;
        }

        if (file == null) {
            mDataset.remove(e);
        } else {
            e.setSource(path);

            this.save();
        }

        return (file != null);
    }

    public void removeFontFile(TypefaceEntry entry) {
        if (entry == null || entry.getType() != TypefaceEntry.typeFile) {
            return;
        }

        File file = DataSource.getTypefaceFile(entry.getUri());
        if (file.exists()) {
            file.delete();
        }
    }

    Typeface createTypeface(String id, boolean bold, boolean italic) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        Typeface tf = null;

        // 追加风格
        int style = Typeface.NORMAL;
        if (bold) {
            style = style | Typeface.BOLD;
        }
        if (italic) {
            style = style | Typeface.ITALIC;
        }
        String key = id + style;

        // 访问缓存
        WeakReference<Typeface> ref = mTypefaceMap.get(key);
        if (ref != null) {
            tf = ref.get();
            if (tf == null) {
                mTypefaceMap.remove(key);
            }
        }

        // 创建字体
        if (tf == null) {
            TypefaceEntry e = this.obtain(id);
            if (e != null) {
                tf = this.createTypeface(e, bold, italic);
                if (tf != null) {
                    mTypefaceMap.put(key, new WeakReference<>(tf));
                }
            }
        }

        return tf;
    }

    Typeface createTypeface(TypefaceEntry entry, boolean bold, boolean italic) {
        Typeface font = null;

        boolean isSystem = isSystem(entry);
        if (isSystem) {
            font = Typeface.DEFAULT;

            String uri = entry.getUri();
            if (uri.equals("sans-serif")) {
                font = Typeface.SANS_SERIF;
            } else if (uri.equals("serif")) {
                font = Typeface.SERIF;
            } else if (uri.equals("monospace")) {
                font = Typeface.MONOSPACE;
            }
        } else {
            boolean isAsset = this.isAssetFont(entry);
            if (isAsset) {
                String path = DataSource.getAssetTypeface(entry.getUri());
                Context context = App.instance();
                font = Typeface.createFromAsset(context.getAssets(), path);
            } else {
                File path = DataSource.getTypefaceFile(entry.getUri());
                font = Typeface.createFromFile(path);
            }
        }

        int style = Typeface.NORMAL;
        if (bold) {
            style = style | Typeface.BOLD;
        }
        if (italic) {
            style = style | Typeface.ITALIC;
        }

        if (style != Typeface.NORMAL) {
            font = Typeface.create(font, style);
        }

        return font;
    }

    public boolean isEditable(TypefaceEntry e) {
        return (e.getType() == TypefaceEntry.typeFile);
    }

    boolean isSystem(TypefaceEntry e) {
        return (e.getType() == TypefaceEntry.typeSystem);
    }

    boolean isAssetFont(TypefaceEntry e) {
        return (e.getType() == TypefaceEntry.typeAsset);
    }

    public void save() {
        if (mDataset != null) {
            DataAccessor.instance().saveTypefaceDataset(mDataset);
        }
    }
}
