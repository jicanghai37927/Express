package com.haiyunshan.express.dataset.note.style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class Style {

    ArrayList<StyleEntity> mList;       // 样式

    Style() {
        this.mList = new ArrayList<>();
    }

    Style(Style ds) {
        this.mList = new ArrayList<>(ds.getList().size());
        for (StyleEntity e : ds.mList) {
            mList.add(new StyleEntity(e));
        }
    }

    public Style(JSONObject json) {
        if (json == null) {
            this.mList = new ArrayList<>();

        } else {
            {
                JSONArray array = json.optJSONArray("list");
                int size = (array == null)? 0: array.length();
                this.mList = new ArrayList<>(size + 1);

                for (int i = 0; i < size; i++) {
                    JSONObject obj = array.optJSONObject(i);
                    StyleEntity e = new StyleEntity(obj);
                    mList.add(e);
                }
            }
        }

        // 填充默认值
        this.fill();
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            {
                JSONArray array = new JSONArray();
                for (StyleEntity e : mList) {
                    JSONObject obj = e.toJson();
                    array.put(obj);
                }

                if (array.length() != 0) {
                    json.put("list", array);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public List<StyleEntity> getList() {
        return mList;
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public StyleEntity obtain(String id) {
        for (StyleEntity e : mList) {
            if (e.getId().equalsIgnoreCase(id)) {
                return e;
            }
        }

        return null;
    }

    public StyleEntity find(String name) {
        for (StyleEntity en : mList) {
            if (en.getName().equalsIgnoreCase(name)) {
                return en;
            }
        }

        return null;
    }

    public void fill() {
        for (StyleEntity en : mList) {
            en.fill();
        }
    }

}
