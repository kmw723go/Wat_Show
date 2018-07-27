package com.team.project.wat_show.upload_Videos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.Serializable;
import java.util.ArrayList;

public class Video_reple_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<video_reple> video_datas = new ArrayList<>();

    String loginUserId;

    com.team.project.wat_show.ip ip = new ip();



    public Video_reple_adapter(Context context, ArrayList<video_reple> video_datas) {
        this.context = context;
        this.video_datas = video_datas;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_reple_item, parent, false);
        return new mViewH(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


//ip+video_datas.get(position).video_reple_profile)
        // 프로필
        Glide.with(context).load(R.drawable.video_default).into(((mViewH)holder).video_reple_profile);

        // 아이디
        ((mViewH)holder).video_reple_id.setText(video_datas.get(position).video_reple_id);

        // 날짜
        ((mViewH)holder).video_reple_time.setText(video_datas.get(position).video_reple_time);

        //추천 개수
        ((mViewH)holder).video_reple_upcount.setText(video_datas.get(position).video_reple_upcount);

        //비추천 개수
        ((mViewH)holder).video_reple_downcount.setText(video_datas.get(position).video_reple_downcount);

        //답글 개수
        ((mViewH)holder).video_reple_count.setText(video_datas.get(position).video_reple_count);

        //내용
        ((mViewH)holder).video_reple_contents.setText(video_datas.get(position).video_reple_contents);

        //추천 클릭시
        ((mViewH)holder).video_reple_thumsup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "추천", Toast.LENGTH_SHORT).show();
            }
        });

        //비추천 클릭시
        ((mViewH)holder).video_reple_thumsdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "비추천", Toast.LENGTH_SHORT).show();
            }
        });

        //답글보기 클릭시
        ((mViewH)holder).video_reple_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "답글", Toast.LENGTH_SHORT).show();
            }
        });



        // 삭제 이벤트
        removeContent(holder,position);


    }



    @Override
    public int getItemCount() {
        return video_datas.size();
    }


    // 삭제 이벤트
    public void removeContent(RecyclerView.ViewHolder holder, final int position){

    }


    // 수정시 댓글 내용 불러오기
    public String Repleedit(final int position){
        String contents =  video_datas.get(position).video_reple_contents;
        return contents;
    }

    // 뷰홀더
    private static class mViewH extends RecyclerView.ViewHolder {

        public ImageView video_reple_profile,video_reple_thumsup,video_reple_thumsdown;
        public TextView video_reple_id,video_reple_time,video_reple_count,video_reple_go,video_reple_upcount,video_reple_downcount,video_reple_contents;

        public mViewH(View view) {
            super(view);

            video_reple_profile = (ImageView)view.findViewById(R.id.video_reple_profile);
            video_reple_thumsup = (ImageView)view.findViewById(R.id.video_reple_thumsup);
            video_reple_thumsdown = (ImageView)view.findViewById(R.id.video_reple_thumsdown);
            video_reple_id = (TextView)view.findViewById(R.id.video_reple_id);
            video_reple_time = (TextView)view.findViewById(R.id.video_reple_time);
            video_reple_count = (TextView)view.findViewById(R.id.video_reple_count);
            video_reple_go = (TextView)view.findViewById(R.id.video_reple_go);
            video_reple_upcount = (TextView)view.findViewById(R.id.video_reple_upcount);
            video_reple_downcount = (TextView)view.findViewById(R.id.video_reple_downcount);
            video_reple_contents = (TextView)view.findViewById(R.id.video_reple_contents);

        }

    }


}
