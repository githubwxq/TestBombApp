package com.example.administrator.testbombapp.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class Person extends BmobObject {
    private String name;
    private String address;
    private List<String> allcourse = new ArrayList<String>();
    private List<Childs> allchilds = new ArrayList<Childs>();

    public List<Childs> getAllchilds() {
        return allchilds;
    }

    public void setAllchilds(List<Childs> allchilds) {
        this.allchilds = allchilds;
    }

    public List<String> getAllcourse() {
        return allcourse;
    }

    public void setAllcourse(List<String> allcourse) {
        this.allcourse = allcourse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}