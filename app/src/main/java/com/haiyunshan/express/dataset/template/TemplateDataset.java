package com.haiyunshan.express.dataset.template;

import com.haiyunshan.express.dataset.note.NoteEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 */
public class TemplateDataset {

    ArrayList<TemplateEntry> mList;

    public TemplateDataset(JSONObject json) {
        if (json == null) {
            this.mList = new ArrayList<>();
        } else {
            JSONArray array = json.optJSONArray("list");
            int size = (array == null)? 0: array.length();

            this.mList = new ArrayList<>(size + 1);
            for (int i = 0; i < size; i++) {
                JSONObject obj = array.optJSONObject(i);
                TemplateEntry e = new TemplateEntry(obj);
                mList.add(e);
            }
        }
    }

    boolean isEmpty() {
        return mList.isEmpty();
    }

    TemplateEntry get(int index) {
        return mList.get(index);
    }

    TemplateEntry obtain(String id) {
        TemplateEntry entry = null;

        for (TemplateEntry e : mList) {
            if (e.mId.compareTo(id) == 0) {
                entry = e;
                break;
            }
        }

        return entry;
    }

}
