package com.team.project.wat_show.Login_Signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.team.project.wat_show.MainActivity;
import com.team.project.wat_show.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;


public class find_id_pw extends AppCompatActivity {

    EditText emailInput,idInput;
    RadioGroup select;
    Button submit;
    String check = "id";
    private HttpConnection httpConn = new HttpConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_pw);
        findView();
        select();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.equals("id")){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email",emailInput.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendData(check,jsonObject);
                }else{
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email",emailInput.getText().toString());
                        jsonObject.put("id",idInput.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendData(check,jsonObject);
                }

            }
        });
    }

    public void select(){
        select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.findId){
                    idInput.setVisibility(View.GONE);
                    check = "id";
                }else{
                    idInput.setVisibility(View.VISIBLE);
                    check = "id_email";
                }
            }
        });
    }

    public void findView(){
        emailInput = (EditText)findViewById(R.id.Find_Email_Input);
        idInput = (EditText)findViewById(R.id.Find_Id_Input);
        submit = (Button)findViewById(R.id.Find_Submit);
        select = (RadioGroup)findViewById(R.id.id_pass_select);
    }

    //okhttp를 통한 서버통신
    public class HttpConnection {

        private OkHttpClient client;

        private HttpConnection(){ this.client = new OkHttpClient(); }


        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(String check, JSONObject json, Callback callback) {
            if(check.equals("id")){
                String encoEmail =null;
                try {
                    encoEmail = URLEncoder.encode(json.getString("email"),"UTF-8");
                    Log.e("qwe",encoEmail);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = new FormBody.Builder()
                        .add("check", check)
                        .add("email", encoEmail)
                        .build();
                Request request = new Request.Builder()
                        .url("http://54.180.2.34/Login_Signup/find_IdPass.php")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }else{
                String encoEmail =null,encoId=null;
                try {
                    encoEmail = URLEncoder.encode(json.getString("email"),"UTF-8");
                    encoId = URLEncoder.encode(json.getString("id"),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody body = new FormBody.Builder()
                        .add("check", check)
                        .add("email", encoEmail)
                        .add("id",encoId)
                        .build();
                Request request = new Request.Builder()
                        .url("http://54.180.2.34/Login_Signup/find_IdPass.php")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }


        }

    }

    private void sendData(final String check, final JSONObject jsonObject) {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(check,jsonObject, callback);
            }
        }.start();;
    }

    //http통신의 결과값 수신
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            final GmailSender gmailSender = new GmailSender();
            Log.e("qweqwe",body);
            if(check.equals("id")){
                if(body.equals("1")){
                    new Thread(new Runnable() {
                        @Override public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(find_id_pw.this, "이메일이 일치하지 않거나 아이디가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                                } });

                        }
                    }).start();
                }else{
                    final String[] item = body.split(",");
                    new Thread(new Runnable() {
                        @Override public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함

                            try {
                                Log.e("qweqwe23",item[0]+"++++"+item[1]);
                                gmailSender.sendMail("[왓쇼] ID 찾기 관련 메일입니다", "\n 귀하의 ID는 "+ item[1] + "입니다",item[0]);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(find_id_pw.this, "전송에 성공하였습니다 이메일을 확인하세요", Toast.LENGTH_SHORT).show();
                                } });


                        }
                    }).start();

                }
            }else{
                if(body.equals("1")){
                    new Thread(new Runnable() {
                        @Override public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(find_id_pw.this, "이메일이 일치하지 않거나 아이디가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                                } });

                        }
                    }).start();
                }else{
                    final String[] item = body.split(",");
                    new Thread(new Runnable() {
                        @Override public void run() {
                            // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                            try {
                                Log.e("qweqwe23",item[0]+"++++"+item[1]);
                                gmailSender.sendMail("[왓쇼] 비밀번호 찾기 관련 메일입니다", "\n 귀하의 임시 비밀번호는 "+ item[1] + "입니다",item[0]);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(find_id_pw.this, "전송에 성공하였습니다 이메일을 확인하세요", Toast.LENGTH_SHORT).show();
                                } });

                        }
                    }).start();
                }
            }

        }
    };

    public class GmailSender extends javax.mail.Authenticator{
        private String mailhost = "smtp.gmail.com";
        private String user = "jiung0802test";
        private String password ="j10361215!";
        private Session session;

        public GmailSender(){

            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.host", mailhost);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.quitwait", "false");

            //구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달해준다.
            session = Session.getDefaultInstance(props, this);
        }


        protected PasswordAuthentication getPasswordAuthentication() {
            //해당 메서드에서 사용자의 계정(id & password)을 받아 인증받으며 인증 실패시 기본값으로 반환됨.
            return new PasswordAuthentication(user, password);
        }


        // 제목, 본문내용 , 받는 사람 이메일
        public synchronized void sendMail(String subject, String body, String recipients) throws Exception {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain")); //본문 내용을 byte단위로 쪼개어 전달
            message.setSender(new InternetAddress(user));  //본인 이메일 설정
            message.setSubject(subject); //해당 이메일의 본문 설정
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            }else {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            }Transport.send(message); //메시지 전달

        }


        public class ByteArrayDataSource implements DataSource {
            private byte[] data;
            private String type;


            public ByteArrayDataSource(byte[] data, String type) {
                super();
                this.data = data;
                this.type = type;
            }

            public ByteArrayDataSource(byte[] data) {
                super();
                this.data = data;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getContentType() {
                if (type == null)
                    return "application/octet-stream";
                else
                    return type;
            }

            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data);
            }

            public String getName() {
                return "ByteArrayDataSource";
            }

            public OutputStream getOutputStream() throws IOException {
                throw new IOException("Not Supported");
            }

        }


    }


}
