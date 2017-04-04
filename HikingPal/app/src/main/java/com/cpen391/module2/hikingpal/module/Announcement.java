package com.cpen391.module2.hikingpal.module;


public class Announcement {

    private long id;
    private String content;
    private String title;


    public Announcement() {
    }

    public long getId(){
        return id;
    }

    public String getContent(){
        return content;
    }

    public String getTitle(){
        return title;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
