package com.team.project.wat_show.chargeUp_exchange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

                server_sender server_sender = new server_sender();
                server_sender.execute();
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

    public class server_sender extends AsyncTask<Void, Integer, Void> {
        String data = "";
        String param = "id="+loginUserId+"&money="+money;
        @Override
        protected Void doInBackground(Void... unused) {
            /* 인풋 파라메터값 생성 */

            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://54.180.2.34/Charge_Exchage/valuesender.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;


                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("qweqwda",data);

            Intent intent = new Intent(chargeUp_exchange_main.this,chargeUp_exchange_main2.class);
            startActivity(intent);
    }


    }
}
