package com.haiyunshan.express.dataset.catalog;

import com.haiyunshan.express.dataset.note.NoteEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CatalogDataset {

    ArrayList<CatalogEntry> mList;
    ArrayList<CatalogEntry> mTrash;

    public CatalogDataset(JSONObject json) {

        if (json == null) {
            mList = new ArrayList<>();
            mTrash = new ArrayList<>();
        } else {
            JSONArray array = json.optJSONArray("list");
            this.mList = toList(array);

            array = json.optJSONArray("trash");
            this.mTrash = toList(array);
        }
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {

            JSONArray array = toJSONArray(mList);
            json.put("list", array);

            if (mTrash != null && !mTrash.isEmpty()) {
                array = toJSONArray(mTrash);
                json.put("trash", array);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    void setEditable(boolean editable) {
        for (CatalogEntry e : mList) {
            e.setEditable(editable);
        }
        for (CatalogEntry e : mTrash) {
            e.setEditable(editable);
        }
    }

    public void setSort(int sort) {
        for (CatalogEntry e : mList) {
            e.setSort(sort);
        }
        for (CatalogEntry e : mTrash) {
            e.setSort(sort);
        }
    }

    boolean isEmpty() {
        return mList.isEmpty();
    }

    int size() {
        return mList.size();
    }

    List<CatalogEntry> getList() {
        return this.mList;
    }

    int indexOf(CatalogEntry e) {
        return mList.indexOf(e);
    }

    CatalogEntry obtain(String id) {
        for (CatalogEntry e : mList) {
            if (e.mId.compareTo(id) == 0) {
                return e;
            }
        }

        return null;
    }

    CatalogEntry find(String name) {
        for (CatalogEntry e : mList) {
            if (e.mName.compareTo(name) == 0) {
                return e;
            }
        }

        return null;
    }

    void add(CatalogEntry entry) {
        mList.add(entry);
    }

    List<CatalogEntry> getTrashList() {
        return mTrash;
    }

    boolean trash(CatalogEntry e) {
        boolean result = mList.remove(e);
        if (result) {
            e.setDeleted(System.currentTimeMillis());
            mTrash.add(e);
        }

        return result;
    }

    CatalogEntry obtainTrash(String id) {
        for (CatalogEntry e : mTrash) {
            if (e.getId().equalsIgnoreCase(id)) {
                return e;
            }
        }

        return null;
    }

    void restore(CatalogEntry e) {
        e.setDeleted(-1);

        mTrash.remove(e);
        mList.add(e);
    }

    boolean remove(CatalogEntry entry) {
        boolean r1 = mList.remove(entry);
        boolean r2 = mTrash.remove(entry);

        return (r1 || r2);
    }

    void clear() {
        mList.clear();
    }

    ArrayList<CatalogEntry> toList(JSONArray array) {
        int size = (array == null) ? 0 : array.length();

        ArrayList<CatalogEntry> list = new ArrayList<>(size + 1);
        for (int i = 0; i < size; i++) {
            JSONObject obj = array.optJSONObject(i);
            CatalogEntry e = new CatalogEntry(obj);
            list.add(e);
        }

        return list;
    }

    JSONArray toJSONArray(ArrayList<CatalogEntry> list) {
        JSONArray array = new JSONArray();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            JSONObject obj = list.get(i).toJson();
            array.put(obj);
        }

        return array;
    }
}
