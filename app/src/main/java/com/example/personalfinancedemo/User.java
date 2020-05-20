package com.example.personalfinancedemo;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userId="";
    private String userPwd="";
    private String userEmail="";
    private int userIcon=0;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(int userIcon) {
        this.userIcon = userIcon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userPwd);
        dest.writeString(this.userEmail);
        dest.writeInt(this.userIcon);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.userPwd = in.readString();
        this.userEmail = in.readString();
        this.userIcon = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
