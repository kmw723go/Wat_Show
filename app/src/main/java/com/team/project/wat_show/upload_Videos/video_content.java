package com.team.project.wat_show.upload_Videos;

import java.io.Serializable;

public class video_content implements Serializable {

    public String dataNo;
    public String makeUserId;
    public String makeUserNick;
    public String makeUserProfile;
    public String content_title;
    public String content_explain;
    public String content_keyWord;
    public String content_vPath;
    public String content_thPath;
    public String content_time;

    public int hits;
    public int recommend;
    public int unrecommend;
    public int sCount;


    public video_content(String dataNo, String makeUserId, String makeUserNick, String makeUserProfile, String content_title, String content_explain, String content_keyWord,
                         String content_vPath, String content_thPath, String content_time, int hits, int recommend, int unrecommend, int sCount) {
        this.dataNo = dataNo;
        this.makeUserId = makeUserId;
        this.makeUserNick = makeUserNick;
        this.makeUserProfile = makeUserProfile;
        this.content_title = content_title;
        this.content_explain = content_explain;
        this.content_keyWord = content_keyWord;
        this.content_vPath = content_vPath;
        this.content_thPath = content_thPath;
        this.content_time = content_time;
        this.hits = hits;
        this.recommend = recommend;
        this.unrecommend = unrecommend;
        this.sCount = sCount;
    }
}
