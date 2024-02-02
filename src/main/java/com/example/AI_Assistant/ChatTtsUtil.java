package com.example.AI_Assistant;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatTtsUtil {

    private Context context;

    private MediaPlayer mediaPlayer;

    public ChatTtsUtil(Context context) {
        this.context=context;
        mediaPlayer = new MediaPlayer();

    }

    public void ttsStart(String result) {

        String json2="      {\n" +
                "        \"model\": \"tts-1\",\n" +
                "        \"input\": \""+result+"\",\n" +
                "        \"voice\": \"alloy\"\n" +
                "      }";

        MediaType mediaType2 = MediaType.parse("application/json");
        RequestBody requestBody2 = RequestBody.create(mediaType2,json2);

        Request request2 = new Request.Builder().post(requestBody2)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "")
                .url("https://api.openai.com/v1/audio/speech").build();

        OkHttpClient okHttpClient2 = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();

//            Response response2 = okHttpClient2.newCall(request2).execute();
            Call call = okHttpClient2.newCall(request2);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TAG-FAILED：", e.toString() + "");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    ResponseBody body = response.body();

                    InputStream is = body.byteStream();
                    byte[] bs = new byte[1024];

                    int len;

                    Log.d("fileStorage", ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DCIM)[0].getAbsolutePath());

                    OutputStream os = new FileOutputStream(ContextCompat.getExternalFilesDirs(context,Environment.DIRECTORY_DCIM)[0].getAbsolutePath()+"/speech.mp3");

                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }

                    os.close();
                    is.close();

                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(ContextCompat.getExternalFilesDirs(context,Environment.DIRECTORY_DCIM)[0].getAbsolutePath()+"/speech.mp3");
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                }
            });
    }
}
