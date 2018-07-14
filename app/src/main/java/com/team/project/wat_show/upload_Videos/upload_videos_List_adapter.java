package com.team.project.wat_show.upload_Videos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.project.wat_show.R;

public class upload_videos_List_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템을 디자인한 레이아웃을 불러오는 코드
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_recyclerview_item, parent, false);
        return new mViewH(view);*/
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    private static class mViewH extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public LinearLayout bestLayout;

        public mViewH(View view) {
            super(view);
        }
    }
}
