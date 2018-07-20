package com.team.project.wat_show.chargeUp_exchange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.team.project.wat_show.Login_Signup.Login;
import com.team.project.wat_show.Login_Signup.Signup2;
import com.team.project.wat_show.MainActivity;
import com.team.project.wat_show.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chargeUp_exchange_main extends AppCompatActivity {
    private WebView authweb;
    private WebSettings authwebSetting;
    private final Handler handler = new Handler();
    private HttpConnection httpConn = new HttpConnection();
    String loginUserId="";
    TextView moneyinput;
    RadioGroup moneyselcet;
    LinearLayout selcetlayout;
    Button submit;
    int money = 0;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_up_exchange_main);
        Intent intent = getIntent();
        loginUserId = intent.getStringExtra("loginUserId");
        findview();
        moneyselect();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(loginUserId, String.valueOf(money));
                selcetlayout.setVisibility(View.GONE);

                authweb = (WebView)findViewById(R.id.charge_exchange);
                authweb.setWebViewClient(new WebViewClient());
                authwebSetting = authweb.getSettings();
                authwebSetting.setJavaScriptEnabled(true);
                authweb.addJavascriptInterface(new AndroidBridge(chargeUp_exchange_main.this),"WatShow");
                authweb.loadUrl("http://54.180.2.34/Charge_Exchage/charge.php");
                authweb.setVisibility(View.VISIBLE);
            }
        });


        //리펙토링때 수정할 부분
       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            authweb.evaluateJavascript("javascript:setMessage('" + loginUserId + "')",null);
        } else {
            authweb.loadUrl("javascript:setMessage('" + loginUserId + "')");
        }*/
    }

    private void findview(){
        moneyinput = (TextView)findViewById(R.id.moneyinput);
        moneyselcet = (RadioGroup)findViewById(R.id.moneyselect);
        submit = (Button)findViewById(R.id.chargesubmit);
        selcetlayout = (LinearLayout)findViewById(R.id.selcetlayout);
    }
    private void moneyselect(){
        moneyselcet.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.fivemoney){
                    moneyinput.setText("5,000원");
                    money = 5000;
                }else if(checkedId == R.id.tenmoney){
                    moneyinput.setText("10,000원");
                    money = 10000;
                }else if(checkedId == R.id.thirtymoney){
                    moneyinput.setText("30,000원");
                    money = 30000;
                }else if(checkedId == R.id.fiftymoney){
                    moneyinput.setText("50,000원");
                    money = 50000;
                }else if(checkedId == R.id.hundredmoney){
                    moneyinput.setText("100,000원");
                    money = 100000;
                }

            }
        });
    }

    private class AndroidBridge {
        Context mContext;
        /** Instantiate the interface and set the context */
        AndroidBridge(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void setMessage(final String arg) {

            handler.post(new Runnable() {

                public void run() {
                    Log.e("qzxczxczxc",arg);
                    new AlertDialog.Builder(chargeUp_exchange_main.this)
                            .setTitle("알림")
                            .setMessage(arg)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();

                }

            });

        }
    }

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
                    .add("money",encoPw)
                    .build();
            Log.e("qweqweqwrfa",encoId+"/////"+encoPw);

            Request request = new Request.Builder()
                    .url("http://54.180.2.34/Charge_Exchage/charge.php")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
        }

    }

    private void sendData(final String id, final String money) {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(id,money, callback);
            }
        }.start();
    }

    //http통신의 결과값 수신
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.d("test",body);


        }
    };
}
