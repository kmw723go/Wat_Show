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

import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;
import com.team.project.wat_show.upload_Videos.video_content;

import java.util.ArrayList;

public class videoContent_adepter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context context;
    ArrayList<video_content> video_datas = new ArrayList<>();

    String loginUserId;

    ip ip = new ip();
    String ipad =ip.getIp()+"/Thum_dir/";

    public videoContent_adepter(Context context, ArrayList<video_content> video_datas, String loginUserId) {
        this.context = context;
        this.video_datas = video_datas;
        this.loginUserId = loginUserId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_video_content_item, parent, false);
        return new mViewH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d("어댑터 내부에서 로그인 아이디",""+loginUserId);

    }

    @Override
    public int getItemCount() {
        return video_datas.size();
    }



    // 뷰홀더
    private static class mViewH extends RecyclerView.ViewHolder {

        public ImageView thumImage,makeUserProfile;
        public TextView title,keyWord,makeTime,recoCount,unrecoCount,viewCount;
        public LinearLayout content;

        public mViewH(View view) {
            super(view);

        }

    }

}
