package com.yazilimmuhendisim.arababam.app.model;

import java.io.Serializable;

public class Model implements Serializable {
    private int id;
    private String model;

    public Model(int id, String model) {
        this.id = id;
        this.model = model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
