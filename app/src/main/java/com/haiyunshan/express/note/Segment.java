package com.haiyunshan.express.note;

import android.text.TextUtils;

import com.haiyunshan.express.app.UUIDUtils;
import com.haiyunshan.express.dataset.note.entity.Entity;

/**
 *
 */
public abstract class Segment {

    public static final int TYPE_PARAGRAPH  = 101; // 段落
    public static final int TYPE_MARGIN     = 201;
    public static final int TYPE_STOP       = 301;
    public static final int TYPE_PICTURE    = 401;

    protected String mId;
    protected int mType;

    protected long mCreated;
    protected long mModified;

    protected Document mDocument;

    public Segment(Document doc) {
        this.mDocument = doc;

        this.mId = UUIDUtils.next();
        this.mType = TYPE_PARAGRAPH;

        this.mCreated = System.currentTimeMillis();
        this.mModified = System.currentTimeMillis();
    }

    public Segment(Document doc, Entity entity) {
        this.mDocument = doc;

        this.mId = entity.getId();
        this.mType = this.getType(entity);

        this.mCreated = entity.getCreated();
        this.mCreated = (this.mCreated <= 0)? System.currentTimeMillis(): this.mCreated;

        this.mModified = entity.getModified();
        this.mModified = (this.mModified <= 0)? System.currentTimeMillis(): this.mModified;
    }

    public abstract Entity toEntity();

    public Document getDocument() {
        return this.mDocument;
    }

    public String getId() {
        return this.mId;
    }

    public int getType() {
        return this.mType;
    }

    public long getCreated() {
        return this.mCreated;
    }

    public long getModified() {
        return this.mModified;
    }

    public void setModified(long time) {
        this.mModified = time;
    }

    public boolean isEmpty() {
        return false;
    }

    protected int getType(Entity entity) {
        int value = -1;

        String type = entity.getType();
        if (type.equalsIgnoreCase(Entity.TYPE_PARAGRAPH)) {
            value = TYPE_PARAGRAPH;
        } else if (type.equalsIgnoreCase(Entity.TYPE_STOP)) {
            value = TYPE_STOP;
        } else if (type.equalsIgnoreCase(Entity.TYPE_PICTURE)) {
            value = TYPE_PICTURE;
        }

        if (value < 0) {
            throw new IllegalArgumentException("segment type not found");
        }

        return value;
    }

    protected String getType(Segment segment) {
        String value = null;

        int type = segment.mType;
        if (type == TYPE_PARAGRAPH) {
            value = Entity.TYPE_PARAGRAPH;
        } else if (type == TYPE_STOP) {
            value = Entity.TYPE_STOP;
        } else if (type == TYPE_PICTURE) {
            value = Entity.TYPE_PICTURE;
        }

        if (TextUtils.isEmpty(value)) {
            throw new IllegalArgumentException("entity type not found");
        }

        return value;
    }
}
