package com.cpen391.module2.hikingpal.module;


public class Message {

    private long id;
    private String content;
    private int sender;

    public Message() {
    }

    public long getId(){
        return id;
    }

    public String getContent(){
        return content;
    }

    public int getSender() {
        return sender;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setSender(int sender){
        this.sender = sender;
    }
}
