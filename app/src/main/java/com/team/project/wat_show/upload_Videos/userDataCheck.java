package com.team.project.wat_show.upload_Videos;

public class userDataCheck  {

    public String loginUserId;
    public String loginUserNick;
    public String loginUserProfile;
    public String reco;   // 0 미선택  1 추천 2 비추천
    public String mylistVcontent; // 0 미선택 1 즐겨찾기


    public userDataCheck(String loginUserId, String loginUserNick, String loginUserProfile, String reco, String mylistVcontent) {
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
        this.loginUserProfile = loginUserProfile;
        this.reco = reco;
        this.mylistVcontent = mylistVcontent;
    }
}
