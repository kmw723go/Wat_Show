package com.team.project.wat_show.upload_Videos;

public class userDataCheck  {

    public String loginUserId;
    public String loginUserNick;
    public String reco;   // 1이면 추천 2면 해제
    public String unreco;  // 1이면  비추천 2면 해제
    public String mylistVcontent; // 1이면 추천  2면 해제

    public userDataCheck(String loginUserId, String loginUserNick, String reco, String unreco, String mylistVcontent) {
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
        this.reco = reco;
        this.unreco = unreco;
        this.mylistVcontent = mylistVcontent;
    }
}
