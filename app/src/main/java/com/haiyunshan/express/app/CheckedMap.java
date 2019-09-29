package com.haiyunshan.express.app;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @param <T>
 */
public class CheckedMap<T> {

    HashMap<String, T> mMap;

    public CheckedMap() {
        this.mMap = new HashMap<>();

    }

    public boolean isEmpty() {
        return mMap.isEmpty();
    }

    public int size() {
        return mMap.size();
    }

    public List<T> getList() {
        ArrayList<T> list = new ArrayList<>(size() + 1);
        list.addAll(mMap.values());

        return list;
    }

    public ArrayList<String> getIds() {
        ArrayList<String> list = new ArrayList<>(size() + 1);
        list.addAll(mMap.keySet());
        return list;
    }

    public Collection<T> values() {
        return mMap.values();
    }

    public boolean isChecked(String id) {
        return mMap.containsKey(id);
    }

    public boolean isChecked(T entry) {
        return mMap.containsValue(entry);
    }

    public void setChecked(String id, T entry, boolean isChecked) {
        if (isChecked) {
            mMap.put(id, entry);
        } else {
            mMap.remove(id);
        }
    }

    public T peek() {
        if (mMap.values().size() == 0) {
            return null;
        }

        T value = mMap.values().iterator().next();
        return value;
    }

    public void clear() {
        mMap.clear();
    }

}
