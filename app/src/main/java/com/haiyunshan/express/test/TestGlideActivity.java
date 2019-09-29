package com.haiyunshan.express.test;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.haiyunshan.express.R;
import com.haiyunshan.express.app.LogUtils;
import com.haiyunshan.express.glide.GlideApp;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SwirlFilterTransformation;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

public class TestGlideActivity extends AppCompatActivity {

    static final String TAG = "Glide";

    static final int REQUEST_ALBUM = 0x1003;
    public static final int PHOTO_REQUEST_CAREMA = 0x1004;// 拍照

    RecyclerView mRecyclerView;
    GlideAdapter mAdapter;
    List<PictureEntry> mList;

    Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_glide);

//        this.testSimple();
        this.testRecyclerView();
    }

    void testSimple() {
        findViewById(R.id.layout_simple).setVisibility(View.VISIBLE);

        ImageView target = findViewById(R.id.iv_test);

        String url = "https://www.baidu.com/img/bd_logo1.png";
        url = "https://gss3.bdstatic.com/7Po3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike220%2C5%2C5%2C220%2C73/sign=08480c2f1530e924dba994632d610563/21a4462309f790529762561906f3d7ca7bcbd593.jpg";
        url = "https://gss2.bdstatic.com/-fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike272%2C5%2C5%2C272%2C90/sign=bdb887489413b07ea9b0585a6dbefa46/7aec54e736d12f2eff72ae0948c2d56285356867.jpg";

        Glide.with(this).load(url).into(target);
    }

    void testRecyclerView() {
        findViewById(R.id.layout_rv).setVisibility(View.VISIBLE);


        this.read();

        this.mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.mAdapter = new GlideAdapter();
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPictureFromAlbum((Activity)v.getContext());
            }
        });
        findViewById(R.id.btn_take_photo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCamera(TestGlideActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        LogUtils.w(TAG, "onActivityResult");

        if (requestCode == REQUEST_ALBUM) {
            if (resultCode == RESULT_OK && data != null) {
//                LogUtils.w("Glide", data);

                String icon_path;
                if(data.getDataString().contains("content")){
                    icon_path = getRealPathFromURI(data.getData());
                }else {
                    icon_path = data.getDataString().replace("file://", "");
                }

//                LogUtils.w("Glide", icon_path);

                mList.add(new PictureEntry(icon_path));
                mAdapter.notifyItemInserted(mList.size());

                save();

            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (resultCode == RESULT_OK) {
                String icon_path;
                if(mImageUri.toString().contains("content")){
                    icon_path = getRealPathFromURI(mImageUri);
                }else {
                    icon_path = mImageUri.toString().replace("file://", "");
                }

//                LogUtils.w("Glide", icon_path);

                mList.add(new PictureEntry(icon_path));
                mAdapter.notifyItemInserted(mList.size());

                save();
            }


        }
    }

    public String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
    }

    public static void selectPictureFromAlbum(Activity activity){
        // 调用系统的相冊
        Intent intent = new Intent(Intent.ACTION_PICK, null);
//        intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");

        // 调用剪切功能
        activity.startActivityForResult(intent, REQUEST_ALBUM);
    }

    void read() {
        List<String> list = null;
        File file = new File(Environment.getExternalStorageDirectory(), "test_glide.txt");
        try {
            list = FileUtils.readLines(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mList = new ArrayList<>();

        if (list != null) {
            for (String str : list) {
                this.mList.add(new PictureEntry(str));
            }
        }
    }

    void save() {
        File file = new File(Environment.getExternalStorageDirectory(), "test_glide.txt");
        if (mList == null) {
            mList = new ArrayList<>();
        }

        List<String> list = new ArrayList<>();
        for (PictureEntry e : mList) {
            list.add(e.mPath);
        }

        try {
            FileUtils.writeLines(file, "utf-8", list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GlideAdapter extends RecyclerView.Adapter<ItemHolder> {

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.layout_test_glide_item, parent, false);
            ItemHolder holder = new ItemHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            final PictureEntry e = mList.get(position);

            ImageView view = holder.mPicView;
            int width = (int)(0.9f * 1080);
            width = width / 2 * 2;
            if (width > e.mWidth) {
                width = e.mWidth;
            }

            width = (int)(1.0f * width);
            int height = width * e.mHeight / e.mWidth;

            view.getLayoutParams().width = width;
            view.getLayoutParams().height = height;

            RequestOptions options = new RequestOptions();
            options.placeholder(new ColorDrawable(Color.GRAY));
            options.transform(new PixelationFilterTransformation(100));

//            options.override(width, height);
//            options.fitCenter();

//            Log.w("Glide", "onBindViewHolder = " + width + ", " + height);

            Glide.with(holder.itemView).load(e.mPath).apply(options).listener(new RequestListener<Drawable>() {

                PictureEntry mEntry = e;
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


//                        Bitmap bmp = resource;
//
//                        Log.w("Glide", "onResourceReady = " + bmp.getConfig().name() + ", " + e.mWidth + ", " + bmp.getWidth());

                    return false;
                }
            }).into(new DrawableImageViewTarget(holder.mPicView) {

                @Override
                protected void setResource(@Nullable Drawable resource) {
//                    if (resource instanceof BitmapDrawable) {
//                        Bitmap bmp = ((BitmapDrawable)resource).getBitmap();
//                        if (bmp.getWidth() < 1080 && bmp.getHeight() < 1080) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(TestGlideActivity.this.getResources(), bmp);
//                            circularBitmapDrawable.setCornerRadius(50); //设置圆角弧度
//
//                            resource = circularBitmapDrawable;
//                        }
//
//                    }

                    super.setResource(resource);
                }
            });
            holder.mNameView.setText(e.toString());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {

        ImageView mPicView;
        TextView mNameView;

        public ItemHolder(View itemView) {
            super(itemView);

            this.mPicView = itemView.findViewById(R.id.iv_pic);
            this.mNameView = itemView.findViewById(R.id.tv_name);
        }
    }

    private class PictureEntry {
        public String mPath;
        public int mWidth;
        public int mHeight;

        PictureEntry(String path) {
            this.mPath = path;
            int[] array = getImageWidthHeight(path);
            mWidth = array[0];
            mHeight = array[1];
        }

        @Override
        public String toString() {
            return mPath + ", " + mWidth + ", " + mHeight;
        }
    }

    public static int[] getImageWidthHeight(String path){
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

    public void openCamera(Activity activity) {
        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            File tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                Uri imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                this.mImageUri = imageUri;
            } else {

                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(this,"请开启存储权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                this.mImageUri = imageUri;

            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
* 判断sdcard是否被挂载
*/
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
