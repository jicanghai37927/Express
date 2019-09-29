package com.haiyunshan.express.dataset.template;

import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.DataManager;

/**
 *
 */
public class TemplateManager {

    TemplateDataset mAsset;

    public static final TemplateManager instance() {
        return DataManager.instance().getTemplateManager();
    }

    public TemplateManager() {

    }

    public TemplateDataset getAsset() {
        if (this.mAsset == null) {
            mAsset = DataAccessor.instance().getAssetTemplate();
        }

        return mAsset;
    }

    public TemplateEntry getDefault() {
        TemplateDataset ds = this.getAsset();
        return ds.get(0);
    }

    public TemplateEntry obtain(String id) {
        TemplateEntry entry = getAsset().obtain(id);
        return entry;
    }

    public boolean createNote(String id, TemplateEntry template) {
        boolean exist = DataAccessor.instance().isNoteExist(id);
        if (exist) {
            return true;
        }

        TemplateEntry entry = template;
        if (entry == null) {
            return false;
        }

        boolean result = true;
        if (getAsset().obtain(template.getId()) != null) {
            result = DataAccessor.instance().createNoteFromAsset(id, template.getId());
        }

        return result;
    }
}
