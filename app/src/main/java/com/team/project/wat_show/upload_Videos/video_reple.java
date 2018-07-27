package com.team.project.wat_show.upload_Videos;

import java.io.Serializable;

public class video_reple implements Serializable {

    public String video_reple_profile;
    public String video_reple_id;
    public String video_reple_time;
    public String video_reple_count;
    public String video_reple_upcount;
    public String video_reple_downcount;
    public String video_reple_contents;
    public String video_reple_check;
    public String video_reple_repleNo;

    public video_reple(String video_reple_profile, String video_reple_id, String video_reple_time, String video_reple_contents,String video_reple_count, String video_reple_upcount, String video_reple_downcount,String video_reple_check,String video_reple_repleNo) {
        this.video_reple_profile = video_reple_profile;
        this.video_reple_id = video_reple_id;
        this.video_reple_time = video_reple_time;
        this.video_reple_contents = video_reple_contents;
        this.video_reple_count = video_reple_count;
        this.video_reple_upcount = video_reple_upcount;
        this.video_reple_downcount = video_reple_downcount;
        this.video_reple_check = video_reple_check;
        this.video_reple_repleNo = video_reple_repleNo;
    }
}
