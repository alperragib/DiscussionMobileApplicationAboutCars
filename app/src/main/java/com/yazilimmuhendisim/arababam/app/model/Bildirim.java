package com.yazilimmuhendisim.arababam.app.model;

import java.io.Serializable;

public class Bildirim implements Serializable {
    private int id,soru_id,okundu;
    private String title,content,date;

    public Bildirim(int id, int soru_id, int okundu, String title, String content, String date) {
        this.id = id;
        this.soru_id = soru_id;
        this.okundu = okundu;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSoru_id() {
        return soru_id;
    }

    public void setSoru_id(int soru_id) {
        this.soru_id = soru_id;
    }

    public int getOkundu() {
        return okundu;
    }

    public void setOkundu(int okundu) {
        this.okundu = okundu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
