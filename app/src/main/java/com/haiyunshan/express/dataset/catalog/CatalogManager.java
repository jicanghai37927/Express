package com.haiyunshan.express.dataset.catalog;

import android.text.TextUtils;

import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.DataManager;
import com.haiyunshan.express.dataset.note.NoteEntry;
import com.haiyunshan.express.dataset.note.NoteManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CatalogManager {

    static final int sortAll        = 1000;
    static final int sortAsset      = 2000;
    static final int sortUser       = 3000;
    static final int sortTrash      = 4000;

    public static final String idAll        = "all";
    public static final String idTrash      = "trash";

    CatalogEntry mAllEntry;     // 所有笔记
    CatalogEntry mTrashEntry;   // 删除的笔记

    CatalogDataset mAsset;      // 内置目录
    CatalogDataset mDataset;    // 用户目录

    public static final CatalogManager instance() {
        return DataManager.instance().getCatalogManager();
    }

    public CatalogManager() {
        this.mAsset = getAsset();

        {
            String id = idAll;
            String name = "所有笔记";

            this.mAllEntry = new CatalogEntry(id, name);
            mAllEntry.setEditable(false);
            mAllEntry.setSort(sortAll);
        }

        {
            String id = idTrash;
            String name = "最近删除";

            this.mTrashEntry = new CatalogEntry(id, name);
            mTrashEntry.setEditable(false);
            mTrashEntry.setSort(sortTrash);
        }
    }

    public boolean isAll(CatalogEntry entry) {
        return (entry == mAllEntry);
    }

    public boolean isTrash(CatalogEntry entry) {
        return (entry == mTrashEntry);
    }

    public List<CatalogEntry> getList() {
        ArrayList<CatalogEntry> list = new ArrayList<>();

        // 所有笔记
        if (NoteManager.instance().size() > 0) {
            list.add(CatalogManager.instance().getAllEntry());
        }

        // 最近删除
        if (NoteManager.instance().getTrashCount() > 0) {
            list.add(CatalogManager.instance().getTrashEntry());
        }

        // 内置的分类
        {
            CatalogDataset ds = this.getAsset();
            list.addAll(ds.getList());
        }

        // 用户定义的分类
        {
            CatalogDataset ds = this.getDataset();
            list.addAll(ds.getList());
        }

        return list;
    }

    public List<CatalogEntry> getUserList() {
        return getDataset().getList();
    }

    public int getCount(CatalogEntry entry) {
        NoteManager mgr = NoteManager.instance();

        if (entry == null) {
            return mgr.size();
        }

        if (entry == mAllEntry) {
            return mgr.size();
        }

        if (entry == mTrashEntry) {
            return mgr.getTrashCount();
        }

        int count = mgr.getCount(entry.getId());
        return count;
    }

    public List<NoteEntry> getList(CatalogEntry entry) {
        NoteManager mgr = NoteManager.instance();

        if (entry == null) {
            return mgr.getList();
        }

        if (entry == mAllEntry) {
            return mgr.getList();
        }

        if (entry == mTrashEntry) {
            return mgr.getTrashList();
        }

        List<NoteEntry> list = mgr.getList(entry.getId());
        return list;
    }

    public CatalogEntry create(String name) {
        String id = UUIDUtils.next();

        CatalogEntry e = new CatalogEntry(id, name);
        e.setEditable(true);
        e.setSort(sortUser);

        CatalogDataset ds = getDataset();
        ds.add(e);

        return e;
    }

    public CatalogEntry getDefault() {
        return getAsset().getList().get(0);
    }

    public CatalogEntry obtain(String id) {
        return this.obtain(id, true);
    }

    public CatalogEntry obtain(String id, boolean useDefault) {
        CatalogEntry e = null;

        while (true) {
            if (TextUtils.isEmpty(id)) {
                break;
            }

            if (mAllEntry.getId().equalsIgnoreCase(id)) {
                e = mAllEntry;
                break;
            }

            if (mTrashEntry.getId().equalsIgnoreCase(id)) {
                e = mTrashEntry;
                break;
            }

            if (e == null) {
                mDataset = this.getDataset();
                e = mDataset.obtain(id);
            }

            if (e == null) {
                e = mAsset.obtain(id);
            }

            break;
        }

        if (e == null) {
            e = useDefault? this.getDefault(): e;
        }

        return e;
    }

    String getName(String name) {

        String realName = name;
        for (int i = 2; ; i++) {
            CatalogEntry e = this.find(realName);
            if (e == null) {
                break;
            }

            realName = name + " " + i;
        }

        name = realName;
        return name;
    }

    public CatalogEntry find(String name) {
        CatalogEntry e = null;

        while (true) {
            if (mAllEntry.getName().equalsIgnoreCase(name)) {
                e = mAllEntry;
                break;
            }

            if (mTrashEntry.getName().equalsIgnoreCase(name)) {
                e = mTrashEntry;
                break;
            }

            if (e == null) {
                mDataset = this.getDataset();
                e = mDataset.find(name);
            }

            if (e == null) {
                e = mAsset.find(name);
            }

            break;
        }

        return e;
    }

    public boolean isEmpty() {
        return getDataset().isEmpty();
    }

    public void clear() {
        getDataset().clear();
    }

    public void restore(NoteEntry entry) {
        String id = entry.getCatalog();
        CatalogEntry e = this.obtain(id, false);
        if (e != null) {
            return;
        }

        CatalogDataset ds = this.getDataset();
        e = ds.obtainTrash(id);
        if (e == null) {
            entry.setCatalog(this.getDefault().getId());
        } else {

            e.setName(this.getName(e.getName()));

            ds.restore(e);
        }

    }

    public void trash(CatalogEntry entry) {
        getDataset().trash(entry);
    }

    public void save() {
        if (mDataset != null) {
            DataAccessor.instance().saveCatalog(mDataset);
        }
    }

    public CatalogEntry getAllEntry() {
        return this.mAllEntry;
    }

    public CatalogEntry getTrashEntry() {
        return this.mTrashEntry;
    }

    CatalogDataset getAsset() {
        if (mAsset != null) {
            return mAsset;
        }

        mAsset = DataAccessor.instance().getAssetCatalog();
        mAsset.setEditable(false);
        List<CatalogEntry> list = mAsset.getList();
        for (int i = 0; i < list.size(); i++) {
            mAsset.setSort(sortAsset + i);
        }

        return mAsset;
    }

    CatalogDataset getDataset() {
        if (mDataset != null) {
            return mDataset;
        }

        mDataset = DataAccessor.instance().getCatalog();
        mDataset.setEditable(true);
        mDataset.setSort(sortUser);

        return mDataset;
    }

}
