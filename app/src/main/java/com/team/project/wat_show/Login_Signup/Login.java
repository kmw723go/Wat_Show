package com.team.project.wat_show.Login_Signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.team.project.wat_show.MainActivity;
import com.team.project.wat_show.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity implements
        View.OnClickListener{

    private HttpConnection httpConn = new HttpConnection();
    EditText id,pw;
    CheckBox idsave,autosave;
    Integer loginReCode = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.Signup).setOnClickListener(this);
        findViewById(R.id.find_id_pw).setOnClickListener(this);
        autosave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idsave.isChecked() != true){
                    Toast.makeText(Login.this, "ID를 먼저 저장해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void idSave(){
        SharedPreferences sessionid = getSharedPreferences("idsave",MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionid.edit();
        editor.putString("sesstionId",id.getText().toString());
        editor.commit();
    }
    public void autologin(){
        SharedPreferences sessionid = getSharedPreferences("idsave",MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionid.edit();
        editor.putString("autologin","true");
        editor.commit();
    }

    //뷰선언
    public void findView(){
        id = (EditText)findViewById(R.id.LoginIdInput);
        pw = (EditText)findViewById(R.id.LoginPwInput);
        idsave = (CheckBox)findViewById(R.id.LoginIdSave);
        autosave = (CheckBox)findViewById(R.id.LoginAutoSave);
    }

    private void find_id_pw() {
        Intent findIntent = new Intent(Login.this,find_id_pw.class);
        startActivity(findIntent);
    }

    private void login() {
        sendData();
    }

    private void Signup() {
        Intent signInIntent = new Intent(Login.this,Signup.class);
        startActivity(signInIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login:
                login();
                break;
            case R.id.Signup:
                Signup();
                break;
            case R.id.find_id_pw:
                find_id_pw();
                break;

        }
    }

    //okhttp를 통한 서버통신
    public class HttpConnection {

        private OkHttpClient client;

        private HttpConnection(){ this.client = new OkHttpClient(); }


        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(String parameter, String parameter2, Callback callback) {
            String encoId =null,encoPw=null;
            try {
                encoId = URLEncoder.encode(parameter,"UTF-8");
                encoPw = URLEncoder.encode(parameter2,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RequestBody body = new FormBody.Builder()
                    .add("id", encoId)
                    .add("pw",encoPw)
                    .build();
            Request request = new Request.Builder()
                    .url("http://54.180.2.34/Login_Signup/Login.php")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
        }

    }

    private void sendData() {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(id.getText().toString(),pw.getText().toString(), callback);
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
            String body = response.body().string();
            Log.e("qweqwe",body);
            if(body.equals("1")){
                Intent loginInIntent = new Intent(Login.this,MainActivity.class);
                loginInIntent.putExtra("loginUserId",id.getText().toString());
                setResult(loginReCode,loginInIntent);
                if(idsave.isChecked() == true){
                    idSave();
                    if(autosave.isChecked() == true){
                        autologin();
                    }
                }

                finish();
            }else{
                new Thread(new Runnable() {
                    @Override public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(Login.this, "아이디나 패스워드가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                } });

                    }
                }).start();

            }
        }
    };
}
