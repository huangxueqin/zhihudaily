package com.example.huangxueqin.zhihudaily.provider;

/**
 * Created by huangxueqin on 16/8/21.
 */
public enum NewsUriEnum {
    READ_NEWSES(100, NewsContact.PATH_READ_NEWSES, NewsContact.ReadNewses.NEWS_ID, false, NewsDatabase.Tables.ReadNewses),
    READ_NEWSES_ID(101, NewsContact.PATH_READ_NEWSES + "/#", NewsContact.ReadNewses.NEWS_ID, false, NewsDatabase.Tables.ReadNewses);

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
