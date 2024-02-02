package com.example.AI_Assistant;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends Activity implements View.OnClickListener{
    private List<ChatMessage> data;
    private ChatItemAdapter chatItemAdapter;
    private ImageButton asrSendBtn;
    private Button chatSpeakBtn;
    private EditText chatInputEt;
    private Button btnSend;
    private ChatAsrUtil chatAsrUtil;
    private ChatTtsUtil chatTtsUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_layout);

        btnSend=findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        asrSendBtn = findViewById(R.id.btn_asr_send);
        asrSendBtn.setOnClickListener(this);

        chatSpeakBtn=findViewById(R.id.btn_chat_speak);
        chatSpeakBtn.setOnClickListener(this);

        chatSpeakBtn.setOnTouchListener(touchListener);

        chatInputEt=findViewById(R.id.et_chat_input);


        RecyclerView recyclerView = findViewById(R.id.rv_chat);

        data=new ArrayList<ChatMessage>();

        chatItemAdapter=new ChatItemAdapter(data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatItemAdapter);

        chatAsrUtil = new ChatAsrUtil(this);
        chatTtsUtil=new ChatTtsUtil(this);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction()==MotionEvent.ACTION_DOWN){

                chatAsrUtil.onClick(MotionEvent.ACTION_DOWN);
                chatSpeakBtn.setText("release to send");

            } else if (event.getAction()==MotionEvent.ACTION_UP){

                chatAsrUtil.onClick(MotionEvent.ACTION_UP);
                chatSpeakBtn.setText("press to speak");

                sendToChatGPTAudio();

            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_send){
            Log.d("","clicked");

            String chatStr = chatInputEt.getText().toString();
            ChatMessage chatMessage=new ChatMessage(""+chatStr,ChatMessage.ME_SEND);
            addChatMessage(chatMessage);
            chatInputEt.setText("");

            sendToChatGPT(chatStr);

        } else if(v.getId()==R.id.btn_asr_send){
            if (btnSend.getVisibility()!=View.GONE){
                asrSendBtn.setImageResource(R.drawable.keyboard);
                chatSpeakBtn.setVisibility(View.VISIBLE);
                chatInputEt.setVisibility(View.GONE);
                btnSend.setVisibility(View.GONE);
            } else {
                asrSendBtn.setImageResource(R.drawable.mic);
                chatSpeakBtn.setVisibility(View.GONE);
                chatInputEt.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.VISIBLE);
            }
        } else if(v.getId()==R.id.btn_chat_speak){

        }
    }
    private void addChatMessage(ChatMessage chatMessage){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.add(chatMessage);
                chatItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addChatMessageStream(ChatMessage chatMessage){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                data.set(data.size()-1,chatMessage);
                Log.d("size", Integer.toString(data.size()));
                chatItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void sendToChatGPT(String chatStr){

        ChatGptUtil.sendHttpRequest(chatStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("result",e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream inputStream = response.body().byteStream();

                Reader reader = new InputStreamReader(inputStream,"utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                bufferedReader.readLine();
                String str;
                String strTotal="";

                ChatMessage chatMessageGpt=new ChatMessage("",ChatMessage.CHATGPT_SEND);
                addChatMessage(chatMessageGpt);

                try {
                    while ((str = bufferedReader.readLine()) != null) {
                        if (str.length()>6 && !str.equals("data: [DONE]")){
//                            Log.d("chunk",str);
                            String parseResult = ChatGptUtil.parseStream(str.substring(6));

                            strTotal+=parseResult;

                            chatMessageGpt.setContent(""+strTotal);
                            addChatMessageStream(chatMessageGpt);
                        }

                    }

                    chatTtsUtil.ttsStart(strTotal);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    inputStream.close();

                }

            }
        });

    }

    private void sendToChatGPTAudio(){

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();

        File file = new File(ContextCompat.getExternalFilesDirs(getApplicationContext(),Environment.DIRECTORY_DCIM)[0].getAbsolutePath()+"/openai.mp3");

        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mp3"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "openai.mp3", fileBody)
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/form-data")
                .addHeader("Authorization", "")
                .url("https://api.openai.com/v1/audio/transcriptions")
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG-失败：", e.toString() + "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("TAG-成功：", string + "");
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String parseString = jsonObject.getString("text");

                    ChatMessage chatMessageGpt=new ChatMessage(""+parseString+"",ChatMessage.ME_SEND);
                    addChatMessage(chatMessageGpt);

                    Log.d("check",parseString);

                    sendToChatGPT(parseString);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatAsrUtil.destroy();
    }
}
