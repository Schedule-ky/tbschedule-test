package com.convict.model;

import java.io.Serializable;

/**
 * Created by Jamin on 2016/9/14.
 */
public class OrderInfo implements Serializable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
