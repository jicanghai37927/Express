package com.haiyunshan.express.dataset.note;

import com.haiyunshan.express.dataset.note.entity.Entity;
import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;
import com.haiyunshan.express.dataset.note.entity.PictureEntity;
import com.haiyunshan.express.dataset.note.entity.StopEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记
 *
 */
public class Note {

    long mCreated;                  // 创建时间
    long mModified;                 // 修改时间

    ParagraphEntity mTitle;         // 标题
    ParagraphEntity mSubtitle;      // 副标题

    List<Entity> mBody;             // Body

    public Note() {

    }

    public Note(JSONObject json) {
        this.mCreated = json.optLong("created", -1);
        this.mModified = json.optLong("modified", -1);

        this.mTitle = new ParagraphEntity(json.optJSONObject("title"));
        this.mSubtitle = new ParagraphEntity(json.optJSONObject("subtitle"));

        {
            JSONArray array = json.optJSONArray("body");
            int size = (array == null) ? 0 : array.length();
            this.mBody = new ArrayList<>(size + 1);
            for (int i = 0; i < size; i++) {
                JSONObject obj = array.optJSONObject(i);
                if (obj == null) {
                    continue;
                }

                Entity en = null;

                String type = obj.optString("type");
                if (type.equalsIgnoreCase(Entity.TYPE_PARAGRAPH)) {
                    en = new ParagraphEntity(obj);
                } else if (type.equalsIgnoreCase(Entity.TYPE_STOP)) {
                    en = new StopEntity(obj);
                } else if (type.equalsIgnoreCase(Entity.TYPE_PICTURE)) {
                    en = new PictureEntity(obj);
                }

                if (en != null) {
                    mBody.add(en);
                }
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("created", this.mCreated);
            json.put("modified", this.mModified);

            json.put("title", mTitle.toJSON());
            json.put("subtitle", mSubtitle.toJSON());
            {
                JSONArray array = new JSONArray();
                for (Entity s : mBody) {
                    JSONObject obj = s.toJSON();
                    array.put(obj);
                }
                json.put("body", array);
            }

        } catch (JSONException e) {
            json = null;
        }

        return json;
    }

    public long getCreated() {
        return this.mCreated;
    }

    public void setCreated(long time) {
        this.mCreated = time;
    }

    public long getModified() {
        return this.mModified;
    }

    public void setModified(long time) {
        this.mModified = time;
    }

    public ParagraphEntity getTitle() {
        return this.mTitle;
    }

    public void setTitle(ParagraphEntity entity) {
        this.mTitle = entity;
    }

    public ParagraphEntity getSubtitle() {
        return this.mSubtitle;
    }

    public void setSubtitle(ParagraphEntity entity) {
        this.mSubtitle = entity;
    }

    public List<Entity> getBody() {
        return mBody;
    }

    public void setBody(List<Entity> list) {
        this.mBody = list;
    }

}
