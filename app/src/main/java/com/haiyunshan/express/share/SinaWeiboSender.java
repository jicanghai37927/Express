package com.haiyunshan.express.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.util.ArrayList;

/**
 *
 */
public class SinaWeiboSender implements WbShareCallback {

    private WbShareHandler shareHandler;

    SinaWeiboSender(Activity context) {
        shareHandler = new WbShareHandler(context);
        shareHandler.registerApp();
        shareHandler.setProgressColor(0xff33b5e5);
    }

    public void onNewIntent(Intent intent) {
        shareHandler.doResultIntent(intent,this);
    }

    public void send(String title, String msg, ArrayList<Uri> pathList) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        {
            TextObject textObject = new TextObject();
            textObject.title = title;
            textObject.text = msg;
            textObject.actionUrl = "http://www.baidu.com";

            weiboMessage.textObject = textObject;
        }

        {
            MultiImageObject multiImageObject = new MultiImageObject();
            multiImageObject.setImageList(pathList);

            weiboMessage.multiImageObject = multiImageObject;
        }

        shareHandler.shareMessage(weiboMessage, false);
    }

    @Override
    public void onWbShareSuccess() {

    }

    @Override
    public void onWbShareCancel() {

    }

    @Override
    public void onWbShareFail() {

    }
}
