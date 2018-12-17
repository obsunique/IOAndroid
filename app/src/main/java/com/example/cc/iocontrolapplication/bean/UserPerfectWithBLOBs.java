package com.example.cc.iocontrolapplication.bean;

public class UserPerfectWithBLOBs extends UserPerfect {
    private String userphoto;

    private String userfaceimage;

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto == null ? null : userphoto.trim();
    }

    public String getUserfaceimage() {
        return userfaceimage;
    }

    public void setUserfaceimage(String userfaceimage) {
        this.userfaceimage = userfaceimage == null ? null : userfaceimage.trim();
    }
}