package com.haiyunshan.express.note;

import android.text.TextUtils;

import com.haiyunshan.express.app.Utils;
import com.haiyunshan.express.dataset.DataAccessor;
import com.haiyunshan.express.dataset.note.Identifier;
import com.haiyunshan.express.dataset.note.Note;
import com.haiyunshan.express.dataset.note.entity.Entity;
import com.haiyunshan.express.dataset.note.entity.ParagraphEntity;
import com.haiyunshan.express.dataset.note.entity.PictureEntity;
import com.haiyunshan.express.dataset.note.entity.StopEntity;
import com.haiyunshan.express.dataset.note.page.Page;
import com.haiyunshan.express.dataset.note.style.Style;
import com.haiyunshan.express.note.segment.ParagraphSegment;
import com.haiyunshan.express.note.segment.PictureSegment;
import com.haiyunshan.express.note.segment.StopSegment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Document {

    public static final int saveMaskNote    = 0x01;
    public static final int saveMaskStyle   = 0x02;
    public static final int saveMaskPage    = 0X04;

    Identifier mId;

    Page mPage;
    Style mStyle;

    long mCreated;
    long mModified;

    ParagraphSegment mTitle;        // 标题
    ParagraphSegment mSubtitle;     // 副标题

    ArrayList<Segment> mBody;       // 正文

    Document(Identifier id, Note note) {
        this.mId = id;

        this.mCreated = note.getCreated();
        this.mCreated = (this.mCreated <= 0)? System.currentTimeMillis(): this.mCreated;

        this.mModified = note.getModified();
        this.mModified = (this.mModified <= 0)? System.currentTimeMillis(): this.mModified;

        this.mTitle = new ParagraphSegment(this, note.getTitle());
        this.mSubtitle = new ParagraphSegment(this, note.getSubtitle());

        {
            this.mBody = new ArrayList<>(note.getBody().size() + 1);

            List<Entity> body = note.getBody();
            for (Entity e : body) {
                Segment s = null;

                String type = e.getType();
                if (type.equalsIgnoreCase(Entity.TYPE_PARAGRAPH)) {
                    s = new ParagraphSegment(this, (ParagraphEntity)e);
                } else if (type.equalsIgnoreCase(Entity.TYPE_STOP)) {
                    s = new StopSegment(this, (StopEntity)e);
                } else if (type.equalsIgnoreCase(Entity.TYPE_PICTURE)) {
                    s = new PictureSegment(this, (PictureEntity)e);
                }

                if (s != null) {
                    this.mBody.add(s);
                }
            }
        }
    }

    public String getId() {
        return this.mId.getId();
    }

    public String getTemplateId() {
        return mId.getTemplateId();
    }

    public Page getPage() {
        return this.mPage;
    }

    public Style getStyle() {
        return this.mStyle;
    }

    public ParagraphSegment getTitle() {
        return this.mTitle;
    }

    public ParagraphSegment getSubtitle() {
        return this.mSubtitle;
    }

    public List<Segment> getBody() {
        return this.mBody;
    }

    public int indexOf(Segment s) {
        return mBody.indexOf(s);
    }

    public void add(Segment s, int index) {
        mBody.add(index, s);
    }

    public Segment obtain(String id) {
        if (mTitle.getId().equalsIgnoreCase(id)) {
            return mTitle;
        }

        if (mSubtitle.getId().equalsIgnoreCase(id)) {
            return mSubtitle;
        }

        for (Segment s : mBody) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }

        return null;
    }

    public Segment get(int index) {
        return mBody.get(index);
    }

    public void remove(Segment s) {
        mBody.remove(s);
    }

    public int size() {
        return mBody.size();
    }

    public int getCount() {
        Document document = this;

        int count = 0;
        List<Segment> list = document.getBody();
        for (Segment segment : list) {
            if (segment.getType() != Segment.TYPE_PARAGRAPH) {
                continue;
            }

            ParagraphSegment seg = (ParagraphSegment)(segment);
            CharSequence cs = seg.getText();
            if (TextUtils.isEmpty(cs)) {
                continue;
            }

            int length = cs.length();
            for (int i = 0; i < length; i++) {
                char c = cs.charAt(i);

                if (Utils.isChinese(c)) {
                    ++count;
                }
            }
        }

        return count;
    }

    public void save() {
        this.save(saveMaskNote);
    }

    public void save(int mask) {
        if ((mask & saveMaskNote) != 0) {
            DataAccessor.instance().saveNoteFile(this.getId(), this.toNote());
        }

        if ((mask & saveMaskPage) != 0) {
            DataAccessor.instance().saveNotePage(this.getId(), this.mPage);
        }

        if ((mask & saveMaskStyle) != 0) {
            DataAccessor.instance().saveNoteStyle(this.getId(), this.mStyle);
        }
    }

    public boolean hasStop() {
        for (Segment segment : mBody) {
            if (segment.getType() == Segment.TYPE_STOP) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        for (Segment s : mBody) {
            if (!s.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void clearStyle(String id) {
        if (mTitle.getStyle().getId().equalsIgnoreCase(id)) {
            mTitle.getStyle().clear();
        }
        if (mSubtitle.getStyle().getId().equalsIgnoreCase(id)) {
            mSubtitle.getStyle().clear();
        }
        for (Segment s : mBody) {
            if (s instanceof ParagraphSegment) {
                ((ParagraphSegment)s).getStyle().clear();
            }
        }
    }

    public Note toNote() {
        Note note = new Note();

        Document doc = this;

        note.setCreated(doc.mCreated);
        note.setModified(doc.mModified);

        note.setTitle(doc.mTitle.toEntity());
        note.setSubtitle(doc.mSubtitle.toEntity());

        ArrayList<Entity> list = new ArrayList<>(doc.mBody.size());
        for (Segment s : doc.mBody) {
            list.add(s.toEntity());
        }
        note.setBody(list);

        return note;
    }

    public Segment peekParagraph() {
        for (Segment s : mBody) {
            if (s.getType() == Segment.TYPE_PARAGRAPH) {
                return s;
            }
        }

        return null;
    }

    public Segment lastParagraph() {
        int size = mBody.size();
        for (int i = size - 1; i >= 0; i--) {
            Segment s = mBody.get(i);
            if (s.getType() == Segment.TYPE_PARAGRAPH) {
                return s;
            }
        }

        return null;
    }

    public Note toTemplate() {
        Note note = new Note();

        note.setCreated(System.currentTimeMillis());
        note.setModified(System.currentTimeMillis());

        {
            ParagraphEntity entity = mTitle.toEntity();
            entity.setText("");
            entity.clearSpans();

            note.setTitle(entity);
        }

        {
            ParagraphEntity entity = mSubtitle.toEntity();
            entity.setText("");
            entity.clearSpans();

            note.setSubtitle(entity);
        }

        {
            ArrayList<Entity> list = new ArrayList<>(this.mBody.size());
            for (Segment s : mBody) {
                if (s instanceof ParagraphSegment) {
                    ParagraphEntity entity = (ParagraphEntity)s.toEntity();
                    entity.setText("");
                    entity.clearSpans();

                    list.add(entity);

                    break;
                }
            }

            note.setBody(list);

        }
        return note;
    }
}
