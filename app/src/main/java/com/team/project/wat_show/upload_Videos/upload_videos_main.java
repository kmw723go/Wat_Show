package com.team.project.wat_show.upload_Videos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.team.project.wat_show.R;

public class upload_videos_main extends AppCompatActivity {

    Integer up_Load_Code = 1111;

    String loginUserId;
    String loginUserNick;


    RecyclerView my_upload_videoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_upload_videos_main);


        // 사용자 아이디 받아오기
        getUserData();


        // 리사이클러 뷰 세팅
        try {
            setVideosListSet();
        }catch (Exception e){

        }

        // 동영상 업로드 버튼
        gotoUpload();

    }

    // ----------------------------  데이터 받아오기 ------------------------------------------

    // 사용자 아이디 받아오기
    public void getUserData(){
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");
    }


    // ( 서버 전송 ) 데이터 가지고 오기
    // 만약에 데이터가 없는 경우 live_list_noContent 를 보여주고  my_upload_videoList를 Gone
    public void getMyUpload_data(){

    }


    // 리사이클러뷰 세팅
    public void setVideosListSet(){
        my_upload_videoList =(RecyclerView)findViewById(R.id.my_upload_videoList);

        // 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        my_upload_videoList.setLayoutManager(layoutManager);
    }

    // -------------------------------- 동영상 업로드 --------------------------------------------
    public void gotoUpload(){
        ImageView goto_upload_Btn =(ImageView)findViewById(R.id.goto_upload_Btn);
        goto_upload_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoVideo_upload_page = new Intent(upload_videos_main.this,video_upload_page.class);
                gotoVideo_upload_page.putExtra("loginUserId",loginUserId);
                gotoVideo_upload_page.putExtra("loginUserNick",loginUserNick);
                startActivityForResult(gotoVideo_upload_page,up_Load_Code);
            }
        });

    }

    // (인텐트 결과)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1111){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, "동영상 업로드 성공", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "동영상 업로드 취소", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
