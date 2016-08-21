package com.example.huangxueqin.zhihudaily.common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangxueqin on 16/8/21.
 */
public class SelectionBuilder {
    private String mTable;
    private Map<String, String> mProjectionMap = new HashMap<>();
    private StringBuilder mSelection = new StringBuilder();
    private List<String> mSelectionArgs = new ArrayList<>();
    private String mGroupBy;
    private String mHaving;

    public SelectionBuilder reset() {
        mTable = null;
        mGroupBy = null;
        mHaving = null;
        mSelection.setLength(0);
        mSelectionArgs.clear();
        return this;
    }

    public SelectionBuilder where(String selection, String... selectionArgs) {
        if(TextUtils.isEmpty(selection)) {
            if(selectionArgs != null && selectionArgs.length > 0) {
                throw new IllegalArgumentException("selection can not be null");
            }
            return this;
        }

        if(mSelection != null) {
            mSelection.append(" AND ");
        }

        mSelection.append("(").append(selection).append(")");
        if(selectionArgs != null) {
            Collections.addAll(mSelectionArgs, selectionArgs);
        }
        return this;
    }

    public SelectionBuilder groupBy(String groupBy) {
        mGroupBy = groupBy;
        return this;
    }

    public SelectionBuilder having(String having) {
        mHaving = having;
        return this;
    }

    public SelectionBuilder table(String table) {
        mTable = table;
        return this;
    }

    private void assertTable() {
        if(mTable == null) {
            throw new IllegalStateException("table must be specified");
        }
    }

    public SelectionBuilder mapToTable(String column, String table) {
        mProjectionMap.put(column, table + "." + column);
        return this;
    }

    public SelectionBuilder map(String fromColumn, String toClause) {
        mProjectionMap.put(fromColumn, toClause + " AS " + fromColumn);
        return this;
    }

    public String getSelection() {
        return mSelection.toString();
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
    }

    private void mapColumns(String[] columns) {
        for(int i = 0; i < columns.length; i++) {
            final String target = mProjectionMap.get(columns[i]);
            if(target != null) {
                columns[i] = target;
            }
        }
    }


    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return query(db, false, columns, orderBy, null);
    }

    /**
     *
     * @param db
     * @param distinct 对重复的记录,只获取其中一条
     * @param columns
     * @param orderBy
     * @param limit "LIMIT numRecord OFFSET baseRow"代表从baseRow行开始,取numRecord行记录
     * @return
     */
    public Cursor query(SQLiteDatabase db, boolean distinct, String[] columns, String orderBy, String limit) {
        assertTable();
        if(columns != null) {
            mapColumns(columns);
        }
        return db.query(distinct, mTable, columns, getSelection(), getSelectionArgs(), mGroupBy, mHaving, orderBy, limit);
    }

    public int update(SQLiteDatabase db, ContentValues values) {
        assertTable();
        return db.update(mTable, values, getSelection(), getSelectionArgs());
    }

    public int delete(SQLiteDatabase db) {
        assertTable();
        return db.delete(mTable, getSelection(), getSelectionArgs());
    }
}
