package com.haiyunshan.express.note.segment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.haiyunshan.express.dataset.note.entity.PictureEntity;
import com.haiyunshan.express.dataset.note.entity.StopEntity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;

/**
 *
 */
public class PictureSegment extends Segment {

    PictureEntity mEntity;

    public PictureSegment(Document doc, String source) {
        super(doc);

        this.mType = Segment.TYPE_PICTURE;

        this.mEntity = new PictureEntity(this.getId(), this.getType(this));

        int[] size = getImageWidthHeight(source);
        mEntity.setSource(source, size[0], size[1]);
    }

    public PictureSegment(Document doc, PictureEntity entity) {
        super(doc, entity);

        this.mEntity = entity;
    }

    @Override
    public PictureEntity toEntity() {
        PictureEntity entity = this.mEntity;
        entity.setId(this.mId);

        entity.setCreated(this.mCreated);
        entity.setModified(this.mModified);

        return entity;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public String getName() {
        return mEntity.getName();
    }

    public String getUri() {
        return mEntity.getSource();
    }

    public int getWidth() {
        return mEntity.getWidth();
    }

    public int getHeight() {
        return mEntity.getHeight();
    }

    static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null

        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }
}
