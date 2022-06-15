package com.yazilimmuhendisim.arababam.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Soru implements Serializable {

    private int id,like,like_size,user_id;
    private String marka,model,baslik,icerik,username,date,user_profile_photo_url;
    private ArrayList<String> imagesList;
    private ArrayList<Yanit> yanitlarList;

    public Soru(int id, int like, int like_size, int user_id, String marka, String model, String baslik, String icerik, String username, String date, String user_profile_photo_url, ArrayList<String> imagesList, ArrayList<Yanit> yanitlarList) {
        this.id = id;
        this.like = like;
        this.like_size = like_size;
        this.user_id = user_id;
        this.marka = marka;
        this.model = model;
        this.baslik = baslik;
        this.icerik = icerik;
        this.username = username;
        this.date = date;
        this.user_profile_photo_url = user_profile_photo_url;
        this.imagesList = imagesList;
        this.yanitlarList = yanitlarList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
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

    public ArrayList<Yanit> getYanitlarList() {
        return yanitlarList;
    }

    public void setYanitlarList(ArrayList<Yanit> yanitlarList) {
        this.yanitlarList = yanitlarList;
    }
}
