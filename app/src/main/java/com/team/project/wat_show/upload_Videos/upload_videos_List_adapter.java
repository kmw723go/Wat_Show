package com.team.project.wat_show.upload_Videos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team.project.wat_show.R;
import com.team.project.wat_show.ip;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class upload_videos_List_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Integer deleteRequast = 2222;

    Context context;
    ArrayList<video_content> video_datas = new ArrayList<>();

    String loginUserId;

    ip ip = new ip();
    String ipad =ip.getIp()+"/Thum_dir/";



    public upload_videos_List_adapter(Context context, ArrayList<video_content> video_datas, String loginUserId) {
        this.context = context;
        this.video_datas = video_datas;
        this.loginUserId =loginUserId;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_content_item, parent, false);
        return new mViewH(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // 썸네일
        Glide.with(context).load(ipad+video_datas.get(position).content_thPath).into(((mViewH)holder).thumImage);

        // 제목
        ((mViewH)holder).title.setText(video_datas.get(position).content_title);

        // 키워드
        ((mViewH)holder).keyWord.setText(video_datas.get(position).content_keyWord);

        //시간
        ((mViewH)holder).makeTime.setText(video_datas.get(position).content_time);




        // 숏클릭
        showMyContent(holder,position);

        // 삭제 이벤트
        removeContent(holder,position);


    }



    @Override
    public int getItemCount() {
        return video_datas.size();
    }


    // 삭제 이벤트
    public void removeContent(RecyclerView.ViewHolder holder, final int position){
        ((mViewH)holder).content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dataSelect = new AlertDialog.Builder(context);
                dataSelect.setTitle("알림");
                dataSelect.setMessage("게시물을 삭제 하시겠습니까?");

                dataSelect.setPositiveButton("삭제",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setDeleteHttp(position);
                    }
                });

                dataSelect.setNegativeButton("취소",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dataSelect.show();   // 실행


                return true;
            }
        });
    }

    //( 서버 연결 삭제 )
    public void setDeleteHttp(int position){
        class DeleteFromHttp extends AsyncTask<Void, Void, String> {

            OkHttpClient client = new OkHttpClient();
            String contentNo;
            String vPath;
            String thPath ;

            ProgressDialog dialog = new ProgressDialog(context);
            int position;

            public DeleteFromHttp(int position) {
                this.position = position;
                this.contentNo =video_datas.get(position).dataNo;
                this.vPath = video_datas.get(position).content_vPath;
                this.thPath = video_datas.get(position).content_thPath;
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
                String serverUrl = ip.getIp()+"/upload_Videos/delete_video_content.php";
                String result = "";
                try {

                    // 보낼 데이터 담기
                    RequestBody sendDatas = new FormBody.Builder()
                            .add("contentNo", contentNo)
                            .add("vPath", vPath)
                            .add("thPath", thPath)
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
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }
                Log.d("삭제 결과",""+a);

                Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                video_datas.remove(position);
                notifyDataSetChanged();

            }
        }  // 클래스 끝

        // Url. 연결
        new DeleteFromHttp(position).execute();
    }


    // 보기
    public void showMyContent(RecyclerView.ViewHolder holder, final int position){
        ((mViewH)holder).content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(context,showVideoContent.class);
                showContent.putExtra("showData", video_datas.get(position));
                showContent.putExtra("loginUserId",loginUserId);
                ((upload_videos_main)context).startActivityForResult(showContent,deleteRequast);

                // 삭제 코드  ( main 에서  결과로  받을 때도 사용이 된다. )

               /* Intent showContent = new Intent(context,video_test_page.class);
                showContent.putExtra("showData", video_datas.get(position));
                showContent.putExtra("loginUserId",loginUserId);
                ((upload_videos_main)context).startActivity(showContent);*/

            }
        });


    }


    // 뷰홀더
    private static class mViewH extends RecyclerView.ViewHolder {

        public ImageView thumImage;
        public TextView title,keyWord,makeTime;
        public LinearLayout content;

        public mViewH(View view) {
            super(view);

            thumImage = (ImageView)view.findViewById(R.id.thum_item);
            title = (TextView)view.findViewById(R.id.title_item);
            keyWord = (TextView)view.findViewById(R.id.keyWord_item);
            makeTime = (TextView)view.findViewById(R.id.makeTime_item);
            content = (LinearLayout)view.findViewById(R.id.vided_content_layout);

        }

    }
}
