package com.example.huangxueqin.zhihudaily.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.huangxueqin.zhihudaily.common.SelectionBuilder;

/**
 * Created by huangxueqin on 16/8/17.
 */
public class StoryProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.huangxueqin.zhihudaily.provider.news";

    private StoryDatabase mOpenHelper;
    private StoryUriMatcher mMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new StoryDatabase(getContext());
        mMatcher = new StoryUriMatcher();
        return true;
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder selectionBuilder = new SelectionBuilder();
        StoryUriEnum uriEnum = mMatcher.matchCode(match);
        switch (uriEnum) {
            case READ_NEWSES: {
                return selectionBuilder.table(StoryDatabase.Tables.ReadNewses);
            }
            case READ_NEWSES_ID: {
                String newsId = uri.getPathSegments().get(1);
                return selectionBuilder.table(StoryDatabase.Tables.ReadNewses)
                        .where(StoryContact.ReadNewses.NEWS_ID + "=?", newsId);
            }
        }
        return selectionBuilder;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        StoryUriEnum matchedUriEnum = mMatcher.matchUri(uri);
        final SelectionBuilder builder = buildExpandedSelection(uri, matchedUriEnum.code);
        return builder.where(selection, selectionArgs)
                .query(db, true, projection, sortOrder, null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        StoryUriEnum matchUriEnum = mMatcher.matchUri(uri);
        return matchUriEnum.contentType;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        StoryUriEnum matchedUriEnum = mMatcher.matchUri(uri);
        if(matchedUriEnum.table != null) {
            db.insertOrThrow(matchedUriEnum.table, null, contentValues);
            return StoryContact.ReadNewses.buildReadNewsesUri(contentValues.getAsString(StoryContact.ReadNewses.NEWS_ID));
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

}
