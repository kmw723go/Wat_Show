package com.team.project.wat_show.upload_Videos;

import java.io.Serializable;

public class video_content implements Serializable {

    public String dataNo;
    public String makeUserId;
    public String content_title;
    public String content_explain;
    public String content_keyWord;
    public String content_vPath;
    public String content_thPath;
    public String content_time;

    public video_content(String dataNo, String makeUserId, String content_title, String content_explain, String content_keyWord, String content_vPath, String content_thPath, String content_time) {
        this.dataNo = dataNo;
        this.makeUserId = makeUserId;
        this.content_title = content_title;
        this.content_explain = content_explain;
        this.content_keyWord = content_keyWord;
        this.content_vPath = content_vPath;
        this.content_thPath = content_thPath;
        this.content_time = content_time;
    }
}
