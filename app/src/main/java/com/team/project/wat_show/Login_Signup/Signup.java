package com.team.project.wat_show.Login_Signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team.project.wat_show.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    //Check는 유효성검사 결과 텍스트뷰, Input 입력창
    TextView SignupIdCheck,SignupPWCheck,SignupConfCheck;
    EditText SignupIdInput,SignupPWInput,SignupConfInput;
    private HttpConnection httpConn = new HttpConnection();
    Boolean idcheck = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findView();
        //버튼 클릭시 onClick 메소드 호출, SignupIdConf는 아이디 중복 확인, SignupNextBtn은 추가입력을 위한 다음페이지로 이동
        findViewById(R.id.SignupIdConf).setOnClickListener(this);
        findViewById(R.id.SignupNextBtn).setOnClickListener(this);
        IdCheck();
        PWCheck();
        ConfCheck();
    }

    //아이디 유효성 검사
    public void IdCheck(){

        SignupIdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = SignupIdInput.getText().toString();
                if(!Pattern.matches("^[a-zA-Z0-9]{5,15}+$", value)){
                    SignupIdCheck.setText("형식에 맞춰 작성하세요");
                    SignupIdCheck.setTextColor(RED);
                    SignupIdCheck.setVisibility(View.VISIBLE);
                    SignupIdInput.requestFocus();
                }else{
                    SignupIdCheck.setText("올바른 형식입니다");
                    SignupIdCheck.setTextColor(GREEN);
                    SignupIdCheck.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //비밀번호 유효성 검사
    public void PWCheck(){

        SignupPWInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = SignupPWInput.getText().toString();
                if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", value)){
                    SignupPWCheck.setText("형식에 맞춰 작성하세요");
                    SignupPWCheck.setTextColor(RED);
                    SignupPWCheck.setVisibility(View.VISIBLE);
                    SignupPWInput.requestFocus();
                }else{
                    SignupPWCheck.setText("올바른 형식입니다");
                    SignupPWCheck.setTextColor(GREEN);
                    SignupPWCheck.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //비밀번호 확인 유효성 검사
    public void ConfCheck(){

        SignupConfInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = SignupConfInput.getText().toString();
                if(!SignupConfInput.getText().toString().equals(SignupPWInput.getText().toString())){
                    SignupConfCheck.setText("비밀번호가 일치하지 않습니다");
                    SignupConfCheck.setTextColor(RED);
                    SignupConfCheck.setVisibility(View.VISIBLE);
                    SignupConfInput.requestFocus();
                }else{
                    SignupConfCheck.setText("비밀번호가 일치합니다");
                    SignupConfCheck.setTextColor(GREEN);
                    SignupConfCheck.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //뷰 선언
    public void findView(){
        SignupIdCheck = (TextView)findViewById(R.id.SignupIdCheck);
        SignupIdInput = (EditText)findViewById(R.id.SignupIdInput);
        SignupPWCheck = (TextView)findViewById(R.id.SignupPWCheck);
        SignupPWInput = (EditText)findViewById(R.id.SignupPWInput);
        SignupConfCheck = (TextView)findViewById(R.id.SignupConfCheck);
        SignupConfInput = (EditText)findViewById(R.id.SignupConfInput);
    }

    //내부 클래스인 HttpConnection로 데이터값 전달
    private void sendData() {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(SignupIdInput.getText().toString(), callback);
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
            if(body.equals("1")){
                new Thread(new Runnable() {
                    @Override public void run() {
                        // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 메시지 큐에 저장될 메시지의 내용
                                Toast.makeText(Signup.this, "이미 사용중인 아이디입니다", Toast.LENGTH_SHORT).show();
                            } });

                    }
                }).start();
            }else{
                new Thread(new Runnable() {
                    @Override public void run() {
                        // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 메시지 큐에 저장될 메시지의 내용
                                Toast.makeText(Signup.this, "사용가능한 아이디입니다", Toast.LENGTH_SHORT).show();
                                idcheck = true;
                            } });

                    }
                }).start();
            }
        }
    };

    //각항목의 유효성 검사 확인 후 추가입력을 위한 다음페이지로 이동
    public void Nextpage(){
        String id = SignupIdInput.getText().toString(),pw = SignupPWInput.getText().toString(),conf = SignupConfInput.getText().toString();
        Boolean check = true;
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "빈칸 없이 작성하세요", Toast.LENGTH_SHORT).show();
            SignupIdInput.requestFocus();
            check = false;
        }
        if( TextUtils.isEmpty(pw)){
            Toast.makeText(this, "빈칸 없이 작성하세요", Toast.LENGTH_SHORT).show();
            SignupPWInput.requestFocus();
            check = false;
        }
        if( TextUtils.isEmpty(conf)){
            Toast.makeText(this, "빈칸 없이 작성하세요", Toast.LENGTH_SHORT).show();
            SignupConfInput.requestFocus();
            check = false;
        }
        if(!Pattern.matches("^[a-zA-Z0-9]{5,15}+$", id)){
            SignupIdCheck.setText("형식에 맞춰 작성하세요");
            SignupIdCheck.setTextColor(RED);
            SignupIdCheck.setVisibility(View.VISIBLE);
            SignupIdInput.requestFocus();
            check = false;
        }
        if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", pw)){
            SignupPWCheck.setText("형식에 맞춰 작성하세요");
            SignupPWCheck.setTextColor(RED);
            SignupPWCheck.setVisibility(View.VISIBLE);
            SignupPWInput.requestFocus();
            check = false;
        }
        if(!SignupConfInput.getText().toString().equals(SignupPWInput.getText().toString())){
            SignupConfCheck.setText("비밀번호가 일치하지 않습니다");
            SignupConfCheck.setTextColor(RED);
            SignupConfCheck.setVisibility(View.VISIBLE);
            SignupConfInput.requestFocus();
            check = false;
        }
        if(idcheck == false){
            Toast.makeText(this, "아이디 중복체크를 확인하세요", Toast.LENGTH_SHORT).show();
            check = false;
        }
        if(check == true){
            Intent intent = new Intent(Signup.this,Signup2.class);
            intent.putExtra("id",SignupIdInput.getText().toString());
            intent.putExtra("pw",SignupPWInput.getText().toString());
            startActivity(intent);
            finish();
        }

    }

    //버튼 클릭시 이벤트 처리
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SignupIdConf:
                sendData();
                break;
            case R.id.SignupNextBtn:
                Nextpage();
                break;

        }
    }

    //okhttp를 통한 서버통신
    public class HttpConnection {

        private OkHttpClient client;

        private HttpConnection(){ this.client = new OkHttpClient(); }


        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(String parameter, Callback callback) {
            String encoId =null;
            try {
                encoId = URLEncoder.encode(parameter,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RequestBody body = new FormBody.Builder()
                    .add("check","id")
                    .add("id", encoId)
                    .build();
            Request request = new Request.Builder()
                    .url("http://54.180.2.34/Login_Signup/Id_Nick_Check.php")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
        }

    }

}
