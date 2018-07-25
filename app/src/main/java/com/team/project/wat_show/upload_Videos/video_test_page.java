package com.team.project.wat_show.upload_Videos;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

public class video_test_page extends AppCompatActivity {

    String loginUserId;
    video_content vc;


    //ip
    com.team.project.wat_show.ip ip = new ip();
    String ipad = ip.getIp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test_page);

        vc = (video_content) getIntent().getSerializableExtra("showData");
        loginUserId = getIntent().getStringExtra("loginUserId").toString();


        String url = ipad+"/Video_dir/"+vc.content_vPath;



        VideoView video_view = (VideoView)findViewById(R.id.v_view_test);
        video_view.setVideoURI(Uri.parse(url));
        video_view.requestFocus();

        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                VideoView video_view = (VideoView)findViewById(R.id.v_view_test);
                video_view.start();
            }
        });




    }
}
