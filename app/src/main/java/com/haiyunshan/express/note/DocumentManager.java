package com.haiyunshan.express.note;

import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.DataManager;
import com.haiyunshan.express.dataset.note.Identifier;
import com.haiyunshan.express.dataset.note.Note;
import com.haiyunshan.express.dataset.template.TemplateManager;

/**
 *
 */
public class DocumentManager {

    Document mCache;

    public static final DocumentManager instance() {
        return DataManager.instance().getDocumentManager();
    }

    public DocumentManager() {

    }

    public Document obtain(String id) {
        if (mCache != null && mCache.getId().equalsIgnoreCase(id)) {
            return mCache;
        }

        if (!DataAccessor.instance().isNoteExist(id)) {
            TemplateManager mgr = TemplateManager.instance();
            mgr.createNote(id, mgr.getDefault());
        }

        DataAccessor accessor = DataAccessor.instance();
        Note note = accessor.getNoteFile(id);

        Identifier identifier = accessor.getNoteId(id);
        Document doc = new Document(identifier, note);

        doc.mPage = accessor.getNotePage(id);
        doc.mStyle = accessor.getNoteStyle(id);

        this.mCache = doc;
        return doc;
    }

    public void clearCache() {
        this.mCache = null;
    }
}
