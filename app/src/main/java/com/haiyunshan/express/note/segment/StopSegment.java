package com.haiyunshan.express.note.segment;

import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TabStopSpan;

import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;
import com.haiyunshan.express.dataset.note.entity.StopEntity;
import com.haiyunshan.express.dataset.note.style.StyleEntity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;

/**
 *
 */
public class StopSegment extends Segment {

    public StopSegment(Document doc) {
        super(doc);

        this.mType = Segment.TYPE_STOP;
    }

    public StopSegment(Document doc, StopEntity entity) {
        super(doc, entity);
    }

    @Override
    public StopEntity toEntity() {
        StopEntity entity = new StopEntity(this.mId, this.getType(this));
        entity.setId(this.mId);

        entity.setCreated(this.mCreated);
        entity.setModified(this.mModified);

        return entity;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
