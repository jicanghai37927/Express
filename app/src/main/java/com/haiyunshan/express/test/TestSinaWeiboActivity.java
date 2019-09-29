package com.haiyunshan.express.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.haiyunshan.express.R;
import com.haiyunshan.express.share.SinaWeibo;
import com.haiyunshan.express.share.SinaWeiboSender;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.io.File;
import java.util.ArrayList;

public class TestSinaWeiboActivity extends AppCompatActivity implements WbShareCallback {

    /** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      = "4071796426";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "http://www.baidu.com";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = "";

    private WbShareHandler shareHandler;

    private SinaWeiboSender mSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sina_weibo);

        if (true) {
            this.mSender = SinaWeibo.instance().getSender(this);

            findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send();
                }
            });
        }

        if (false) {
            WbSdk.install(this, new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE));

            shareHandler = new WbShareHandler(this);
            shareHandler.registerApp();
            shareHandler.setProgressColor(0xff33b5e5);

            findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMultiMessage(true, true, false);
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (false) {
            shareHandler.doResultIntent(intent, this);
        }
    }

    private void send() {
        String title = "万象笔记";
        String msg = "如果你看到这条消息，依次是JPG、PNG、GIF，文殊师利菩萨保佑！";
        ArrayList<Uri> list = new ArrayList<>();
        list.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Express/IMG_0051.JPG")));
        list.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Express/rabbit.png")));
        list.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Express/rabbit.gif")));
//        list.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Express/a.jpg")));
//        list.add(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Express/a.jpg")));
        for (Uri uri : list) {
            Log.w("SinaWeibo", uri.toString());
        }

        mSender.send(title, msg, list);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasMultiImage) {


        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }

        if(hasMultiImage){
            weiboMessage.multiImageObject = getMultiImageObject();
        }

        shareHandler.shareMessage(weiboMessage, false);

    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        textObject.title = "万象笔记";
        textObject.actionUrl = "http://www.baidu.com";
        return textObject;
    }

    private String getSharedText() {

        String text = "如果你看到这条消息，说明新浪微博分享成功了 3 次，佛祖保佑！";

        return text;
    }

    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_code);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    private MultiImageObject getMultiImageObject(){
        MultiImageObject multiImageObject = new MultiImageObject();
        //pathList设置的是本地本件的路径,并且是当前应用可以访问的路径，现在不支持网络路径（多图分享依靠微博最新版本的支持，所以当分享到低版本的微博应用时，多图分享失效
        // 可以通过WbSdk.hasSupportMultiImage 方法判断是否支持多图分享,h5分享微博暂时不支持多图）多图分享接入程序必须有文件读写权限，否则会造成分享失败
        ArrayList<Uri> pathList = new ArrayList<Uri>();
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/aaa.png")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/bbbb.jpg")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/ccc.JPG")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/ddd.jpg")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/fff.jpg")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/ggg.JPG")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/eee.jpg")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/hhhh.jpg")));
        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/kkk.JPG")));
        multiImageObject.setImageList(pathList);
        return multiImageObject;
    }

    @Override
    public void onWbShareSuccess() {
        Toast.makeText(this, "成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWbShareCancel() {
        Toast.makeText(this, "取消", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWbShareFail() {
        Toast.makeText(this,
                "失败 " + "Error Message: ",
                Toast.LENGTH_LONG).show();
    }
}
