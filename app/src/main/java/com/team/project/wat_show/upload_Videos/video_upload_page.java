package com.team.project.wat_show.upload_Videos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.util.IOUtils;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    String loginUserId;
    String loginUserNick;

    //ip 주소
    ip ip = new ip();
    String ipad = ip.getIp();


    // 비디오
    AssetFileDescriptor afd;
    String videoPath;
    Uri video_URI;
    File videosFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_page);

        // 사용자 아이디 닉네임 받아오기
        getUserData();

        // 동영상 가지고 오기
        getVideoFromGallery();

        // 저장 버튼
        postData();

        requirePermission();
    }

    // 사용자 아이디 받아오기
    public void getUserData() {
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");
    }

    // 동영상 앨범 열기
    public void getVideoFromGallery() {
        ImageView getVideoBtn = (ImageView) findViewById(R.id.getVideoBtn);
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

    // 절대 경로 찾기
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String realPath = cursor.getString(column_index);
        Log.d("동영상 파일의 절대 경로", realPath);

        // File로 변환
        //videosFile = new File(Environment.getExternalStorageDirectory(),video_URI.getPath());
        videosFile = new File(realPath);
        cursor.close();
        return realPath;
    }

    // 저장
    public void postData() {
        Button postVideoConent_Btn = (Button) findViewById(R.id.postVideoConent_Btn);
        postVideoConent_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVideoContentToServer();
            }
        });
    }

    // 서버에 전송
    public void sendVideoContentToServer() {


        class sendDataToHttp extends AsyncTask<Void, Void, String> {
            String serverUrl = "http://54.180.2.34/upload_Videos/create_video_content.php";
            OkHttpClient client = new OkHttpClient();
            private int serverResponseCode;

            Context context;

            public sendDataToHttp(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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
                            .addFormDataPart("file", "fname",videoFile)
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
                            Log.d("결과",""+result);
                            fd.close();
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

        }

        sendDataToHttp sendData = new sendDataToHttp(this);
        sendData.execute();
    }


    // 전송 파일 종류 추출
    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();
            Log.d("임시경로", "" + uri.toString());

            video_URI = uri;

            // 절대 경로로 변환
            String path = getPath(uri);
            Log.d("절대 경로", "path : " + path);

            videoPath = path;

        }

    }


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
