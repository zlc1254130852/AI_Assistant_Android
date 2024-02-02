package com.example.AI_Assistant;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ChatGptUtil {
    public static void sendHttpRequest(String question,okhttp3.Callback callback){

        String url = "https://api.openai.com/v1/chat/completions";
        String json="{\n" +
                "        \"model\": \"gpt-3.5-turbo\",\n" +
                "                \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"system\",\n" +
                "                \"content\": \"You are a helpful assistant.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "                \"content\": \""+question+"\"\n" +
                "        }\n" +
                "    ],\"stream\":true,\"max_tokens\":500,\"temperature\":0.5\n" +
                "  }";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request request=new Request.Builder().post(requestBody)
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization","")
                .url(url)
                .build();

        OkHttpClient okHttpClient=new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

            okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     *
     * @param result
     * @return
     * @throws JSONException
     */
    public static String parse(String result) throws JSONException {
        String content="";

        JSONTokener jsonTokener=new JSONTokener(result);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        JSONArray choices = jsonObject.getJSONArray("choices");

        if (choices!=null){
            JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message");
            content = messageObject.getString("content");
        }

        return content;
    }

    public static String parseStream(String result) throws JSONException {
        String content="";

        JSONTokener jsonTokener=new JSONTokener(result);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        JSONArray choices = jsonObject.getJSONArray("choices");

        if (choices!=null){
            JSONObject messageObject = choices.getJSONObject(0).getJSONObject("delta");
            String finish_reason = choices.getJSONObject(0).getString("finish_reason");
            if (!finish_reason.equals("stop"))
                content = messageObject.getString("content");
        }

        return content;
    }
}
