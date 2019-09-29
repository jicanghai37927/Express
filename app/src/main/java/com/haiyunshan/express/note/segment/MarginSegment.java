package com.haiyunshan.express.note.segment;

import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;

/**
 * Created by sanshibro on 2018/2/7.
 */

public class MarginSegment extends FakeSegment {

    int mHeight;

    public MarginSegment(Document doc, int height) {
        super(doc);

        this.mType = Segment.TYPE_MARGIN;

        this.mHeight = height;
    }

    public int getHeight() {
        return mHeight;
    }
}
