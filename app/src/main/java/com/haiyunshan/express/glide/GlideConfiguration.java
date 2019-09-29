package com.haiyunshan.express.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.haiyunshan.express.app.LogUtils;

/**
 * Created by sanshibro on 2018/3/24.
 */

@GlideModule
public class GlideConfiguration extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return true;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        RequestOptions options = new RequestOptions();
        options.format(DecodeFormat.PREFER_ARGB_8888);

        builder.setDefaultRequestOptions(options);

        LogUtils.w("Glide", "applyOptions");
    }
}
