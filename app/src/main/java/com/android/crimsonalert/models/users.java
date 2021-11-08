package com.android.crimsonalert.models;

public class users {
    public String name;
    public String phone;
    public String userId;
    public String code;

public users() { }

    public users(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public users(String name, String phone, String userId, String code) {
        this.name = name;
        this.phone = phone;
        this.userId = userId;
        this.code = code;
    }

}


