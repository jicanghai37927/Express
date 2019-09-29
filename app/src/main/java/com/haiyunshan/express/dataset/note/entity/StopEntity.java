package com.haiyunshan.express.dataset.note.entity;

import android.view.View;

import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.note.style.StyleEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class StopEntity extends Entity {

    public static final StopEntity create() {
        String id = UUIDUtils.next();
        String type = Entity.TYPE_STOP;

        return new StopEntity(id, type);
    }

    public StopEntity(String id, String type) {
        super(id, type);
    }

    public StopEntity(JSONObject json) {
        super(json);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        if (json == null) {
            return json;
        }

        return json;
    }

}
