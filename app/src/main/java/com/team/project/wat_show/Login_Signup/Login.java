package com.team.project.wat_show.Login_Signup;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private HttpConnection httpConn = new HttpConnection();
    EditText id,pw;
    CheckBox idsave,autosave;
    Integer loginReCode = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.Signup).setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null);
        }
    }
    private void updateUI(@Nullable GoogleSignInAccount account) {

    }

    //뷰선언
    public void findView(){
        id = (EditText)findViewById(R.id.LoginIdInput);
        pw = (EditText)findViewById(R.id.LoginPwInput);
        idsave = (CheckBox)findViewById(R.id.LoginIdSave);
        autosave = (CheckBox)findViewById(R.id.LoginAutoSave);
    }

    // [END onActivityResult]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.login:
                login();
                break;
            case R.id.Signup:
                Signup();
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
                encoId = URLEncoder.encode(parameter,"EUC-KR");
                encoPw = URLEncoder.encode(parameter2,"EUC-KR");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RequestBody body = new FormBody.Builder()
                    .add("id", parameter)
                    .add("pw",parameter2)
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
