package com.example.administrator.testbombapp.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/12 0012.
 */
public class Childs  extends BmobObject {

    private String grade;
    private  String hobby;

    public Childs(String grade,String hobby){
        this.grade=grade;
        this.hobby=hobby;
    }


    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
