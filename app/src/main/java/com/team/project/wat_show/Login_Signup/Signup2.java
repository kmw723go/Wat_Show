package com.team.project.wat_show.Login_Signup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team.project.wat_show.MainActivity;
import com.team.project.wat_show.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Signup2 extends AppCompatActivity implements View.OnClickListener{

    //Check는 유효성검사 결과 텍스트뷰, Input 입력창
    TextView SignupNickCheck,SignupEmailCheck;
    EditText SignupNickInput,SignupEmailInput;
    ImageView SignupProfile;
    private HttpConnection httpConn = new HttpConnection();
    String id,pw,mCurrentPhotoPath;
    private static final int MY_CAMERA = 120;
    private Uri imgUri, photoURI, albumURI,saveuri;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 3;
    File copyFile;
    //콜백메소드에서 중복검사인지 회원가입결과인지 확인
    String check;
    //닉네임 중복 검사 버튼 클릭 확인
    Boolean nickcheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        pw = intent.getStringExtra("pw");
        findView();
        checkPermission();
        findViewById(R.id.SignupNickConf).setOnClickListener(this);
        SignupProfile.setOnClickListener(this);
        findViewById(R.id.SignupSubmit).setOnClickListener(this);

        NickCheck();
        EmailCheck();
    }
    //뷰 선언
    public void findView(){
        SignupNickCheck = (TextView)findViewById(R.id.SignupNickCheck);
        SignupNickInput = (EditText)findViewById(R.id.SignupNickInput);
        SignupEmailCheck = (TextView)findViewById(R.id.SignupEmailCheck);
        SignupEmailInput = (EditText)findViewById(R.id.SignupEmailInput);
        SignupProfile = (ImageView)findViewById(R.id.SignupProfile);
    }

    //클릭시 이벤트 처리
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SignupNickConf:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("nick",SignupNickInput.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendData(1,jsonObject);
                break;
            case R.id.SignupProfile:
                makeDialog();
                break;
            case R.id.SignupSubmit:
                Boolean allcheck = true;
                if(!Pattern.matches("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$", SignupEmailInput.getText().toString())){
                    SignupEmailCheck.setText("형식에 맞춰 작성하세요");
                    SignupEmailCheck.setTextColor(RED);
                    SignupEmailCheck.setVisibility(View.VISIBLE);
                    SignupEmailInput.requestFocus();
                    allcheck = false;
                }
                if(!Pattern.matches("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{3,10}+$", SignupNickInput.getText().toString())){
                    SignupNickCheck.setText("형식에 맞춰 작성하세요");
                    SignupNickCheck.setTextColor(RED);
                    SignupNickCheck.setVisibility(View.VISIBLE);
                    SignupNickInput.requestFocus();
                    allcheck = false;
                }
                if(nickcheck == false){
                    allcheck = false;
                }
                if(allcheck == true){
                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("email",SignupEmailInput.getText().toString());
                        jsonObject2.put("nick",SignupNickInput.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendData(2,jsonObject2);
                }

                break;

        }
    }

    //앨범,사진찍기,크롭을 선택,결과값
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    imgUri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imgUri, "image/*");

                    intent.putExtra("outputX", 250);
                    intent.putExtra("outputY", 250);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, CROP_FROM_CAMERA);

                }
                break;
            }
            case FROM_CAMERA : {
                Log.e("카메라",data.toString());
                if(data.getData()!=null){
                    imgUri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imgUri, "image/*");

                    intent.putExtra("outputX", 250);
                    intent.putExtra("outputY", 250);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, CROP_FROM_CAMERA);

                }
                break;
            }
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                if (resultCode != RESULT_OK) {
                    return;
                }
                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/SmartWheel" + System.currentTimeMillis() + ".jpg";

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    storeCropImage(photo, filePath);
                    mCurrentPhotoPath = filePath;
                    saveuri = Uri.fromFile(new File(filePath));
                    Glide.with(Signup2.this).load(saveuri).apply(new RequestOptions().circleCrop()).into(SignupProfile);
                }

                // 임시 파일 삭제
                File f = new File(saveuri.getPath());
                if(f.exists())
                {
                    //  f.delete();
                }

                break;
            }
        }
    }

    //이미지 크롭하는 메소드
    private void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if(!directory_SmartWheel.exists()) {
            directory_SmartWheel.mkdirs();
        }
        copyFile = new File(filePath);
        BufferedOutputStream out = null;
        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        }catch (Exception e){e.printStackTrace();}
    }


    //카메라 선택 클릭
    public void takePhoto(){

        // 촬영 후 이미지 가져옴


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // 임시로 사용할 파일의 경로를 생성
            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            imgUri= Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
            Log.e("포토",imgUri.toString());
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imgUri);
            startActivityForResult(intent, FROM_CAMERA);

    }

    //앨범 선택 클릭
    public void selectAlbum(){

        //앨범 열기

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);


        intent.setType("image/*");


        startActivityForResult(intent, FROM_ALBUM);

    }

    //이미지 클릭시 다이얼로그
    private void makeDialog(){

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(Signup2.this);

        alt_bld.setTitle("사진 업로드").setCancelable(

                false).setPositiveButton("사진촬영",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Log.v("알림", "다이얼로그 > 사진촬영 선택");

                        // 사진 촬영 클릭

                        takePhoto();

                    }

                }).setNeutralButton("앨범선택",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int id) {

                        Log.v("알림", "다이얼로그 > 앨범선택 선택");

                        //앨범에서 선택

                        selectAlbum();

                    }

                }).setNegativeButton("취소",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Log.v("알림", "다이얼로그 > 취소 선택");

                        // 취소 클릭. dialog 닫기.

                        dialog.cancel();

                    }

                });

        AlertDialog alert = alt_bld.create();

        alert.show();

    }


    //퍼미션 수락 다이얼로그
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
            (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))){
                    new AlertDialog.Builder(this)
                            .setTitle("알림")
                            .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정ㅇ에서 해당 권한을 직접 허용하셔야합니다.")
                            .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:"+getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, MY_CAMERA);
            }
        }
    }

    //퍼미션 검사
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case MY_CAMERA:
                for(int i =  0; i < grantResults.length; i++){
                    //grantResults[] : 허용된 권한은 0, 거분한 권한은 -1
                    if(grantResults[i] < 0){
                        Toast.makeText(this, "해당 권한을 활성화 하셔야합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
            }break;
        }
    }

    //닉네임 유효성 검사
    public void NickCheck(){

        SignupNickInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = SignupNickInput.getText().toString();
                if(!Pattern.matches("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{3,10}+$", value)){
                    SignupNickCheck.setText("형식에 맞춰 작성하세요");
                    SignupNickCheck.setTextColor(RED);
                    SignupNickCheck.setVisibility(View.VISIBLE);
                    SignupNickInput.requestFocus();
                }else{
                    SignupNickCheck.setText("올바른 형식입니다");
                    SignupNickCheck.setTextColor(GREEN);
                    SignupNickCheck.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //이메일 유효성 검사
    public void EmailCheck(){

        SignupEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = SignupEmailInput.getText().toString();
                if(!Pattern.matches("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$", value)){
                    SignupEmailCheck.setText("형식에 맞춰 작성하세요");
                    SignupEmailCheck.setTextColor(RED);
                    SignupEmailCheck.setVisibility(View.VISIBLE);
                    SignupEmailInput.requestFocus();
                }else{
                    SignupEmailCheck.setText("올바른 형식입니다");
                    SignupEmailCheck.setTextColor(GREEN);
                    SignupEmailCheck.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //내부 클래스인 HttpConnection로 데이터값 전달
    private void sendData(final int i, final JSONObject json) {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(i, json, callback);
            }
        }.start();;
    }

    //http통신의 결과값 수신
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(final Call call, final IOException e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 메시지 큐에 저장될 메시지의 내용
                            Log.e("qweqwe", call+"//"+e.toString());
                        }
                    });

                }
            }).start();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            if (check.equals("1")) {
                if (body.equals("1")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(Signup2.this, "이미 사용중인 닉네임입니다", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(Signup2.this, "사용가능한 닉네임입니다", Toast.LENGTH_SHORT).show();
                                    nickcheck = true;
                                    Log.e("qwe",body);
                                }
                            });

                        }
                    }).start();
                }
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 메시지 큐에 저장될 메시지의 내용
                                if(body.equals("0")){
                                    Toast.makeText(Signup2.this, "회원가입에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Signup2.this, MainActivity.class);
                                    intent.putExtra("loginUserId",id);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Log.e("RESULT","에러 발생! ERRCODE = " + body);
                                    Toast.makeText(Signup2.this, "등록중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                }).start();
            }
        }

    };
    //okhttp를 통한 서버통신
    public class HttpConnection {

        private OkHttpClient client;

        private HttpConnection(){
            this.client = new OkHttpClient(); }


        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(int i,JSONObject json, Callback callback) {
            if(i == 1){
                String nick = null;
                check = "1";
                String encoNick = null;
                try {
                    nick = (String) json.get("nick");
                    encoNick = URLEncoder.encode(nick,"EUC-KR");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                RequestBody body = new FormBody.Builder()
                        .add("check","nick")
                        .add("nick", nick)
                        .build();
                Request request = new Request.Builder()
                        .url("http://54.180.2.34/Login_Signup/Id_Nick_Check.php")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }else{
                String email = null,nick = null;
                check = "2";
                try {
                   nick = (String) json.get("nick");
                   email = (String) json.get("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String encoId = null,encoPw = null,encoNick = null,encoEmail = null,encoProfile = null;
                try {
                     encoId = URLEncoder.encode(id,"UTF-8");
                     encoPw = URLEncoder.encode(pw,"UTF-8");
                     encoNick = URLEncoder.encode(nick,"UTF-8");
                     encoEmail = URLEncoder.encode(email,"UTF-8");
                     encoProfile = URLEncoder.encode("SmartWheel" + System.currentTimeMillis() + ".jpg","UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MediaType MEDIA_TYPE_JPG = MediaType.parse("image/*");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", encoId)
                        .addFormDataPart("pw", encoPw)
                        .addFormDataPart("nick", encoNick)
                        .addFormDataPart("email", encoEmail)
                        .addFormDataPart("file", encoProfile,RequestBody.create(MultipartBody.FORM, copyFile))
                        .build();
                Request request = new Request.Builder()
                        .url("http://54.180.2.34/Login_Signup/Signup.php")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }

        }
        
    }
}
