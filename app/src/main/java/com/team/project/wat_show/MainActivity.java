package com.team.project.wat_show;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.team.project.wat_show.main_activity.main_viewPager_Adapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout main_drawer;
    public ActionBarDrawerToggle main_Toggle;
    public TabLayout tabLayout;

    // 네비게이션 뷰 항목
    View navi_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // 메인 드로워레이아웃 설정
        settingDrawer();


        // 뷰페이저 설정
        setViewPager();



    }


    //------------------------------ 뷰페이저 설정 ( 연동 및 아이콘 ) ----------------------------------------
    public void setViewPager() {
        // 뷰페이저 어댑터 연동
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        main_viewPager_Adapter adapter = new main_viewPager_Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // 탭 레이아웃과 뷰페이저의 연동  ( 제목 지정은 helpListViewPagerAdapter 에서 한다 )
        tabLayout = (TabLayout) findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(viewPager);

        // 초기 아이콘 설정
        tabLayout.getTabAt(0).setIcon(R.drawable.live_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.video_content);
        tabLayout.getTabAt(2).setIcon(R.drawable.my_list);

        // 메인 목록 선택시 반응
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int a = tab.getPosition();

                if(a == 0){ // 생방송
                    tabLayout.getTabAt(0).setIcon(R.drawable.live_selected);
                    tabLayout.getTabAt(1).setIcon(R.drawable.video_content);
                    tabLayout.getTabAt(2).setIcon(R.drawable.my_list);
                }else if (a == 1){ // 동영상 목록
                    tabLayout.getTabAt(1).setIcon(R.drawable.video_content_selected);
                    tabLayout.getTabAt(0).setIcon(R.drawable.live);
                    tabLayout.getTabAt(2).setIcon(R.drawable.my_list);
                }else if (a == 2){ // 즐겨찾기 목록
                    tabLayout.getTabAt(2).setIcon(R.drawable.my_list_selected);
                    tabLayout.getTabAt(0).setIcon(R.drawable.live);
                    tabLayout.getTabAt(1).setIcon(R.drawable.video_content);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int a = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    //------------------------------ 드로워레이아웃과 네비게이션 메뉴 클릭 --------------------
    public void settingDrawer() {
        main_drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        main_Toggle = new ActionBarDrawerToggle(MainActivity.this, main_drawer, R.string.open, R.string.close);


        main_drawer.addDrawerListener(main_Toggle);
        main_Toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바 활성화
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.main_navi_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }


        // 헤더 뷰에 있는 위젯 값 설정하기
        navi_View = mNavigationView.getHeaderView(0);
        TextView main_navi_userNick = (TextView)navi_View.findViewById(R.id.main_navi_userNick);
        main_navi_userNick.setText("홍길동이");


    }


    // ----------------------------- 네이게이션 메뉴가 드로워 활성화----------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (main_Toggle.onOptionsItemSelected(item)) {
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
