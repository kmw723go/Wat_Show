package com.team.project.wat_show;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.team.project.wat_show.main_activity.main_viewPager_Adapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout main_drawer;
    public ActionBarDrawerToggle main_Toggle;
    public TabLayout tabLayout;

    Boolean navi_open = false;

    // 네비게이션 뷰 항목
    View navi_View;

    // 서치뷰
    MenuItem main_searchView;

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


    // --------------------- 검색 ( 서치뷰 ) onCreateOptionsMenu 내부에서 호출 ---------------------
    public void setSearchEvent(){

        // 서치뷰 클릭시 자동 포커스 주기
        main_searchView.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override // 서치뷰가 확장되었을 시
            public boolean onMenuItemActionExpand(MenuItem item) {
                SearchView sv = (SearchView)main_searchView.getActionView();
                sv.setFocusable(true);
                sv.setIconified(false);
                sv.requestFocusFromTouch();
                return true;
            }

            @Override // 서치뷰가 없어졌을시.
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });



        // 검색어 및 검색어 입력 관련 이벤트
        SearchView sv = (SearchView)main_searchView.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override // 검색 버튼을 눌렀을 시 이벤트
            public boolean onQueryTextSubmit(String query) {

                //Toast.makeText(MainActivity.this, "검색결과 : "+query, Toast.LENGTH_SHORT).show();
                // 서치뷰 닫기
                main_searchView.collapseActionView();
                return true;
            }

            @Override // 글자가 쓰면서 발생하는 이벤트
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(MainActivity.this, "검색중 : "+newText , Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        // 서치뷰  닫기 버튼 X 모양 버튼 클릭시 이벤트
        sv.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MainActivity.this, "닫기", Toast.LENGTH_SHORT).show();

                // 서치뷰 닫기
                main_searchView.collapseActionView();

                // 키보드 내리기
                View searchView = getCurrentFocus();
                if(searchView != null){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(),0);
                }
                return true;
            }
        });



    }

    //------------------------------ 뷰페이저 설정 ( 연동 및 아이콘 ) ---------------------------
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

        // 메인 목록 선택시 아이콘  반응
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



    // ----------------------------------옵션 아이템 클릭 리스너 -------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (main_Toggle.onOptionsItemSelected(item)) {
            navi_open = true;
            return true;
        }


        // ...  에있는 항목들.
        switch (item.getItemId()) {
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
    
    // ----------------------------- 네이게이션 메뉴가 드로워 활성화----------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 우측 상단 설정메뉴
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_setting_menu, menu);


        // 우측 상단 서치뷰
        inflater.inflate(R.menu.main_search_view, menu);
        main_searchView = menu.findItem(R.id.search);

        // 서치뷰 검색 이벤트
        setSearchEvent();


        return true;
    }

    //------------------------------- 왼쪽 상단 메뉴바 클릭 이벤트 --------------------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_userCash) {
            Toast.makeText(MainActivity.this, "소지금", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_cash_ChargeUp) {
            Toast.makeText(MainActivity.this, "충전 환전", Toast.LENGTH_SHORT).show();
        }else if( id == R.id.nav_rec){
            Toast.makeText(MainActivity.this, "방송하기", Toast.LENGTH_SHORT).show();
        }

        // 아이템 클릭시  네비게이션 드로워 닫기
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return false;

    }


    @Override
    public void onBackPressed() {

        //네비게이션 뷰가 열려 있다면. true를 반환하여 네비게이션 뷰만 닫아준다.
        if(navi_open == true){
            // 아이템 클릭시  네비게이션 드로워 닫기
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
            drawer.closeDrawer(GravityCompat.START);
            navi_open = false;
        }else{
            super.onBackPressed();
        }



    }
}
