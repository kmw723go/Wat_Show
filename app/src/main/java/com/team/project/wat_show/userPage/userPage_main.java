package com.team.project.wat_show.userPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team.project.wat_show.Login_Signup.Login;
import com.team.project.wat_show.Login_Signup.Signup2;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Color.RED;

public class userPage_main extends AppCompatActivity implements
        View.OnClickListener {

    private HttpConnection httpConn = new HttpConnection();
    EditText userPage_prePass,userPage_Nick,userPage_Email,userPage_Pass,userPage_Con;
    ImageView userPage_profile;
    Button userPage_confirm_btn,userPage_submit;
    LinearLayout userPage_confirm,userPage_edit;
    int checksendNum;
    Integer loginReCode = 1111;
    String loginUserId,mCurrentPhotoPath,prePass;
    private Uri imgUri,saveuri;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 3;
    File copyFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_page_main);
        loginUserId = getIntent().getStringExtra("Id");
        findView();
        findViewById(R.id.userPage_confirm_btn).setOnClickListener(this);
        findViewById(R.id.userPage_submit).setOnClickListener(this);
        userPage_profile.setOnClickListener(this);
    }

    public void findView(){
        userPage_prePass = (EditText)findViewById(R.id.userPage_prePass);
        userPage_Nick = (EditText)findViewById(R.id.userPage_Nick);
        userPage_Email = (EditText)findViewById(R.id.userPage_Email);
        userPage_Pass = (EditText)findViewById(R.id.userPage_Pass);
        userPage_Con = (EditText)findViewById(R.id.userPage_Con);
        userPage_profile = (ImageView)findViewById(R.id.userPage_profile);
        userPage_confirm_btn = (Button)findViewById(R.id.userPage_confirm_btn);
        userPage_submit = (Button)findViewById(R.id.userPage_submit);
        userPage_confirm = (LinearLayout)findViewById(R.id.userPage_confirm);
        userPage_edit = (LinearLayout)findViewById(R.id.userPage_edit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.userPage_confirm_btn:
                Passcheck();
                break;
            case R.id.userPage_submit:
                editConfirm();
                break;
            case R.id.userPage_profile:
                makeDialog();
                break;
        }
    }

    public void Passcheck()  {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Pass",userPage_prePass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prePass = userPage_prePass.getText().toString();
        sendData(2,jsonObject);
    }

    public void editConfirm(){
        Boolean allcheck = true;
        if(!Pattern.matches("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$", userPage_Email.getText().toString())){
            userPage_Email.requestFocus();
            allcheck = false;
        }
        if(!Pattern.matches("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{3,10}+$", userPage_Nick.getText().toString())){
            userPage_Nick.requestFocus();
            allcheck = false;
        }
        if(!userPage_prePass.getText().toString().equals("")){
            if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", userPage_Pass.getText().toString())){
                userPage_Pass.requestFocus();
                allcheck = false;
            }
            if(!userPage_Con.getText().toString().equals(userPage_Pass.getText().toString())){
                userPage_Con.requestFocus();
                allcheck = false;
            }
        }
        if(allcheck == true){
            JSONObject jsonObject2 = new JSONObject();
            try {
                jsonObject2.put("Email",userPage_Email.getText().toString());
                jsonObject2.put("Nick",userPage_Nick.getText().toString());
                if(userPage_prePass.getText().toString().equals("")){
                    jsonObject2.put("Pass",prePass);
                }else{
                    jsonObject2.put("Pass",userPage_Pass.getText().toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }sendData(3,jsonObject2);

        }
    }
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
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imgUri, "image/*");

                intent.putExtra("outputX", 250);
                intent.putExtra("outputY", 250);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

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
                    Glide.with(userPage_main.this).load(saveuri).apply(new RequestOptions().circleCrop()).into(userPage_profile);
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

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(userPage_main.this);

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
    public class HttpConnection {

        private OkHttpClient client;

        private HttpConnection(){ this.client = new OkHttpClient(); }


        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(int j,JSONObject jsonObject, Callback callback) {
            checksendNum = j;
            if(checksendNum == 1){
                String encoId=null;
                try {
                    encoId = URLEncoder.encode(jsonObject.getString("Id"),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = new FormBody.Builder()
                        .add("loginUserId",encoId)
                        .build();
                Request request = new Request.Builder()
                        .url("http://54.180.2.34/userData/getUserData.php")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }
            else if(checksendNum == 2){
                String encoId = null,encoPw=null,pw;

                try {
                    pw = (String) jsonObject.get("Pass");
                    encoId = URLEncoder.encode(loginUserId,"UTF-8");
                    encoPw = URLEncoder.encode(pw,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = new FormBody.Builder()
                        .add("id",encoId)
                        .add("pw",encoPw)
                        .build();
                Log.e("qweqwe1",encoId+"//"+encoPw);
                Request request = new Request.Builder()
                        .url("http://54.180.2.34/Login_Signup/Login.php")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }else if(checksendNum == 3){
                String check = null;
                if(copyFile == null){
                    check = "NOT";
                    String encoId = null,encoPw=null,encoNick=null,encoEmail =null,nick,email,pw;
                    try {
                        pw = (String) jsonObject.get("Pass");
                        nick = (String) jsonObject.get("Nick");
                        email = (String) jsonObject.get("Email");
                        encoPw = URLEncoder.encode(pw,"UTF-8");
                        encoId = URLEncoder.encode(loginUserId,"UTF-8");
                        encoNick = URLEncoder.encode(nick,"UTF-8");
                        encoEmail = URLEncoder.encode(email,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = new FormBody.Builder()
                            .add("check",check)
                            .add("id",encoId)
                            .add("pw",encoPw)
                            .add("nick",encoNick)
                            .add("email",encoEmail)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://54.180.2.34/userData/setUserData.php")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(callback);
                }else{
                    check = "OK";
                    String encoId = null,encoPw = null,encoNick = null,encoEmail = null,encoProfile = null,nick,email,pw;
                    try {
                        pw = (String) jsonObject.get("Pass");
                        nick = (String) jsonObject.get("Nick");
                        email = (String) jsonObject.get("Email");
                        encoPw = URLEncoder.encode(pw ,"UTF-8");
                        encoId = URLEncoder.encode(loginUserId,"UTF-8");
                        encoNick = URLEncoder.encode(nick,"UTF-8");
                        encoEmail = URLEncoder.encode(email,"UTF-8");
                        encoProfile = URLEncoder.encode("SmartWheel" + System.currentTimeMillis() + ".jpg","UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("check",check)
                            .addFormDataPart("id",encoId)
                            .addFormDataPart("pw", encoPw)
                            .addFormDataPart("nick", encoNick)
                            .addFormDataPart("email", encoEmail)
                            .addFormDataPart("file", encoProfile,RequestBody.create(MultipartBody.FORM, copyFile))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://54.180.2.34/userData/setUserData.php")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(callback);
                }

            }

        }

    }

    private void sendData(final int j, final JSONObject jsonObject) {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                    httpConn.requestWebServer(j,jsonObject, callback);

            }
        }.start();;
    }

    //http통신의 결과값 수신
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            Log.e("qweqwe", body);
            if (checksendNum == 1) {
                if (!body.equals("")) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String[] d1 = body.split("@@@@");
                                    // 메시지 큐에 저장될 메시지의 내용
                                    // 프로필 뿌려주기
                                    userPage_Nick.setText(d1[0]);
                                    userPage_Email.setText(d1[1]);
                                    Glide.with(userPage_main.this).load("http://54.180.2.34" + d1[2]).apply(new RequestOptions().circleCrop()).into(userPage_profile);
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
                                    Toast.makeText(userPage_main.this, "정보 불러오기에 실패했습니다", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).start();
                }
            } else if (checksendNum == 2) {
                if (body.equals("1")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    userPage_confirm.setVisibility(View.GONE);
                                    userPage_edit.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    }).start();

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Id",loginUserId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendData(1,jsonObject);

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(userPage_main.this, "패스워드가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).start();
                }
            } else {
                if (body.equals("0")) {

                    Intent intent = new Intent(userPage_main.this, MainActivity.class);
                    intent.putExtra("loginUserId",loginUserId);
                    setResult(loginReCode, intent);
                    finish();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(userPage_main.this, "수정에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).start();


                }
            }
        }


    };
}
