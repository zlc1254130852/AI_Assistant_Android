package com.example.AI_Assistant;

public class ChatMessage {
    private String content;
    private int sendType;

    public static int ME_SEND=1;
    public static int CHATGPT_SEND=2;

    public ChatMessage(String content, int sendType) {
        this.content = content;
        this.sendType = sendType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

}
