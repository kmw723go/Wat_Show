package com.team.project.wat_show;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout main_drawer;
    public ActionBarDrawerToggle main_Toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 메인 드로워레이아웃 설정
        settingDrawer();

    }

    //------------------------------ 드로워레이아웃과 네비게이션 메뉴 클릭 --------------------
    public void settingDrawer(){
        main_drawer = (DrawerLayout)findViewById(R.id.main_drawer);
        main_Toggle = new ActionBarDrawerToggle(MainActivity.this,main_drawer,R.string.open,R.string.close);


        main_drawer.addDrawerListener(main_Toggle);
        main_Toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바 활성화


        NavigationView mNavigationView = (NavigationView) findViewById(R.id.main_navi_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }


    // ----------------------------- 네이게이션 메뉴가 드로워 활성화----------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(main_Toggle.onOptionsItemSelected(item)){
            return true;
        }


        // ...  에있는 항목들.
        switch (item.getItemId()) {
            case R.id.home:
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.setting:
                Toast.makeText(this, "환경설정", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_setting_menu, menu);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "카메라", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "갤러리", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "슬라이드쇼", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(MainActivity.this, "툴", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "공유", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(MainActivity.this, "보내기", Toast.LENGTH_SHORT).show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        drawer.closeDrawer(GravityCompat.START);

        return false;

    }

}
