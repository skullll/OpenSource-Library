package com.example.projectapp;

public class FindFriends {

    public  String profileimage,fullname,aboutyou;

    public FindFriends()
    {

    }

    public FindFriends(String profileimage, String fullname, String aboutyou) {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.aboutyou = aboutyou;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAboutyou() {
        return aboutyou;
    }

    public void setAboutyou(String aboutyou) {
        this.aboutyou = aboutyou;
    }
}

