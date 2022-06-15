package com.yazilimmuhendisim.arababam.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private int id,yanit_size,like_size,en_iyi_yanit_size;
    private String username,about,user_profile_photo_url;
    private ArrayList<Soru> soruList;
    private ArrayList<Yanit> yanitList;

    public User(int id, int yanit_size, int like_size, int en_iyi_yanit_size, String username, String about, String user_profile_photo_url, ArrayList<Soru> soruList, ArrayList<Yanit> yanitList) {
        this.id = id;
        this.yanit_size = yanit_size;
        this.like_size = like_size;
        this.en_iyi_yanit_size = en_iyi_yanit_size;
        this.username = username;
        this.about = about;
        this.user_profile_photo_url = user_profile_photo_url;
        this.soruList = soruList;
        this.yanitList = yanitList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYanit_size() {
        return yanit_size;
    }

    public void setYanit_size(int yanit_size) {
        this.yanit_size = yanit_size;
    }

    public int getLike_size() {
        return like_size;
    }

    public void setLike_size(int like_size) {
        this.like_size = like_size;
    }

    public int getEn_iyi_yanit_size() {
        return en_iyi_yanit_size;
    }

    public void setEn_iyi_yanit_size(int en_iyi_yanit_size) {
        this.en_iyi_yanit_size = en_iyi_yanit_size;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUser_profile_photo_url() {
        return user_profile_photo_url;
    }

    public void setUser_profile_photo_url(String user_profile_photo_url) {
        this.user_profile_photo_url = user_profile_photo_url;
    }

    public ArrayList<Soru> getSoruList() {
        return soruList;
    }

    public void setSoruList(ArrayList<Soru> soruList) {
        this.soruList = soruList;
    }

    public ArrayList<Yanit> getYanitList() {
        return yanitList;
    }

    public void setYanitList(ArrayList<Yanit> yanitList) {
        this.yanitList = yanitList;
    }
}
