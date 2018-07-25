package com.team.project.wat_show.main_activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.team.project.wat_show.MainActivity;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;
import com.team.project.wat_show.upload_Videos.video_content;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Menu_videoContent extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";


    // 리사이클러뷰
    RecyclerView videoContent_RecyclerView;
    View view;
    Context context;

    // ip
    ip ip = new ip();
    String ipad = ip.getIp();

    // 유저정보
    String loginUserId;
    String loginUserNick;


    //데이터
    // 리사이클러뷰
    RecyclerView my_upload_videoList;
    ArrayList<video_content> video_datas = new ArrayList<>();
    video_content content;

    videoContent_adepter content_adepter;

    String [] d1;
    String [] d2;




    public Menu_videoContent() {
        // Required empty public constructor
    }

    // 프레그먼트 설정.
    public static Menu_videoContent newInstance(int columnCount) {
        Menu_videoContent fragment = new Menu_videoContent();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 인플레이트
       view = inflater.inflate(R.layout.fragment_menu_video_content, container, false);

       try{
           getVideoContentDataHttp();
       }catch (NullPointerException e){

       }


        return view;
    }


    //--------------------------------- 리사이클러뷰 세팅 -----------------------------------------
    public void setLive_list_RecyclerView(){

        // 리사이클러 뷰 불러오기
        videoContent_RecyclerView =(RecyclerView)view.findViewById(R.id.video_content_RecyclerView);

        // 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        videoContent_RecyclerView.setLayoutManager(layoutManager);

        content_adepter = new videoContent_adepter(getContext(),video_datas,((MainActivity)getActivity()).getUserIdFromMain());
        my_upload_videoList.setAdapter(content_adepter);

    }

    // ( 서버 연결 )데이터 불러오기
    public void getVideoContentDataHttp(){
        class getDataFromHttp extends AsyncTask<Void, Void, String> {

            OkHttpClient client = new OkHttpClient();

            String loginUserId;
            ProgressDialog dialog = new ProgressDialog(getContext());

            public getDataFromHttp(String loginUserId) {
                this.loginUserId = loginUserId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("서버에 전송중..");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String serverUrl = ipad+"/upload_Videos/main_get_all_data.php";
                String result = "";
                try {
                    // 보낼 데이터 담기
                    RequestBody sendDatas = new FormBody.Builder()
                            .add("getData", "00")
                            .build();

                    // 요청하면서  데이터 보내기
                    Request request = new Request.Builder()
                            .url(serverUrl)
                            .post(sendDatas)
                            .build();

                    // 응답  (response.body().string() 는  1회만 사용이 가능하다 )
                    Response response = client.newCall(request).execute();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("실패", "failed", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            Log.d("프레그먼트 비디오 결과", "" + result);
                            divideData(result);
                        }
                    });

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
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }
            }
        }  // 클래스 끝

        // Url. 연결
        new getDataFromHttp(loginUserId).execute();
    }

    // 데이터 쪼개기
    public void divideData(String result){

        if(result.equals("없음")){
        }else{
            d1 = result.split("%%%");
            for( int i =0 ; i < d1.length; i++){
                d2 = d1[i].split("@@@");
                content = new video_content(d2[0],d2[1],d2[2],d2[3],d2[4],d2[5],d2[6],d2[7],d2[8],d2[9],
                        Integer.parseInt(d2[10]),Integer.parseInt(d2[11]),Integer.parseInt(d2[12]),Integer.parseInt(d2[13]));
                video_datas.add(content);
            }

            // 리사이클러 뷰 세팅
            try {
                setLive_list_RecyclerView();
            } catch (Exception e) {

            }

        }

    }


}
