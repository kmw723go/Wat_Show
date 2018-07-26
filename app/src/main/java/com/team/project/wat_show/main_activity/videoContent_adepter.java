package com.team.project.wat_show.main_activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;
import com.team.project.wat_show.upload_Videos.video_content;

import java.util.ArrayList;

public class videoContent_adepter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context context;
    ArrayList<video_content> video_datas = new ArrayList<>();

    String loginUserId;

    ip ip = new ip();
    String ipad =ip.getIp();

    public videoContent_adepter(Context context, ArrayList<video_content> video_datas) {
        this.context = context;
        this.video_datas = video_datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_video_content_item, parent, false);
        return new mViewH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d("어댑터 내부에서 로그인 아이디",""+loginUserId);

        // 썸네일
        Glide.with(context).load(ipad+"/Thum_dir/"+video_datas.get(position).content_thPath).into(((mViewH)holder).vList_thum);

        // 작성자 이미지
        Glide.with(context).load(ipad+video_datas.get(position).makeUserProfile).into(((mViewH)holder).vList_mkUserProfile);

        // 작성자 닉네임
        ((mViewH)holder).vList_mkUserNick.setText(video_datas.get(position).makeUserNick);

        // 제목
        ((mViewH)holder).vList_title.setText(video_datas.get(position).content_title);

        // 키워드
        ((mViewH)holder).vList_keyWord.setText(video_datas.get(position).content_keyWord);

        // 작성일
        ((mViewH)holder).vList_date.setText(video_datas.get(position).content_time);

        // 추천수
        ((mViewH)holder).vList_recoC.setText(""+video_datas.get(position).recommend);

        // 비추천
        ((mViewH)holder).vList_unrecoC.setText(""+video_datas.get(position).unrecommend);

        // 조회수
        ((mViewH)holder).vList_viewC.setText(""+video_datas.get(position).hits);
    }

    @Override
    public int getItemCount() {
        return video_datas.size();
    }



    // 뷰홀더
    private static class mViewH extends RecyclerView.ViewHolder {

        public ImageView vList_thum,vList_mkUserProfile;
        public TextView vList_title,vList_keyWord,vList_date,vList_recoC,vList_unrecoC,vList_viewC,vList_mkUserNick;
        public LinearLayout vList_ItemLayout;

        public mViewH(View view) {
            super(view);

             vList_thum = (ImageView)view.findViewById(R.id.vList_thum);
             vList_mkUserProfile = (ImageView)view.findViewById(R.id.vList_mkUserProfile);

             vList_title = (TextView)view.findViewById(R.id.vList_title);
             vList_mkUserNick = (TextView)view.findViewById(R.id.vList_mkUserNick);
             vList_keyWord = (TextView)view.findViewById(R.id.vList_keyWord);
             vList_date = (TextView)view.findViewById(R.id.vList_date);
             vList_recoC = (TextView)view.findViewById(R.id.vList_recoC);
             vList_unrecoC = (TextView)view.findViewById(R.id.vList_unrecoC);
             vList_viewC = (TextView)view.findViewById(R.id.vList_viewC);

            vList_ItemLayout = (LinearLayout)view.findViewById(R.id.vList_ItemLayout);

        }

    }

}
