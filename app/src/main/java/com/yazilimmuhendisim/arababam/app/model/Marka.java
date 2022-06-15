package com.yazilimmuhendisim.arababam.app.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Marka implements Serializable {
    private int id;
    private String marka,logo_image_url;
    private ArrayList<Model> modelList;

    public Marka(int id, String marka, String logo_image_url, ArrayList<Model> modelList) {
        this.id = id;
        this.marka = marka;
        this.logo_image_url = logo_image_url;
        this.modelList = modelList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getLogo_image_url() {
        return logo_image_url;
    }

    public void setLogo_image_url(String logo_image_url) {
        this.logo_image_url = logo_image_url;
    }

    public ArrayList<Model> getModelList() {
        return modelList;
    }

    public void setModelList(ArrayList<Model> modelList) {
        this.modelList = modelList;
    }
}
