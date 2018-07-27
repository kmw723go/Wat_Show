package com.team.project.wat_show.upload_Videos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class showVideoContent extends AppCompatActivity {

    String loginUserId;
    video_content vc;


    //ip
    ip ip = new ip();
    String ipad = ip.getIp();

    // 설명보기
    Boolean showExplain = false;

    // 동영상 관련
    MediaController mc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.show_video_content);

        // 사용자 및 컨텐츠 데이터 받아오기
        getUserData();

    }

    // 사용자 데이터 받아오기
    public void getUserData(){
        vc = (video_content) getIntent().getSerializableExtra("showData");
        loginUserId = getIntent().getStringExtra("loginUserId").toString();

        // 비로그인 사용자 인경우에는 loginUserId를 따로 처리해야 것다 .

        // 로그인 사용자 정보 가져오기
        if(loginUserId.equals("비로그인")){

        }else{
            getUserDataHttp();
        }


        // 데이터 뿌려주기
        setDataContent();

    }

    // ( 서버 연결 ) 사용자 프로필과, 추천 비추천, 즐겨찾기 상태 받아오기
    public void getUserDataHttp(){
        class getUserDatas extends AsyncTask<Void,Void,String>{
            OkHttpClient client = new OkHttpClient();
            ProgressDialog dialog = new ProgressDialog(showVideoContent.this);

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

                String serverUrl = ipad+"/userData/userRecoDatas.php";
                String result = "";
                try {
                    // 보낼 데이터 담기
                    RequestBody sendDatas = new FormBody.Builder()
                            .add("loginUserId", loginUserId)
                            .add("no",vc.dataNo)
                            .build();

                    // 요청하면서  데이터 보내기
                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(sendDatas)
                            .build();

                    // 응답  (response.body().string() 는  1회만 사용이 가능하다 )
                    Response response = client.newCall(request).execute();
                    result = response.body().string();

                    Log.d("showVideoContent 사용자 정보",result);

                }catch (Exception e){
                }
                    // 가지고 온 데이터
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
                Log.d("사용자 정보 결과",""+s);
            }
        }

        getUserDatas gUD = new getUserDatas();
        gUD.execute();
    }

    // 데이터 뿌려주기
    public void setDataContent(){

        // 제작자 메뉴 설정
        if(loginUserId.equals(vc.makeUserId)){
            LinearLayout makeUserMenu = (LinearLayout)findViewById(R.id.makeUserMenu);
            makeUserMenu.setVisibility(View.VISIBLE);
        }

        // 작성자 프로필
        ImageView video_maker_user_profile = (ImageView)findViewById(R.id.video_maker_user_profile);
        Glide.with(this).load(ipad +vc.makeUserProfile).into(video_maker_user_profile);

        //닉네임
        TextView video_maker_user_Nick = (TextView)findViewById(R.id.video_maker_user_Nick);
        video_maker_user_Nick.setText(vc.makeUserNick);

        // 작성자 구독자수
        TextView video_content_sCount = (TextView)findViewById(R.id.video_content_sCount);
        video_content_sCount.setText(String.valueOf(vc.sCount)+"명");


        // 추천수
        TextView recoCount = (TextView)findViewById(R.id.recoCount);
        recoCount.setText(String.valueOf(vc.recommend));

        // 비추천수
        TextView unrecoCount = (TextView)findViewById(R.id.unrecoCount);
        unrecoCount.setText(String.valueOf(vc.unrecommend));

        // 제목
        TextView showVideoContent_title = (TextView)findViewById(R.id.showVideoContent_title);
        showVideoContent_title.setText(vc.content_title);

        // 키워드
        TextView video_content_keyWord = (TextView)findViewById(R.id.video_content_keyWord);
        video_content_keyWord.setText(vc.content_keyWord);

        // 설명
        TextView video_content_explain = (TextView)findViewById(R.id.video_content_explain);
        video_content_explain.setText(vc.content_explain);


        // 설명 보기 버튼.
        ImageView show_explainBtn = (ImageView)findViewById(R.id.show_explainBtn);
        show_explainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showExplain == false){
                    showExplain = true;
                    LinearLayout explainLayout = (LinearLayout)findViewById(R.id.explainLayout);
                    explainLayout.setVisibility(View.VISIBLE);

                }else if(showExplain == true){
                    showExplain = false;
                    LinearLayout explainLayout = (LinearLayout)findViewById(R.id.explainLayout);
                    explainLayout.setVisibility(View.GONE);
                }
            }
        });

        

        // 미디어 컨트롤러
        mc = new MediaController(this);


        // 동영상
        setVideos();



        // 수정  삭제 버튼 이벤트
        delete_data();
        edit_data();

    }

    // 비디오 설정
    public void setVideos(){

        //비디오 사이즈 재설정을 필요로 함
        String url = ipad+"/Video_dir/"+vc.content_vPath;



        VideoView video_view = (VideoView)findViewById(R.id.video_view);
        mc.setAnchorView(video_view);


        video_view.setVideoURI(Uri.parse(url));
        video_view.requestFocus();


        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                VideoView video_view = (VideoView)findViewById(R.id.video_view);
                video_view.start();
            }
        });

    }


    // 삭제 버튼
    public void delete_data(){
        TextView delete_video_contentTextView=(TextView)findViewById(R.id.delete_video_content);
        delete_video_contentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dataSelect = new AlertDialog.Builder(showVideoContent.this);
                dataSelect.setTitle("알림");
                dataSelect.setMessage("게시물을 삭제 하시겠습니까?");

                dataSelect.setPositiveButton("삭제",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setDeleteHttp();
                    }
                });

                dataSelect.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dataSelect.show();   // 실행

            }
        });
    }

    //( 서버 연결 삭제 )
    public void setDeleteHttp(){
        class DeleteFromHttp extends AsyncTask<Void, Void, String> {

            OkHttpClient client = new OkHttpClient();
            String contentNo = vc.dataNo;
            String vPath = vc.content_vPath;
            String thPath = vc.content_thPath;

            ProgressDialog dialog = new ProgressDialog(showVideoContent.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("서버에 전송중..");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String serverUrl = ipad+"/upload_Videos/delete_video_content.php";
                String result = "";
                try {

                    // 보낼 데이터 담기
                    RequestBody sendDatas = new FormBody.Builder()
                            .add("contentNo", contentNo)
                            .add("vPath", vPath)
                            .add("thPath", thPath)
                            .build();

                    // 요청하면서  데이터 보내기
                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(sendDatas)
                            .build();

                    // 응답  (response.body().string() 는  1회만 사용이 가능하다 )
                    Response response = client.newCall(request).execute();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // 종료
                            // 메인 페이지로 이동

                            showVideoContent.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if ( dialog != null && dialog.isShowing()){
                                            dialog.dismiss();
                                        }
                                    }catch (Exception e){

                                    }
                                }
                            });
                            setResult(RESULT_OK);
                            finish();
                        }
                    });

                    // 가지고 온 데이터
                    result = response.body().string();


                } catch (IOException e) {
                    e.printStackTrace();
                }


                return result;
            }

            @Override
            protected void onPostExecute(String a) {
                super.onPostExecute(a);


                Log.d("삭제 결과",""+a);
                if(a.equals("완료")){
                    Toast.makeText(showVideoContent.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("삭제 실패",""+a);
                }

            }
        }  // 클래스 끝

        // Url. 연결
        new DeleteFromHttp().execute();
    }

    // 수정 버튼
    public void edit_data(){
        TextView edit_video_content = (TextView)findViewById(R.id.edit_video_content);
        edit_video_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dataSelect = new AlertDialog.Builder(showVideoContent.this);
                dataSelect.setTitle("알림");
                dataSelect.setMessage("게시물을 수정 하시겠습니까?");

                dataSelect.setPositiveButton("수정",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent gotoEditPage =new Intent(showVideoContent.this,video_Edit_Page.class);
                        gotoEditPage.putExtra("editData",vc);
                        gotoEditPage.putExtra("loginUserId",loginUserId);
                        startActivity(gotoEditPage);
                        finish();
                    }
                });

                dataSelect.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dataSelect.show();   // 실행
            }
        });

    }

    //--------------------------------------- 버튼 이벤트 ----------------------------------------
   // 추천 비추천 이벤트
    public void recoBtnEvent(){

        // 추천 버튼
        ImageView recoBtn = (ImageView)findViewById(R.id.recoBtn);
        recoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        // 비추천 버튼
        ImageView unrecoBtn = (ImageView)findViewById(R.id.unrecoBtn);
        unrecoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    // 즐겨찾기
    public void myListBtnEvent(){

        //즐겨찾기 버튼
        ImageView mylist_Vcontent =(ImageView)findViewById(R.id.mylist_Vcontent);
        mylist_Vcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



}
