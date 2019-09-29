package com.haiyunshan.express.note.holder;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.express.fragment.note.NoteComposeFragment;
import com.haiyunshan.express.dataset.note.entity.Entity;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by sanshibro on 2017/11/9.
 */

public class SegmentHolder<SEG extends Segment> extends RecyclerView.ViewHolder {

    public static final int TYPE_TITLE      = 1;
    public static final int TYPE_SUBTITLE   = 2;

    public static final int TYPE_PARAGRAPH  = 101;

    int mType;

    int mPosition;
    SEG mSegment;

    NoteComposeFragment mContext;

    public SegmentHolder(NoteComposeFragment context, View itemView) {
        super(itemView);

        this.mContext = context;
    }

    public void bind(int position, SEG entity) {
        this.mPosition = position;
        this.mSegment = entity;
    }

    public int getType() {
        return this.mType;
    }

    public Document getNote() {
        return mContext.getDocument();
    }

    public SEG getEntity() {
        return this.mSegment;
    }

    public void applyChanged() {

    }

    public void onViewAttachedToWindow() {

    }

    public void onViewDetachedFromWindow() {

    }

    public void onDrawDecoration(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    public void onDrawDecorationOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    public void getItemDecorationOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

    }

    public void onModeChanged(int oldMode, int newMode) {

    }

    public void clearFocus() {

    }

    public void requestFocus() {

    }

    public void beginFormat() {

    }

    public void applyStyle() {

    }

    int getPagePaddingLeft() {
        int value = getNote().getPage().getPaddingLeft();
        value = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, value, mContext.getResources().getDisplayMetrics());
        return value;
    }

    int getPagePaddingTop() {
        int value = getNote().getPage().getPaddingTop();
        value = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, value, mContext.getResources().getDisplayMetrics());
        return value;
    }

    int getPagePaddingRight() {
        int value = getNote().getPage().getPaddingRight();
        value = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, value, mContext.getResources().getDisplayMetrics());
        return value;
    }

    int getPagePaddingBottom() {
        int value = getNote().getPage().getPaddingBottom();
        value = (int)TypedValue.applyDimension(COMPLEX_UNIT_DIP, value, mContext.getResources().getDisplayMetrics());
        return value;
    }

    public static final SegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int viewType) {
        SegmentHolder holder = null;

        int itemType = viewType;
        switch (itemType) {
            case TYPE_TITLE: {
                holder = TitleSegmentHolder.create(fragment, parent);
                break;
            }
            case TYPE_SUBTITLE: {
                holder = TitleSegmentHolder.create(fragment, parent);
                break;
            }
            case TYPE_PARAGRAPH: {
                holder = ParagraphSegmentHolder.create(fragment, parent);
                break;
            }
        }

        if (holder != null) {
            holder.mType = itemType;
        }

        return holder;
    }

    public static final int getType(Document note, Segment entity) {
        int itemType = TYPE_PARAGRAPH;

        if (entity == note.getTitle()) {
            itemType = TYPE_TITLE;
        } else if (entity == note.getSubtitle()) {
            itemType = TYPE_SUBTITLE;
        } else {

            String type = entity.getType();

            if (type.equalsIgnoreCase(Entity.TYPE_PARAGRAPH)) {
                itemType = TYPE_PARAGRAPH;
            }
        }

        return itemType;
    }

}
