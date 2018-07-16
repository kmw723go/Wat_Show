package com.team.project.wat_show.authentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.team.project.wat_show.R;

public class userAuthentication extends AppCompatActivity {

    private WebView authweb;
    private WebSettings authwebSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        authweb = (WebView)findViewById(R.id.authweb);
        authweb.setWebViewClient(new WebViewClient());
        authwebSetting = authweb.getSettings();
        authwebSetting.setJavaScriptEnabled(true);

        authweb.loadUrl("http://54.180.2.34/Authentication/userAuthentication.php");
    }
}
