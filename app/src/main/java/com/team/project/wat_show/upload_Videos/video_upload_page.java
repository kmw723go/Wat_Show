package com.team.project.wat_show.upload_Videos;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sun.mail.imap.Rights;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

public class video_upload_page extends AppCompatActivity {
    Integer VIDEOFILE_REQUEST = 1111;
    Integer ThumFILE_REQUEST = 1112;

    String loginUserId;
    String loginUserNick;

    //ip 주소
    ip ip = new ip();
    String ipad = ip.getIp();


    // 비디오
    Uri video_URI;
    Boolean setVideo = false;
    String video_name;

    // 썸네일
    Uri thum_URI;
    File thum_file;
    Boolean setThum = false;

    // 제목 , 설명 , 키워드
    String title;
    String explainComment;
    String keyWord="";

    // 작성시간
    String formatDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_page);

        // 사용자 아이디 닉네임 받아오기
        getUserData();

        // 동영상 가지고 오기
        getVideoFromGallery();

        // 동영상 업로드 취소 버튼
        cancelUploadVideos();

        // 썸네일 가지고 오기
        getThemFromGallery();

        // 썸네일 취소 버튼
        cancelUploadThum();

        // 저장 버튼
        postData();

        // 스피너 설정
        setSpinner();

        // 권한확인
        requirePermission();

        // 취소버튼
        cancelBtn();
    }

    // 취소버튼
    public void cancelBtn(){
        Button VideoConent_Btn = (Button)findViewById(R.id.VideoConent_Btn);
        VideoConent_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 사용자 아이디 받아오기
    public void getUserData() {
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");
    }

    // 저장 버튼
    public void postData() {
        Button postVideoCotent_Btn = (Button) findViewById(R.id.postVideoCotent_Btn);
        postVideoCotent_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getData();
            }
        });
    }

    // (저장 )데이터 값 가져오기
    public void getData() {
        // 제목
        EditText upload_video_title = (EditText) findViewById(R.id.upload_video_title);
        title = upload_video_title.getText().toString();

        // 설명
        EditText upload_video_content = (EditText) findViewById(R.id.upload_video_content);
        explainComment = upload_video_content.getText().toString();


        Log.d("타이틀",title);
        Log.d("설명",explainComment);
        Log.d("키워드",keyWord);


        if(title.equals("")||TextUtils.isEmpty(title)||title==null||
                explainComment.equals("")||TextUtils.isEmpty(explainComment)||explainComment==null||
                keyWord.equals("--키워드 설정--")|| keyWord.equals("")||TextUtils.isEmpty(keyWord)||keyWord==null ||
                setVideo == false || setThum == false){
            Toast.makeText(this, "입력되지 않은 데이터가 있습니다.", Toast.LENGTH_SHORT).show();
        }else{
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
            formatDate = sdfNow.format(date);

            sendVideoContentToServer();
        }


    }

    //  (서버 연결 ) 작성 데이터 전송
    public void sendVideoContentToServer() {


        class sendDataToHttp extends AsyncTask<Void, Void, String> {
            String serverUrl = ipad + "/upload_Videos/create_video_content.php";
            OkHttpClient client = new OkHttpClient();
            Context context;
            ProgressDialog dialog = new ProgressDialog(video_upload_page.this);

            public sendDataToHttp(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 전송중..");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                ContentResolver contentResolver = context.getContentResolver();
                final String contentType = contentResolver.getType(video_URI);
                final AssetFileDescriptor fd;
                try {
                    fd = contentResolver.openAssetFileDescriptor(video_URI, "r");

                    if (fd == null) {
                        throw new FileNotFoundException("could not open file descriptor");
                    }


                    RequestBody videoFile = new RequestBody() {
                        @Override
                        public long contentLength() {
                            return fd.getDeclaredLength();
                        }

                        @Override
                        public MediaType contentType() {
                            return MediaType.parse(contentType);
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            try (InputStream is = fd.createInputStream()) {
                                sink.writeAll(Okio.buffer(Okio.source(is)));
                            }
                        }
                    };


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("loginUserId", loginUserId)
                            .addFormDataPart("title", title)
                            .addFormDataPart("explainComment", explainComment)
                            .addFormDataPart("keyWord", keyWord)
                            .addFormDataPart("time",formatDate)
                            .addFormDataPart("Vfile", "fname", videoFile)
                            .addFormDataPart("Tfile", "fname", RequestBody.create(MultipartBody.FORM, thum_file))
                            .build();

                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            try {
                                fd.close();
                            } catch (IOException ex) {
                                e.addSuppressed(ex);
                            }
                            Log.d("실패", "failed", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            Log.d("결과", "" + result);
                            fd.close();

                            video_upload_page.this.runOnUiThread(new Runnable() {
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

                            // 종료
                            setResult(RESULT_OK);
                            finish();
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

        }

        sendDataToHttp sendData = new sendDataToHttp(this);
        sendData.execute();
    }

    // ---------------------------   테그 관련 -----------------------------------------
    public void setSpinner(){

        // 1차 키워드
        Spinner mainTag = (Spinner)findViewById(R.id.video_tag1);
        mainTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 0){
                    keyWord = "";
                }else if( position == 1){
                    keyWord = "코미디";
                }else if( position == 2){
                    keyWord = "게임";
                }else if( position == 3){
                    keyWord = "리뷰";
                }else if( position == 4){
                    keyWord = "라디오";
                }else if( position == 5){
                    keyWord = "뷰티";
                }else if( position == 6){
                    keyWord = "여행";
                }else if( position == 7){
                    keyWord = "음식";
                }else if( position == 8){
                    keyWord = "영화";
                }else if( position == 9){
                    keyWord = "음악";
                }else if( position == 10){
                    keyWord = "패션";
                }else if( position == 11){
                    keyWord = "기타";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //--------------------------- 동영상  섬네일 관련 -----------------------------------

    // 동영상 앨범 열기
    public void getVideoFromGallery() {
        TextView getVideoBtn = (TextView) findViewById(R.id.getVideoBtn);
        getVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리 동영상 호출
                Uri uri = Uri.parse("content://media/external/images/media");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, VIDEOFILE_REQUEST);

            }
        });

    }

    // 동영상 업로드 취소
    public void cancelUploadVideos() {
        TextView getVideoCancelBtn = (TextView) findViewById(R.id.getVideoCancelBtn);
        getVideoCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_URI = null;
                setVideo = false;


                // 파일 명
                TextView getVideoName = (TextView) findViewById(R.id.getVideoName);
                video_name = null;
                getVideoName.setVisibility(View.GONE);
                getVideoName.setText("");

                // 이미지 바꿔주기
                ImageView video_upload_check_no = (ImageView) findViewById(R.id.video_upload_check_no);
                video_upload_check_no.setVisibility(View.VISIBLE);

                ImageView video_upload_check_yes = (ImageView) findViewById(R.id.video_upload_check_yes);
                video_upload_check_yes.setVisibility(View.GONE);

                // 등록 버튼 보이기

                // 취소 글자 띄워주고 등록 없애기
                TextView getVideoBtn = (TextView) findViewById(R.id.getVideoBtn);
                getVideoBtn.setVisibility(View.VISIBLE);

                TextView getVideoCancelBtn = (TextView) findViewById(R.id.getVideoCancelBtn);
                getVideoCancelBtn.setVisibility(View.GONE);

                Toast.makeText(video_upload_page.this, "동영상을 다시 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 갤러리 사진 앨범 열기
    public void getThemFromGallery() {
        ImageView getthumBtn = (ImageView) findViewById(R.id.getthumBtn);
        getthumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, ThumFILE_REQUEST);
            }
        });
    }

    // 썸네일 등록 취소
    public void cancelUploadThum() {
        ImageView getthumBtn = (ImageView) findViewById(R.id.getthumBtn);
        getthumBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (thum_URI == null) {
                    Toast.makeText(video_upload_page.this, "등록된 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        ImageView getthumBtn = (ImageView) findViewById(R.id.getthumBtn);
                        Glide.with(video_upload_page.this).load(R.drawable.default_image).into(getthumBtn);

                        thum_URI = null;
                        thum_file = null;
                        setThum = false;
                    } catch (Exception e) {
                        Log.d("썸네일 취소 실패", "");
                    }

                }
                return true;
            }
        });
    }

    // 절대 경로 찾기 ( 썸네일 받아올때 사용 )
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String realPath = cursor.getString(column_index);
        Log.d("파일의 절대 경로", realPath);


        cursor.close();
        return realPath;
    }

    // 동영상 사진 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 동영상
        if (requestCode == VIDEOFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            video_URI = uri;
            setVideo = true;

            // 동영상 파일명 가지고 오기
            video_name = getName(video_URI);



            // 파일 이름 보여주기
            TextView getVideoName = (TextView) findViewById(R.id.getVideoName);
            getVideoName.setText(video_name);
            getVideoName.setVisibility(View.VISIBLE);


            // 취소 글자 띄워주고 등록 없애기
            TextView getVideoBtn = (TextView) findViewById(R.id.getVideoBtn);
            getVideoBtn.setVisibility(View.GONE);

            TextView getVideoCancelBtn = (TextView) findViewById(R.id.getVideoCancelBtn);
            getVideoCancelBtn.setVisibility(View.VISIBLE);


            // 이미지 바꿔주기
            ImageView video_upload_check_no = (ImageView) findViewById(R.id.video_upload_check_no);
            video_upload_check_no.setVisibility(View.GONE);

            ImageView video_upload_check_yes = (ImageView) findViewById(R.id.video_upload_check_yes);
            video_upload_check_yes.setVisibility(View.VISIBLE);

        } else {
            if (video_URI == null) {
                setVideo = false;
            } else {
                setVideo = true;
            }
        }

        // 썸네일
        if (requestCode == ThumFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            thum_URI = uri;

            String test = getPath(thum_URI);

            // 파일로 변환
            thum_file = new File(test);

            // 화면에 보여주기
            ImageView getthumBtn = (ImageView) findViewById(R.id.getthumBtn);
            Glide.with(this).load(thum_URI).into(getthumBtn);

            setThum = true;
        } else {
            if (thum_file == null) {
                setThum = false;
            } else {
                setThum = true;
            }
        }

    }

    //파일명 찾기
    private String getName(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    // ------------------------------- 권한 관련 -------------------------------------------
    //권한 확인
    public void requirePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest
                    .permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }

        } else {
        }

    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                break;
            }
        }

    }


}
