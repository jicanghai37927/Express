package com.haiyunshan.express.dataset;

import com.haiyunshan.express.App;
import com.haiyunshan.express.dataset.catalog.CatalogManager;
import com.haiyunshan.express.dataset.note.NoteManager;
import com.haiyunshan.express.dataset.template.TemplateManager;
import com.haiyunshan.express.music.MusicManager;
import com.haiyunshan.express.note.DocumentManager;
import com.haiyunshan.express.picture.PictureManager;
import com.haiyunshan.express.typeface.TypefaceManager;

/**
 * Created by sanshibro on 2017/11/11.
 */

public class DataManager {

    DocumentManager mDocumentMgr;   //
    TemplateManager mTemplateMgr;   //

    NoteManager mNoteMgr;           // 笔记管理
    CatalogManager mCatalogMgr;     // 目录管理
    PictureManager mPictureMgr;     // 图片管理
    MusicManager mMusicMgr;         // 音乐管理
    TypefaceManager mTypefaceMgr;   //

    DataAccessor mAccessor;         // 数据存取器

    public static DataManager instance() {
        return App.instance().getDataManager();
    }

    public DataManager() {
        this.mAccessor = new DataAccessor();
    }

    public DocumentManager getDocumentManager() {
        if (mDocumentMgr == null) {
            mDocumentMgr = new DocumentManager();
        }

        return mDocumentMgr;
    }

    public TemplateManager getTemplateManager() {
        if (mTemplateMgr == null) {
            mTemplateMgr = new TemplateManager();
        }

        return mTemplateMgr;
    }

    public NoteManager getNoteManager() {
        if (mNoteMgr == null) {
            mNoteMgr = new NoteManager();
        }

        return mNoteMgr;
    }

    public CatalogManager getCatalogManager() {
        if (mCatalogMgr == null) {
            mCatalogMgr = new CatalogManager();
        }

        return mCatalogMgr;
    }

    public PictureManager getPictureManager() {
        if (mPictureMgr == null) {
            mPictureMgr = new PictureManager();
        }

        return mPictureMgr;
    }

    public MusicManager getMusicManager() {
        if (mMusicMgr == null) {
            mMusicMgr = new MusicManager();
        }

        return mMusicMgr;
    }

    public TypefaceManager getTypefaceManager() {
        if (mTypefaceMgr == null) {
            mTypefaceMgr = new TypefaceManager();
        }

        return mTypefaceMgr;
    }

    public DataAccessor accessor() {
        return mAccessor;
    }
}
