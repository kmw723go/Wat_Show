package com.team.project.wat_show.main_activity;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.project.wat_show.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Menu_videoContent extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";


    // 리사이클러뷰
    RecyclerView videoContent_RecyclerView;
    View view;
    Context context;


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
           // 리사이클러뷰 세팅
           setLive_list_RecyclerView();
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
    }

}
