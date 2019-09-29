package com.haiyunshan.express.compose.holder;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.WindowUtils;
import com.haiyunshan.express.compose.NoteComposeFragment;
import com.haiyunshan.express.note.segment.PictureSegment;
import com.haiyunshan.express.note.segment.StopSegment;
import com.haiyunshan.express.test.TestGlideActivity;

import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;

/**
 * Created by sanshibro on 2018/2/7.
 */

public class PictureSegmentHolder extends SegmentHolder<PictureSegment> implements View.OnLongClickListener {

    ImageView mPictureView;
    EditText mNameView;

    public static PictureSegmentHolder create(NoteComposeFragment fragment, ViewGroup parent, int type) {
        LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

        int resource = R.layout.layout_note_picture_item;
        View view = inflater.inflate(resource, parent, false);
        PictureSegmentHolder holder = new PictureSegmentHolder(fragment, view, type);

        return holder;
    }

    public PictureSegmentHolder(NoteComposeFragment fragment, View itemView, int type) {
        super(fragment, itemView, type);

        this.mNameView = (itemView.findViewById(R.id.edit_name));
        this.mPictureView = itemView.findViewById(R.id.iv_picture);

        itemView.setOnLongClickListener(this);
    }

    @Override
    protected void onBind(int position, PictureSegment segment) {
        super.onBind(position, segment);

        String name = mSegment.getName();
        name = (TextUtils.isEmpty(name))? mSegment.getId(): name;
        mNameView.setText(name);

        int[] size = getViewSize();
        mPictureView.getLayoutParams().width = size[0];
        mPictureView.getLayoutParams().height = size[1];

        RequestOptions options = new RequestOptions();
        options.placeholder(new ColorDrawable(Color.GRAY));
//        options.transform(new PixelationFilterTransformation(100));

        Glide.with(mPictureView).load(mSegment.getUri()).apply(options).listener(new RequestListener<Drawable>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                return false;
            }
        }).into(new DrawableImageViewTarget(mPictureView) {

            @Override
            protected void setResource(@Nullable Drawable resource) {
//                    if (resource instanceof BitmapDrawable) {
//                        Bitmap bmp = ((BitmapDrawable)resource).getBitmap();
//                        if (bmp.getWidth() < 1080 && bmp.getHeight() < 1080) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), bmp);
//                            circularBitmapDrawable.setCornerRadius(50); //设置圆角弧度
//
//                            resource = circularBitmapDrawable;
//                        }
//
//                    }

                super.setResource(resource);
            }
        });

        mNameView.setText(mSegment.getName());

    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }

    int[] getViewSize() {
        int width = mRecyclerView.getMeasuredWidth();
        width = (int)(0.9f * width);

        width = (mSegment.getWidth() < width)? mSegment.getWidth(): width;
        int height = width * mSegment.getHeight() / mSegment.getWidth();

        return new int[] { width, height};
    }
}
