package com.example.huangxueqin.zhihudaily.db;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * Created by huangxueqin on 16/8/21.
 */
public class NewsProviderUriMatcher {
    private UriMatcher mUriMatcher;
    private SparseArray<NewsUriEnum> mEnumsMap = new SparseArray<>();

    public NewsProviderUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        String authority = NewsProvider.AUTHORITY;
        NewsUriEnum[] uris = NewsUriEnum.values();
        for(NewsUriEnum item : uris) {
            mUriMatcher.addURI(authority, item.path, item.code);
        }
        buildEnumsMap();
    }

    private void buildEnumsMap() {
        NewsUriEnum[] uris = NewsUriEnum.values();
        for(NewsUriEnum uri : uris) {
            mEnumsMap.put(uri.code, uri);
        }
    }

    public NewsUriEnum matchUri(Uri uri) {
        int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("unknown uri: " + uri);
        }
    }

    public NewsUriEnum matchCode(int code) {
        NewsUriEnum item = mEnumsMap.get(code);
        if(item != null) {
            return item;
        }
        else {
            throw new UnsupportedOperationException("unknown uri with code: " + code);
        }
    }
}
