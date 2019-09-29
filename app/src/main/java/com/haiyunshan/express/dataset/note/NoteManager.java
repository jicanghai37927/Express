package com.haiyunshan.express.dataset.note;

import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.DataManager;
import com.haiyunshan.express.dataset.catalog.CatalogEntry;
import com.haiyunshan.express.dataset.catalog.CatalogManager;

import java.util.List;

/**
 *
 */
public class NoteManager {

    NoteDataset mDs;

    public static NoteManager instance() {
        return DataManager.instance().getNoteManager();
    }

    public NoteManager() {

    }

    public NoteEntry obtain(String id) {
        NoteEntry entry = getDataset().obtain(id);
        return entry;
    }

    public NoteDataset getDataset() {
        if (mDs == null) {
            mDs = DataAccessor.instance().getNoteDs();
        }

        return mDs;
    }

    public NoteEntry create(CatalogEntry catalog, String name) {
        return create(UUIDUtils.next(), catalog, name);
    }

    public NoteEntry create(String id, CatalogEntry catalog, String name) {
        NoteEntry entry = this.obtain(id);
        if (entry != null) {
            return entry;
        }

        CatalogManager mgr = CatalogManager.instance();
        CatalogEntry folder = catalog;
        if (folder == null || mgr.isAll(folder) || mgr.isTrash(folder)) {
            folder = mgr.getDefault();
        }

        name = this.getName(name, folder.getId());
        String desc = "";

        NoteDataset ds = this.getDataset();

        entry = ds.put(id, name, desc);
        entry.setCatalog(folder.getId());

        return entry;
    }

    public NoteEntry copy(NoteEntry src) {
        CatalogEntry catalog = CatalogManager.instance().obtain(src.getCatalog());
        NoteEntry dest = this.create(catalog, src.getName());
        dest.setDesc(src.getDesc());

        boolean result = this.copy(src, dest);
        if (!result) {
            this.remove(dest);
            dest = null;
        }

        return dest;
    }

    boolean copy(NoteEntry src, NoteEntry dest) {
        boolean result = DataAccessor.instance().copyNote(src.getId(), dest.getId());
        if (!result) {
            return result;
        }

        // 更新ID信息
        Identifier id = DataAccessor.instance().getNoteId(dest.getId());
        id.setId(dest.getId());
        id.setCreated(dest.getCreated());
        id.setModified(dest.getModified());

        DataAccessor.instance().saveNoteId(id);

        return result;
    }

    String getName(String name, String catalogId) {

        NoteDataset ds = this.getDataset();
        List<NoteEntry> list = ds.getList(catalogId);

        String realName = name;
        for (int i = 2; ; i++) {
            int pos = this.indexOf(realName, list);
            if (pos < 0) {
                break;
            }

            realName = name + " " + i;
        }

        name = realName;
        return name;
    }

    int indexOf(String name, List<NoteEntry> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            NoteEntry e = list.get(i);
            if (e.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }

        return -1;
    }

    public int size() {
        NoteDataset ds = this.getDataset();
        int size = ds.size();

        return size;
    }

    public void restore(NoteEntry entry) {
        entry.setName(this.getName(entry.getName(), entry.getCatalog()));

        getDataset().restore(entry);
    }

    public void trash(NoteEntry entry) {
        NoteDataset ds = this.getDataset();
        ds.trash(entry);
    }

    public int getTrashCount() {
        NoteDataset ds = this.getDataset();
        return ds.getTrashCount();
    }

    public int getCount() {
        NoteDataset ds = this.getDataset();
        return ds.size();
    }

    public int getCount(String catalog) {
        NoteDataset ds = this.getDataset();
        int count = ds.getCount(catalog);
        return count;
    }

    public void remove(NoteEntry entry) {
        NoteDataset ds = this.getDataset();
        ds.remove(entry);

        DataAccessor.instance().removeNote(entry.getId());
    }

    public void save() {
        if (mDs != null) {
            DataAccessor.instance().saveNoteDataset(mDs);
        }
    }

    public List<NoteEntry> getList() {
        return getList(null);
    }

    public List<NoteEntry> getList(String catalog) {
        NoteDataset ds = this.getDataset();
        return ds.getList(catalog);
    }

    public List<NoteEntry> getTrashList() {
        NoteDataset ds = this.getDataset();
        return ds.getTrashList();
    }
}
