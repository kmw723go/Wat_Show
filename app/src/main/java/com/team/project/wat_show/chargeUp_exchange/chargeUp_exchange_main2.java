package com.team.project.wat_show.chargeUp_exchange;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.team.project.wat_show.R;

public class chargeUp_exchange_main2 extends AppCompatActivity {
    private WebView authweb;
    private WebSettings authwebSetting;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_up_exchange_main2);

        CheckTypesTask CheckTypesTask = new CheckTypesTask();
        CheckTypesTask.execute();


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
                    new AlertDialog.Builder(chargeUp_exchange_main2.this)
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

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                chargeUp_exchange_main2.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
            authweb = (WebView)findViewById(R.id.charge_exchange);
            authweb.setWebViewClient(new WebViewClient());
            authwebSetting = authweb.getSettings();
            authwebSetting.setJavaScriptEnabled(true);
            authweb.addJavascriptInterface(new chargeUp_exchange_main2.AndroidBridge(chargeUp_exchange_main2.this),"WatShow");
            authweb.loadUrl("http://54.180.2.34/Charge_Exchage/charge.php");
            authweb.setVisibility(View.VISIBLE);
        }
    }

}
