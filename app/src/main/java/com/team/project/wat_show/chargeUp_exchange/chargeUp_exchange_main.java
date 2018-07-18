package com.team.project.wat_show.chargeUp_exchange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.team.project.wat_show.R;

public class chargeUp_exchange_main extends AppCompatActivity {
    private WebView authweb;
    private WebSettings authwebSetting;
    private final Handler handler = new Handler();
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_up_exchange_main);
        authweb = (WebView)findViewById(R.id.charge_exchange);
        authweb.setWebViewClient(new WebViewClient());
        authwebSetting = authweb.getSettings();
        authwebSetting.setJavaScriptEnabled(true);
        authweb.addJavascriptInterface(new AndroidBridge(this),"WatShow");
        authweb.loadUrl("http://54.180.2.34/Charge_Exchage/charge.php");
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
}
