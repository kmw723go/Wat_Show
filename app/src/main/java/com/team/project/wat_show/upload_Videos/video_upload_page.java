package com.team.project.wat_show.upload_Videos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.team.project.wat_show.R;

public class video_upload_page extends AppCompatActivity {

    String loginUserId;
    String loginUserNick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_page);

        getUserData();
    }

    // 사용자 아이디 받아오기
    public void getUserData(){


        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");
    }
}
