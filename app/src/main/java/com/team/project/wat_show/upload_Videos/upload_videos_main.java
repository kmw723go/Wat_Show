package com.team.project.wat_show.upload_Videos;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class upload_videos_main extends AppCompatActivity {

    Integer up_Load_Code = 1111;


    //ip
    ip ip = new ip();
    String ipad = ip.getIp();


    String loginUserId;
    String loginUserNick;


    // 리사이클러뷰
    RecyclerView my_upload_videoList;
    ArrayList<video_content> video_datas = new ArrayList<>();
    video_content content;

    upload_videos_List_adapter vAdapter;

    String [] d1;
    String [] d2;

    String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_upload_videos_main);


        // 사용자 아이디 받아오기
        getUserData();



        // 동영상 업로드 버튼
        gotoUpload();

    }

    // ----------------------------  데이터 받아오기 ------------------------------------------

    // 사용자 아이디 받아오기
    public void getUserData() {
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");

        // 데이터 받아오기
        getMyUpload_data();
    }


    // ( 서버 전송 ) 데이터 가지고 오기
    // 만약에 데이터가 없는 경우 live_list_noContent 를 보여주고  my_upload_videoList를 Gone
    public void getMyUpload_data() {

        class getDataToHttp extends AsyncTask<Void, Void, String> {
            String serverUrl = ipad + "/upload_Videos/get_user_videos_data.php";
            OkHttpClient client = new OkHttpClient();
            Context context;
            ProgressDialog dialog = new ProgressDialog(upload_videos_main.this);

            public getDataToHttp(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 수신중..");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("loginUserId", loginUserId)
                            .build();

                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(requestBody)
                            .build();

                    // 응답  (response.body().string() 는  1회만 사용이 가능하다 )
                    Response response = client.newCall(request).execute();

                    // 가지고 온 데이터
                    result = response.body().string();

                }catch (Exception e){

                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }
                Log.d("동영상 컨텐츠 데이터",""+result);

                if(result.equals("없음")){
                    // 뷰설정
                    RecyclerView my_upload_videoList = (RecyclerView) findViewById(R.id.my_upload_videoList);
                    my_upload_videoList.setVisibility(View.GONE);
                    TextView live_list_noContent = (TextView)findViewById(R.id.live_list_noContent);
                    live_list_noContent.setVisibility(View.VISIBLE);
                }else{
                    // 뷰설정
                    TextView live_list_noContent = (TextView)findViewById(R.id.live_list_noContent);
                    live_list_noContent.setVisibility(View.GONE);
                    RecyclerView my_upload_videoList = (RecyclerView) findViewById(R.id.my_upload_videoList);
                    my_upload_videoList.setVisibility(View.VISIBLE);

                    // 받은 데이터 확인하기

                    try {
                        divideData(result);
                    }catch (Exception e){

                    }

                }
            }
        }

        getDataToHttp getData = new getDataToHttp(this);
        getData.execute();

    }

    // 데이터 쪼개기
    public void divideData(String result){
        d1 = result.split("%%%");
        for( int i =0 ; i < d1.length; i++){
            d2 = d1[i].split("@@@");
            content = new video_content(d2[0],d2[1],d2[2],d2[3],d2[4],d2[5],d2[6],d2[7]);
            video_datas.add(content);
        }

        // 리사이클러 뷰 세팅
        try {
            setVideosListSet();
        } catch (Exception e) {

        }

    }


    // 리사이클러뷰 세팅
    public void setVideosListSet() {

        my_upload_videoList = (RecyclerView) findViewById(R.id.my_upload_videoList);

        // 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        my_upload_videoList.setLayoutManager(layoutManager);

        vAdapter = new upload_videos_List_adapter(this,video_datas,loginUserId);
        my_upload_videoList.setAdapter(vAdapter);


    }

    // -------------------------------- 동영상 업로드 이동 --------------------------------------------
    public void gotoUpload() {
        ImageView goto_upload_Btn = (ImageView) findViewById(R.id.goto_upload_Btn);
        goto_upload_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoVideo_upload_page = new Intent(upload_videos_main.this, video_upload_page.class);
                gotoVideo_upload_page.putExtra("loginUserId", loginUserId);
                gotoVideo_upload_page.putExtra("loginUserNick", loginUserNick);
                startActivityForResult(gotoVideo_upload_page, up_Load_Code);
            }
        });

    }

    // (인텐트 결과)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "동영상 업로드 성공", Toast.LENGTH_SHORT).show();

                // 데이터 비워주고
                video_datas.clear();

                // 데이터 받아오기
                getMyUpload_data();

                vAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "동영상 업로드 취소", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
