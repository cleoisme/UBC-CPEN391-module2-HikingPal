package com.cpen391.module2.hikingpal.module;


public class Message {

    private long id;
    private String message;
    private int sender;

    public Message(long id, String message, int sender) {
        this.id = id;
        this.message = message;
        this.sender = sender;
    }

    public long getId(){
        return id;
    }

    public String getContent(){
        return message;
    }

    public int getSender() {
        return sender;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setContent(String content){
        this.message = content;
    }

    public void setSender(int sender){
        this.sender = sender;
    }
}
