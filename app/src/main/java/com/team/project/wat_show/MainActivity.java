package com.team.project.wat_show;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.team.project.wat_show.Login_Signup.Login;
import com.team.project.wat_show.Login_Signup.Signup;
import com.team.project.wat_show.appSetting.appSetting_main;
import com.team.project.wat_show.broadCast.broadCast_main;
import com.team.project.wat_show.chargeUp_exchange.chargeUp_exchange_main;
import com.team.project.wat_show.main_activity.main_viewPager_Adapter;
import com.team.project.wat_show.serviceCenter.serviceCenter_main;
import com.team.project.wat_show.upload_Videos.upload_videos_main;
import com.team.project.wat_show.userPage.userPage_main;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Integer loginReCode = 1111;


    //ip 주소
    ip ip = new ip();
    String ipad = ip.getIp();


    public DrawerLayout main_drawer;
    public ActionBarDrawerToggle main_Toggle;
    public TabLayout tabLayout;

    // 사용자 정보
    String loginUserId = "";
    String loginUserNick;
    String loginUserCash;
    String loginUserPrfile;

    // 로그인 상태
    Boolean loginOn = false;


    // 네비게이션 메뉴가 열려있는지 확인하고, ( 백버튼시 숨겨주기 위함 )
    Boolean navi_open = false;

    // 네비게이션 뷰 항목
    View navi_View;
    NavigationView mNavigationView;

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

        getUserData();

    }

    public void getUserData() {

        try {
            loginUserId = getIntent().getStringExtra("loginUserId");
            if (!loginUserId.equals("") || loginUserId != null) {

                loginOn = true;
                getUserDataOnHttp(loginUserId);
            }else{
                loginOn = false;
            }
        } catch (Exception e) {

        }


    }

    // 로그인 체크 (레이아웃을 숨기거나 보여줌 )
    public void loginCheck() {
        if (loginOn == false) { //loginUserId.equals("") || loginUserId == null || TextUtils.isEmpty(loginUserId)

            navi_View = mNavigationView.getHeaderView(0);

            // 사용자 정보 숨기기
            LinearLayout main_draw_profile_Layout = (LinearLayout) navi_View.findViewById(R.id.main_draw_profile_Layout);
            main_draw_profile_Layout.setVisibility(View.GONE);


            // 로그인 회원가입창 띄우기
            LinearLayout main_draw_Login_Layout = (LinearLayout) navi_View.findViewById(R.id.main_draw_Login_Layout);
            main_draw_Login_Layout.setVisibility(View.VISIBLE);

            // 소지금 0원으로 만들기
            TextView nav_userCash_text = (TextView) navi_View.findViewById(R.id.nav_userCash_text);
            nav_userCash_text.setText("0 원");


        } else {

            // 사용자 정보 띄우기
            LinearLayout main_draw_profile_Layout = (LinearLayout) navi_View.findViewById(R.id.main_draw_profile_Layout);
            main_draw_profile_Layout.setVisibility(View.VISIBLE);


            // 로그인 회원가입창 가리기
            LinearLayout main_draw_Login_Layout = (LinearLayout) navi_View.findViewById(R.id.main_draw_Login_Layout);
            main_draw_Login_Layout.setVisibility(View.GONE);
        }
    }


    // --------------------- 검색 ( 서치뷰 ) onCreateOptionsMenu 내부에서 호출 ---------------------
    public void setSearchEvent() {

        // 서치뷰 클릭시 자동 포커스 주기
        main_searchView.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override // 서치뷰가 확장되었을 시
            public boolean onMenuItemActionExpand(MenuItem item) {
                SearchView sv = (SearchView) main_searchView.getActionView();
                sv.setFocusable(true);
                sv.setIconified(false);
                sv.requestFocusFromTouch();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            @Override // 서치뷰가 없어졌을시.
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });


        // 검색어 및 검색어 입력 관련 이벤트
        SearchView sv = (SearchView) main_searchView.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override // 검색 버튼을 눌렀을 시 이벤트
            public boolean onQueryTextSubmit(String query) {

                //Toast.makeText(MainActivity.this, "검색결과 : "+query, Toast.LENGTH_SHORT).show();
                // 서치뷰 닫기
                main_searchView.collapseActionView();
                View searchView = getCurrentFocus();

                if (searchView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
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
                //Toast.makeText(MainActivity.this, "닫기", Toast.LENGTH_SHORT).show();

                // 서치뷰 닫기
                main_searchView.collapseActionView();

                // 키보드 내리기
                View searchView = getCurrentFocus();
                if (searchView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
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

                if (a == 0) { // 생방송
                    tabLayout.getTabAt(0).setIcon(R.drawable.live_selected);
                    tabLayout.getTabAt(1).setIcon(R.drawable.video_content);
                    tabLayout.getTabAt(2).setIcon(R.drawable.my_list);
                } else if (a == 1) { // 동영상 목록
                    tabLayout.getTabAt(1).setIcon(R.drawable.video_content_selected);
                    tabLayout.getTabAt(0).setIcon(R.drawable.live);
                    tabLayout.getTabAt(2).setIcon(R.drawable.my_list);
                } else if (a == 2) { // 즐겨찾기 목록
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
        mNavigationView = (NavigationView) findViewById(R.id.main_navi_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }


        // 헤더 뷰에 있는 위젯 값 설정하기
        navi_View = mNavigationView.getHeaderView(0);
        TextView main_navi_userNick = (TextView) navi_View.findViewById(R.id.main_navi_userNick);
        main_navi_userNick.setText("홍길동이");


        // 유저페이지 이동 인텐트
        gotoUserPage();

        // 충전 환전 이동
        gotoChargeUpPage();

        // 방송하기 페이지로 이동
        gotoBroadCast();

        // 로그인 이동
        gotoLoginPage();

        // 회원가입으로 이동
        gotoJoinPage();

        // 동영상 업로드로 이동
        gotoUploadVideos();


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
                Intent gotoAppSetting = new Intent(MainActivity.this, appSetting_main.class);
                startActivity(gotoAppSetting);
                return true;

            case R.id.serviceCenter:
                Intent gotoServiceCenter = new Intent(MainActivity.this, serviceCenter_main.class);
                startActivity(gotoServiceCenter);
                return true;

            case R.id.logout:
                if (loginOn == false) {
                    Toast.makeText(this, "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                    loginOn = false;
                    loginUserId ="";
                    loginCheck();
                }
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

        // 로그인 상태 받아오기
        loginCheck();


        return true;
    }

    // ------------------------------네비게이션 메뉴  클릭 이벤트 -----------------------
    //사용자 정보창
    public void gotoUserPage() {
        navi_View = mNavigationView.getHeaderView(0);

        LinearLayout main_draw_profile_Layout = (LinearLayout) navi_View.findViewById(R.id.main_draw_profile_Layout);
        main_draw_profile_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoUserPage = new Intent(MainActivity.this, userPage_main.class);
                startActivity(gotoUserPage);
            }
        });
    }

    // 충전 및 환전 하기
    public void gotoChargeUpPage() {
        //충전 환전  클릭 이벤트


        navi_View = mNavigationView.getHeaderView(0);
        TextView nav_cash_ChargeUp = (TextView) navi_View.findViewById(R.id.nav_cash_ChargeUp);
        nav_cash_ChargeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loginOn == true) {
                    Intent gotoExchange = new Intent(MainActivity.this, chargeUp_exchange_main.class);
                    startActivity(gotoExchange);
                } else {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
/*
                // 아이템 클릭시  네비게이션 드로워 닫기
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
                drawer.closeDrawer(GravityCompat.START);*/
            }
        });


    }

    // 방송하기
    public void gotoBroadCast() {

        navi_View = mNavigationView.getHeaderView(0);
        LinearLayout nav_rec = (LinearLayout) navi_View.findViewById(R.id.nav_rec);
        nav_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loginOn == true) {
                    Intent gotoBroadCast = new Intent(MainActivity.this, broadCast_main.class);
                    startActivity(gotoBroadCast);
                } else {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

              /*  // 아이템 클릭시  네비게이션 드로워 닫기
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
                drawer.closeDrawer(GravityCompat.START);*/
            }
        });
    }

    // 동영상 업로드 이동하기
    public void gotoUploadVideos(){
        navi_View = mNavigationView.getHeaderView(0);
        LinearLayout nav_rec = (LinearLayout) navi_View.findViewById(R.id.nav_upload_Video);
        nav_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loginOn == true) {
                    Intent gotoBroadCast = new Intent(MainActivity.this, upload_videos_main.class);
                    gotoBroadCast.putExtra("loginUserId",loginUserId);
                    gotoBroadCast.putExtra("loginUserNick",loginUserNick);
                    startActivity(gotoBroadCast);
                } else {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

              /*  // 아이템 클릭시  네비게이션 드로워 닫기
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
                drawer.closeDrawer(GravityCompat.START);*/
            }
        });
    }

    // 로그인으로 이동하기
    public void gotoLoginPage() {
        navi_View = mNavigationView.getHeaderView(0);
        TextView goLoginBtn = (TextView) navi_View.findViewById(R.id.goLoginBtn);
        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoLogin = new Intent(MainActivity.this, Login.class);
                startActivityForResult(gotoLogin, loginReCode);
            }
        });
    }

    // 회원가입으로 이동하기
    public void gotoJoinPage() {
        navi_View = mNavigationView.getHeaderView(0);
        TextView goSignupBtn = (TextView) navi_View.findViewById(R.id.goSignupBtn);
        goSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoJoinPage = new Intent(MainActivity.this, Signup.class);
                startActivity(gotoJoinPage);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        //네비게이션 뷰가 열려 있다면. true를 반환하여 네비게이션 뷰만 닫아준다.
        if (navi_open == true) {
            // 아이템 클릭시  네비게이션 드로워 닫기
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);
            drawer.closeDrawer(GravityCompat.START);
            navi_open = false;
        } else {
            super.onBackPressed();
        }


    }


    // 인텐트 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 로그인
        if (requestCode == loginReCode) {

            if (resultCode == loginReCode) {
                loginUserId = data.getStringExtra("loginUserId");
                // 사용자 정보 가지고 오기
                try {
                    loginOn = true;
                    // 로그인 체크 엑티비티 변경
                    loginCheck();
                    getUserDataOnHttp(loginUserId);
                } catch (Exception e) {
                    Toast.makeText(this, "사용자의 정보를 가지고 오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        } else {
            Toast.makeText(this, "로그인에 실패 했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // --------------------------- 서버 연결 -----------------------------------------------------

    //사용자 정보 받아오기
    public void getUserDataOnHttp(String loginUserId) {
        class getDataFromHttp extends AsyncTask<Void, Void, String> {

            OkHttpClient client = new OkHttpClient();

            String loginUserId;

            public getDataFromHttp(String loginUserId) {
                this.loginUserId = loginUserId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String serverUrl = "http://54.180.2.34/userData/getUserData.php";
                String result = "";
                try {


                    // 보낼 데이터 담기
                    RequestBody sendDatas = new FormBody.Builder()
                            .add("loginUserId", loginUserId)
                            .build();

                    // 요청하면서  데이터 보내기
                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(sendDatas)
                            .build();

                    // 응답  (response.body().string() 는  1회만 사용이 가능하다 )
                    Response response = client.newCall(request).execute();

                    // 가지고 온 데이터
                    result = response.body().string();


                } catch (IOException e) {
                    e.printStackTrace();
                }


                return result;
            }

            @Override
            protected void onPostExecute(String a) {
                super.onPostExecute(a);

                loginOn = true;
                Log.d("회원 정보 가지고옴 ", "" + a);
                devideData(a);

            }
        }  // 클래스 끝

        // Url. 연결
        new getDataFromHttp(loginUserId).execute();

    }

    // 사용자 정보 프로필에 뿌려주기
    public void setUserProfile(String loginUserNick, String loginUserPrfile, String loginUserCash) {
        navi_View = mNavigationView.getHeaderView(0);

        // 닉설정
        TextView main_navi_userNick = (TextView) navi_View.findViewById(R.id.main_navi_userNick);
        main_navi_userNick.setText(loginUserNick);

        // 캐쉬설정
        TextView nav_userCash_text = (TextView) navi_View.findViewById(R.id.nav_userCash_text);
        nav_userCash_text.setText(loginUserCash + "원");

        // 프로필 설정
        ImageView main_navi_userProfile = (ImageView) navi_View.findViewById(R.id.main_navi_userProfile);
        Glide.with(this).load(ipad +loginUserPrfile).into(main_navi_userProfile);

    }

    // 들어온 데이터 쪼개기
    public void devideData(String result) {
        // ( 유저 닉네임 ,  이메일 , 프로필,  캐쉬를 받는다.)
        String[] d1 = result.split("@@@@");

        // 프로필 뿌려주기
        setUserProfile(d1[0], d1[2], d1[3]);
       /* Log.d("데이터 길이 ",""+d1.length);
        Log.d("유저아이디",""+loginUserId);
        Log.d("유저닉네임",""+d1[0]);
        Log.d("유저이메일",""+d1[1]);
        Log.d("유저프로필",""+d1[2]);
        Log.d("유저캐쉬",""+d1[3]);*/

    }


    //  원래는 메뉴를 사용해서 아이템값에따라 이벤트를 지정하나 ,  이미지가 흑백으로만 나오는 문제로 pass
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

}
