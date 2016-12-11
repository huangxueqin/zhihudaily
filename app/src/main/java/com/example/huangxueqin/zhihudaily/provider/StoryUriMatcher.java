package com.example.huangxueqin.zhihudaily.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * Created by huangxueqin on 16/8/21.
 */
public class StoryUriMatcher {
    private UriMatcher mUriMatcher;
    private SparseArray<StoryUriEnum> mEnumsMap = new SparseArray<>();

    public StoryUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        String authority = StoryProvider.AUTHORITY;
        StoryUriEnum[] uris = StoryUriEnum.values();
        for(StoryUriEnum item : uris) {
            mUriMatcher.addURI(authority, item.path, item.code);
        }
        buildEnumsMap();
    }

    private void buildEnumsMap() {
        StoryUriEnum[] uris = StoryUriEnum.values();
        for(StoryUriEnum uri : uris) {
            mEnumsMap.put(uri.code, uri);
        }
    }

    public StoryUriEnum matchUri(Uri uri) {
        int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("unknown uri: " + uri);
        }
    }

    public StoryUriEnum matchCode(int code) {
        StoryUriEnum item = mEnumsMap.get(code);
        if(item != null) {
            return item;
        }
        else {
            throw new UnsupportedOperationException("unknown uri with code: " + code);
        }
    }
}
