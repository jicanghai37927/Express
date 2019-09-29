package com.haiyunshan.express.share;

import android.app.Activity;
import android.content.Context;

import com.haiyunshan.express.App;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareHandler;

/**
 * Created by sanshibro on 2018/3/3.
 */

public class SinaWeibo {

    public static final String APP_KEY      = "4071796426";
    public static final String REDIRECT_URL = "http://www.baidu.com";
    public static final String SCOPE        = "";

    private static SinaWeibo sInstance;

    public static final SinaWeibo instance() {
        if (sInstance == null) {
            sInstance = new SinaWeibo();
        }

        return sInstance;
    }

    private SinaWeibo() {

        Context context = App.instance();

        WbSdk.install(context,new AuthInfo(context, APP_KEY, REDIRECT_URL, SCOPE));
    }


    public SinaWeiboSender getSender(Activity context) {
        SinaWeiboSender sender = new SinaWeiboSender(context);
        return sender;
    }




}
