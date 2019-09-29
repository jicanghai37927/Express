package com.haiyunshan.express.typeface;

import com.haiyunshan.express.app.UUIDUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TypefaceDataset {

    ArrayList<TypefaceEntry> mList;

    public TypefaceDataset(JSONObject json) {
        if (json == null) {
            this.mList = new ArrayList<>();
        } else {
            JSONArray array = json.optJSONArray("list");
            int size = (array == null)? 0: array.length();

            this.mList = new ArrayList<>(size + 1);
            for (int i = 0; i < size; i++) {
                JSONObject obj = array.optJSONObject(i);
                TypefaceEntry e = new TypefaceEntry(obj);
                if (e.isEnable()) {
                    mList.add(e);
                }
            }
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {

            JSONArray array = new JSONArray();
            for (TypefaceEntry e : mList) {
                JSONObject obj = e.toJson();
                array.put(obj);
            }

            json.put("list", array);
        } catch (JSONException e) {

        }

        return json;
    }

    public List<TypefaceEntry> getList() {
        return mList;
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public int size() {
        return mList.size();
    }

    public void clear() {
        mList.clear();
    }

    public TypefaceEntry add(String name) {
        String id = UUIDUtils.next();
        TypefaceEntry e = new TypefaceEntry(id, name);
        mList.add(e);

        return e;
    }

    public boolean remove(TypefaceEntry entry) {
        boolean r = mList.remove(entry);
        return r;
    }

    public TypefaceEntry obtain(String id) {
        for (TypefaceEntry e : mList) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }

    public TypefaceEntry obtainBySource(String source) {
        for (TypefaceEntry e : mList) {
            if (e.getSource().equals(source)) {
                return e;
            }
        }

        return null;
    }

    public int indexOf(TypefaceEntry e) {
        int index = mList.indexOf(e);
        return index;
    }


}
