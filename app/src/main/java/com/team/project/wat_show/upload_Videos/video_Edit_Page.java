package com.team.project.wat_show.upload_Videos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class video_Edit_Page extends AppCompatActivity {

    Integer ThumFILE_REQUEST = 1112;

    //ip
    ip ip = new ip();
    String ipad = ip.getIp();

    String loginUserId;

    // 기존데이터 백업
    video_content backUpVc;

    // 기존 테그의 인덱스 위치
    public int tegIndex = 999;

    // 새로운 데이터
    String title;
    String keyWord;
    String explain;

    // 썸네일
    Uri thum_URI;
    File thum_file;

    Boolean setThum = true;
    Boolean changeImgae = false;

    // 모드 1 ( 이미지가 안바뀜 ) 모드2 (이미지가 바뀜 ) 모드3 (이미지가 없음)
    int mode = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video__edit__page);

        // 데이터 가지고 오기
        getDatas();


        // 저장 버튼 이벤트
        edit_Save_Btn();

        // 취소 버튼
        cancelBtn();
    }

    // 데이터 가지고 오기
    public void getDatas() {
        loginUserId = getIntent().getStringExtra("loginUserId");
        backUpVc = (video_content) getIntent().getSerializableExtra("editData");


        // 기존 데이터 뿌려주기
        setData();
    }

    // 수정전 기존 데이터 뿌려주기
    public void setData() {

        // 제목
        EditText edit_video_title = (EditText) findViewById(R.id.edit_video_title);
        edit_video_title.setText(backUpVc.content_title);

        // 키워드
        Spinner edit_tag1 = (Spinner) findViewById(R.id.edit_tag1);
        edit_tag1.setSelection(findIndex());

        // 초기 세팅.
        keyWord = backUpVc.content_keyWord;

        // 이미지 설정
        ImageView edit_getthumBtn = (ImageView) findViewById(R.id.edit_getthumBtn);
        Glide.with(this).load(ipad + "/Thum_dir/" + backUpVc.content_thPath).into(edit_getthumBtn);


        // 설명 설정
        EditText edit_video_content = (EditText) findViewById(R.id.edit_video_content);
        edit_video_content.setText(backUpVc.content_explain);


        // 이미지 뷰 클릭 이벤트
        setThumImage();

        // 스피너
        setSpinner();
    }

    // 인덱스 값 찾아오기
    public int findIndex() {
        if (backUpVc.content_keyWord.equals("코미디")) {
            tegIndex = 1;

        } else if (backUpVc.content_keyWord.equals("게임")) {
            tegIndex = 2;

        } else if (backUpVc.content_keyWord.equals("리뷰")) {
            tegIndex = 3;
        } else if (backUpVc.content_keyWord.equals("라디오")) {
            tegIndex = 4;
        } else if (backUpVc.content_keyWord.equals("뷰티")) {
            tegIndex = 5;
        } else if (backUpVc.content_keyWord.equals("여행")) {
            tegIndex = 6;
        } else if (backUpVc.content_keyWord.equals("음식")) {
            tegIndex = 7;
        } else if (backUpVc.content_keyWord.equals("영화")) {
            tegIndex = 8;
        } else if (backUpVc.content_keyWord.equals("음악")) {
            tegIndex = 9;
        } else if (backUpVc.content_keyWord.equals("패션")) {
            tegIndex = 10;
        } else if (backUpVc.content_keyWord.equals("기타")) {
            tegIndex = 11;
        }

        return tegIndex;
    }

    // 이미지 뷰 클릭 이벤트
    public void setThumImage() {
        ImageView edit_getthumBtn = (ImageView) findViewById(R.id.edit_getthumBtn);
        edit_getthumBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (setThum == true) {
                    ImageView edit_getthumBtn = (ImageView) findViewById(R.id.edit_getthumBtn);
                    Glide.with(video_Edit_Page.this).load(R.drawable.default_image).into(edit_getthumBtn);
                    thum_URI = null;
                    setThum = false;

                } else {
                    Toast.makeText(video_Edit_Page.this, "등록된 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        edit_getthumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getThemFromGallery();
            }
        });

    }

    // 갤러리 사진 앨범 열기
    public void getThemFromGallery() {
        ImageView edit_getthumBtn = (ImageView) findViewById(R.id.edit_getthumBtn);
        edit_getthumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, ThumFILE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 썸네일
        if (requestCode == ThumFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            thum_URI = uri;

            String test = getPath(thum_URI);

            // 파일로 변환
            thum_file = new File(test);

            // 화면에 보여주기
            ImageView edit_getthumBtn = (ImageView) findViewById(R.id.edit_getthumBtn);
            Glide.with(this).load(thum_URI).into(edit_getthumBtn);
            changeImgae = true;
            setThum = true;
        } else {
            if (thum_file == null) {
                setThum = false;
            } else {
                setThum = true;
            }
        }
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

    // ---------------------------   테그 관련 -----------------------------------------
    public void setSpinner() {

        // 1차 키워드
        Spinner mainTag = (Spinner) findViewById(R.id.edit_tag1);
        mainTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    keyWord = "";
                } else if (position == 1) {
                    keyWord = "코미디";
                } else if (position == 2) {
                    keyWord = "게임";
                } else if (position == 3) {
                    keyWord = "리뷰";
                } else if (position == 4) {
                    keyWord = "라디오";
                } else if (position == 5) {
                    keyWord = "뷰티";
                } else if (position == 6) {
                    keyWord = "여행";
                } else if (position == 7) {
                    keyWord = "음식";
                } else if (position == 8) {
                    keyWord = "영화";
                } else if (position == 9) {
                    keyWord = "음악";
                } else if (position == 10) {
                    keyWord = "패션";
                } else if (position == 11) {
                    keyWord = "기타";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    // 저장 버튼
    public void edit_Save_Btn() {
        Button edit_postVideoCotent_Btn = (Button) findViewById(R.id.edit_postVideoCotent_Btn);
        edit_postVideoCotent_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 타이틀
                EditText edit_video_title = (EditText) findViewById(R.id.edit_video_title);
                title = edit_video_title.getText().toString();

                // 설명 가지고 오기
                EditText edit_video_content = (EditText) findViewById(R.id.edit_video_content);
                explain = edit_video_content.getText().toString();


                // 이미지 변동 및 , 존재 여부 체크
                if (setThum == true) {
                    // 이미지가 변경됨
                    if (changeImgae == false) {
                        mode = 1;
                    } else {
                        mode = 2;
                    }
                } else {
                    // 이미지가 변경되지 않음
                    mode = 3;
                }

                if (title == null || title.equals("") || TextUtils.isEmpty(title) ||
                        explain == null || explain.equals("") || TextUtils.isEmpty(explain) ||
                        mode == 3 || keyWord.equals("")) {
                    Toast.makeText(video_Edit_Page.this, "데이터를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 모드에 맞게 서버에 전송  1은 이미지 안바뀜  2는 이미지 바뀜
                    if (mode == 1) {
                        editDbHttpMode1();
                    } else if (mode == 2) {
                        editDbHttpMode2();
                    }

                }


            }
        });
    }

    // 취소버튼
    public void cancelBtn() {
        Button edit_VideoConent_Btn = (Button) findViewById(R.id.edit_VideoConent_Btn);
        edit_VideoConent_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // 서버에 전송  ( 기존이미지  유지  - 데이터만 변경 )
    public void editDbHttpMode1() {
        class getDataFromHttp extends AsyncTask<Void, Void, String> {

            OkHttpClient client = new OkHttpClient();

            String loginUserId;
            ProgressDialog dialog = new ProgressDialog(video_Edit_Page.this);
            String result = "";

            public getDataFromHttp(String loginUserId) {
                this.loginUserId = loginUserId;
            }

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
                String serverUrl = ipad + "/upload_Videos/edit_video_content_mode1.php";

                try {


                    // 보낼 데이터 담기
                    RequestBody sendDatas = new FormBody.Builder()
                            .add("no", backUpVc.dataNo)
                            .add("title", title)
                            .add("explain", explain)
                            .add("keyWord", keyWord)
                            .build();

                    // 요청하면서  데이터 보내기
                    final Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(sendDatas)
                            .build();

                    // 응답  (response.body().string() 는  1회만 사용이 가능하다 )
                    Response response = client.newCall(request).execute();
                    result = response.body().string();

                    video_Edit_Page.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } catch (Exception e) {

                            }
                            Log.d("수정 결과 ",""+result);
                            finish();
                        }
                    });

                    // 가지고 온 데이터


                } catch (IOException e) {
                    e.printStackTrace();
                }


                return result;
            }

            @Override
            protected void onPostExecute(String a) {
                super.onPostExecute(a);


            }
        }  // 클래스 끝

        // Url. 연결
        new getDataFromHttp(loginUserId).execute();
    }

    // 서버 전송 ( 새로운 이미지  파일까지  전송 )
    public void editDbHttpMode2() {
        class sendDataToHttp extends AsyncTask<Void, Void, String> {
            String serverUrl = ipad + "/upload_Videos/edit_video_content_mode2.php";
            OkHttpClient client = new OkHttpClient();
            Context context;
            ProgressDialog dialog = new ProgressDialog(video_Edit_Page.this);
            String result;

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

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("loginUserId", loginUserId)
                        .addFormDataPart("no", backUpVc.dataNo)
                        .addFormDataPart("title", title)
                        .addFormDataPart("explain", explain)
                        .addFormDataPart("keyWord", keyWord)
                        .addFormDataPart("Tfile", "fname", RequestBody.create(MultipartBody.FORM, thum_file))
                        .build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("실패", "failed", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        result = response.body().string();
                        Log.d("결과", "" + result);

                        video_Edit_Page.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                } catch (Exception e) {

                                }
                                if(result.equals("성공")){
                                    Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{

                                }
                            }
                        });

                        // 종료
                        setResult(RESULT_OK);
                        finish();
                    }
                });

                return null;
            }

        }

        sendDataToHttp sendData = new sendDataToHttp(this);
        sendData.execute();

    }


}
