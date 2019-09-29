package com.haiyunshan.express.compose;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.haiyunshan.express.R;
import com.haiyunshan.express.compose.holder.MarginSegmentHolder;
import com.haiyunshan.express.compose.holder.ParagraphSegmentHolder;
import com.haiyunshan.express.compose.holder.PictureSegmentHolder;
import com.haiyunshan.express.compose.holder.SegmentHolder;
import com.haiyunshan.express.compose.holder.StopSegmentHolder;
import com.haiyunshan.express.compose.holder.TitleSegmentHolder;
import com.haiyunshan.express.note.Document;
import com.haiyunshan.express.note.Segment;
import com.haiyunshan.express.note.segment.MarginSegment;
import com.haiyunshan.express.note.segment.StopSegment;

import java.util.ArrayList;

import static com.haiyunshan.express.compose.holder.SegmentHolder.TYPE_MARGIN;
import static com.haiyunshan.express.compose.holder.SegmentHolder.TYPE_PARAGRAPH;
import static com.haiyunshan.express.compose.holder.SegmentHolder.TYPE_PICTURE;
import static com.haiyunshan.express.compose.holder.SegmentHolder.TYPE_STOP;
import static com.haiyunshan.express.compose.holder.SegmentHolder.TYPE_SUBTITLE;
import static com.haiyunshan.express.compose.holder.SegmentHolder.TYPE_TITLE;

public class DocumentAdapter extends RecyclerView.Adapter<SegmentHolder> {

    ArrayList<Segment> mList;

    Document mDoc;
    NoteComposeFragment mFragment;

    public DocumentAdapter(NoteComposeFragment fragment, Document doc) {
        this.mFragment = fragment;
        this.mDoc = doc;

        this.mList = new ArrayList<>();
        mList.add(doc.getTitle());
        mList.add(doc.getSubtitle());
        mList.addAll(doc.getBody());

        // margin
        Resources res = fragment.getActivity().getResources();
        {
            int height = res.getDimensionPixelSize(R.dimen.doc_margin_top);
            MarginSegment seg = new MarginSegment(doc, height);
            mList.add(0, seg);
        }
        {
            int height = res.getDimensionPixelSize(R.dimen.doc_margin_bottom);
            MarginSegment seg = new MarginSegment(doc, height);
            mList.add(seg);
        }
    }

    public int remove(String id, boolean notify) {
        int index = indexOf(id);
        if (index >= 0) {
            mList.remove(index);
        }

        if (notify) {
            this.notifyItemRemoved(index);
        }

        return index;
    }

    public int add(Segment segment, int index) {
        mList.add(index, segment);
        return mList.indexOf(segment);
    }

    public final boolean notifyItemChanged(String id) {
        int pos = this.indexOf(id);
        if (pos >= 0) {
            this.notifyItemChanged(pos);
        }

        return (pos >= 0);
    }

    public int indexOf(String id) {
        int size = mList.size();

        for (int i = 0; i < size; i++) {
            Segment s = mList.get(i);
            if (s.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }

        return -1;
    }

    public int indexStop(StopSegment segment) {
        int index = 0;
        for (Segment seg : mList) {
            if (seg.getType() == Segment.TYPE_STOP) {
                if (segment == seg) {
                    break;
                }

                ++index;
            }
        }

        return index;
    }

    @Override
    public int getItemViewType(int position) {
        Segment s = mList.get(position);
        int type = getType(this.mDoc, s);

        return type;
    }

    @Override
    public SegmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SegmentHolder holder = create(mFragment, parent, viewType);

        return holder;
    }

    @Override
    public void onBindViewHolder(SegmentHolder holder, int position) {
        Segment s = mList.get(position);

        holder.bind(position, s);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onViewAttachedToWindow(SegmentHolder holder) {
        DocumentState state = mFragment.peekState();
        state.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(SegmentHolder holder) {
        DocumentState state = mFragment.peekState();
        state.onViewDetachedFromWindow(holder);
    }

    static final SegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int viewType) {
        SegmentHolder holder = null;

        int itemType = viewType;
        switch (itemType) {
            case TYPE_TITLE: {
                holder = TitleSegmentHolder.create(fragment, parent, itemType);
                break;
            }
            case TYPE_SUBTITLE: {
                holder = TitleSegmentHolder.create(fragment, parent, itemType);
                break;
            }
            case TYPE_PARAGRAPH: {
                holder = ParagraphSegmentHolder.create(fragment, parent, itemType);
                break;
            }
            case TYPE_MARGIN: {
                holder = MarginSegmentHolder.create(fragment, parent, itemType);
                break;
            }
            case TYPE_STOP: {
                holder = StopSegmentHolder.create(fragment, parent, itemType);
                break;
            }
            case TYPE_PICTURE: {
                holder = PictureSegmentHolder.create(fragment, parent, itemType);
            }
        }

        return holder;
    }

    static final int getType(Document doc, Segment seg) {
        int itemType = -1;

        if (seg == doc.getTitle()) {
            itemType = TYPE_TITLE;
        } else if (seg == doc.getSubtitle()) {
            itemType = TYPE_SUBTITLE;
        } else {

            int type = seg.getType();

            if (type == Segment.TYPE_PARAGRAPH) {
                itemType = TYPE_PARAGRAPH;
            } else if (type == Segment.TYPE_MARGIN) {
                itemType = TYPE_MARGIN;
            } else if (type == Segment.TYPE_STOP) {
                itemType = TYPE_STOP;
            } else if (type == Segment.TYPE_PICTURE) {
                itemType = TYPE_PICTURE;
            }
        }

        if (itemType < 0) {
            throw new IllegalArgumentException("ItemHolder not found");
        }

        return itemType;
    }

}
