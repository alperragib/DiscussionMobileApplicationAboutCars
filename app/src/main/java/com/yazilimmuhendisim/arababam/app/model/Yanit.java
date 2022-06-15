package com.yazilimmuhendisim.arababam.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Yanit implements Serializable {
    private int id,user_id,soru_id,like,like_size,en_iyi_yanit;
    private String icerik,username,date,user_profile_photo_url;
    private ArrayList<String> imagesList;

    public Yanit(int id, int user_id, int soru_id, int like, int like_size, int en_iyi_yanit, String icerik, String username, String date, String user_profile_photo_url, ArrayList<String> imagesList) {
        this.id = id;
        this.user_id = user_id;
        this.soru_id = soru_id;
        this.like = like;
        this.like_size = like_size;
        this.en_iyi_yanit = en_iyi_yanit;
        this.icerik = icerik;
        this.username = username;
        this.date = date;
        this.user_profile_photo_url = user_profile_photo_url;
        this.imagesList = imagesList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getSoru_id() {
        return soru_id;
    }

    public void setSoru_id(int soru_id) {
        this.soru_id = soru_id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getLike_size() {
        return like_size;
    }

    public void setLike_size(int like_size) {
        this.like_size = like_size;
    }

    public int getEn_iyi_yanit() {
        return en_iyi_yanit;
    }

    public void setEn_iyi_yanit(int en_iyi_yanit) {
        this.en_iyi_yanit = en_iyi_yanit;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_profile_photo_url() {
        return user_profile_photo_url;
    }

    public void setUser_profile_photo_url(String user_profile_photo_url) {
        this.user_profile_photo_url = user_profile_photo_url;
    }

    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }
}
