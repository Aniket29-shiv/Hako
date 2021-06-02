package com.hako.dreamproject.model;

//        mes: ""
//        msgId: ""
//        recieverId: ""
//        senderId: ""

public class Message {
    private String message;
    private String messageId;
    private String receiverId;
    private String senderId;

    public Message() { }

    public Message(String message, String messageId, String receiverId, String senderId) {
        this.message = message;
        this.messageId = messageId;
        this.receiverId = receiverId;
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
