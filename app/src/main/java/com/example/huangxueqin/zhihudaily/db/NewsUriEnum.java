package com.example.huangxueqin.zhihudaily.db;

/**
 * Created by huangxueqin on 16/8/21.
 */
public enum NewsUriEnum {
    READ_NEWSES(100, "newses", NewsContact.ReadNewsesColumns.NEWS_ID, false, NewsDatabase.Tables.ReadNewses),
    READ_NEWSES_ID(101, "newses/#", NewsContact.ReadNewsesColumns.NEWS_ID, false, NewsDatabase.Tables.ReadNewses);

    int code;
    String path;
    String table;
    String contentType;

    NewsUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.table = table;
        if(item) {
            this.contentType = NewsContact.makeContentItemType(contentTypeId);
        }
        else {
            this.contentType = NewsContact.makeContentType(contentTypeId);
        }
    }
}
