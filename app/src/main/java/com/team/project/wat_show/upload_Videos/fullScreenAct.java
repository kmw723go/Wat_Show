package com.team.project.wat_show.upload_Videos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.team.project.wat_show.R;

public class fullScreenAct extends Activity {

    VideoView videoView = null;
    int currenttime = 0;
    String Url="";
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_full_screen2);


        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            currenttime = extras.getInt("currenttime", 0);
            Url=extras.getString("Url");
        }
        progressDialog = ProgressDialog.show(this, "", "Loading...", true);
        videoView = (VideoView) findViewById(R.id.VideoViewfull);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri video = Uri.parse(Url);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer arg0) {
                progressDialog.dismiss();
                videoView.start();
                videoView.seekTo(currenttime);
            }
        });
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("currenttime", videoView.getCurrentPosition());
        setResult(5555, data);
        super.finish();
    }


}
