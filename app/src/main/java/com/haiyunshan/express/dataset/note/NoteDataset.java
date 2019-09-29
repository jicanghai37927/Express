package com.haiyunshan.express.dataset.note;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记数据库
 *
 */
public class NoteDataset {

    ArrayList<NoteEntry> mList;     //
    ArrayList<NoteEntry> mTrash;    //

    public NoteDataset() {
        this.mList = new ArrayList<>();
        this.mTrash = new ArrayList<>();
    }

    public NoteDataset(JSONObject json) {
        if (json == null) {
            this.mList = new ArrayList<>();
            this.mTrash = new ArrayList<>();
        } else {

            JSONArray array = json.optJSONArray("list");
            this.mList = toList(array);

            array = json.optJSONArray("trash");
            this.mTrash = toList(array);
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {

            JSONArray array = toJSONArray(mList);
            json.put("list", array);

            array = toJSONArray(mTrash);
            json.put("trash", array);

        } catch (Exception e) {

        }

        return json;
    }

    List<NoteEntry> getList() {
        return this.mList;
    }

    List<NoteEntry> getTrashList() {
        return mTrash;
    }

    List<NoteEntry> getList(String catalogId) {
        if (TextUtils.isEmpty(catalogId)) {
            return mList;
        }

        ArrayList<NoteEntry> list = new ArrayList<>();
        for (NoteEntry e : mList) {
            if (e.getCatalog().equalsIgnoreCase(catalogId)) {
                list.add(e);
            }
        }

        return list;
    }

    int getCount(String catalog) {
        if (TextUtils.isEmpty(catalog)) {
            return this.size();
        }

        int count = 0;

        String catalogId = catalog;
        for (NoteEntry e : mList) {
            if (e.getCatalog().equalsIgnoreCase(catalogId)) {
                ++count;
            }
        }

        return count;
    }

    NoteEntry obtain(String id) {
        NoteEntry entry = null;

        for (NoteEntry e : mList) {
            if (e.mId.compareTo(id) == 0) {
                entry = e;
                break;
            }
        }

        return entry;
    }

    NoteEntry find(String name) {
        for (NoteEntry e : mList) {
            if (e.mName.compareTo(name) == 0) {
                return e;
            }
        }

        return null;
    }

    /**
     *
     * @param name
     * @param desc
     * @return
     */
    NoteEntry put(String id, String name, String desc) {
        NoteEntry e = new NoteEntry(id, name, desc);
        mList.add(e);

        return e;
    }

    void restore(NoteEntry e) {
        mTrash.remove(e);
        e.setDeleted(-1);
        mList.add(e);
    }

    boolean trash(NoteEntry e) {
        boolean result = mList.remove(e);
        if (result) {
            e.setDeleted(System.currentTimeMillis());
            mTrash.add(e);
        }

        return result;
    }

    boolean remove(NoteEntry e) {
        boolean r1 = mList.remove(e);
        boolean r2 = mTrash.remove(e);

        return (r1 || r2);
    }

    int getTrashCount() {
        int size = mTrash.size();

        return size;
    }

    int size() {
        return mList.size();
    }

    ArrayList<NoteEntry> toList(JSONArray array) {
        int size = (array == null) ? 0 : array.length();

        ArrayList<NoteEntry> list = new ArrayList<>(size + 1);
        for (int i = 0; i < size; i++) {
            JSONObject obj = array.optJSONObject(i);
            NoteEntry e = new NoteEntry(obj);
            list.add(e);
        }

        return list;
    }

    JSONArray toJSONArray(ArrayList<NoteEntry> list) {
        JSONArray array = new JSONArray();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            JSONObject obj = list.get(i).toJson();
            array.put(obj);
        }

        return array;
    }
}
