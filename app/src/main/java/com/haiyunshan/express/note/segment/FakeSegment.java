package com.haiyunshan.express.note.segment;

import com.haiyunshan.express.dataset.note.entity.Entity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;

/**
 * Created by sanshibro on 2018/2/7.
 */

public class FakeSegment extends Segment {

    public FakeSegment(Document doc) {
        super(doc);
    }

    @Override
    public Entity toEntity() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
