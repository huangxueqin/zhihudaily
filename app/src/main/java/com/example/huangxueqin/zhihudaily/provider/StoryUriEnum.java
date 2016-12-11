package com.example.huangxueqin.zhihudaily.provider;

/**
 * Created by huangxueqin on 16/8/21.
 */
public enum StoryUriEnum {
    READ_NEWSES(100, StoryContact.PATH_READ_NEWSES, StoryContact.ReadNewses.NEWS_ID, false, StoryDatabase.Tables.ReadNewses),
    READ_NEWSES_ID(101, StoryContact.PATH_READ_NEWSES + "/#", StoryContact.ReadNewses.NEWS_ID, false, StoryDatabase.Tables.ReadNewses);

    int code;
    String path;
    String table;
    String contentType;

    StoryUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.table = table;
        if(item) {
            this.contentType = StoryContact.makeContentItemType(contentTypeId);
        }
        else {
            this.contentType = StoryContact.makeContentType(contentTypeId);
        }
    }
}
