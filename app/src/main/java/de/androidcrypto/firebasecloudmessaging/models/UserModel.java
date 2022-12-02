package de.androidcrypto.firebasecloudmessaging.models;

public class UserModel {

    String userId, userName, userMail, userToken="null";

    public UserModel() {
    }

    public UserModel(String userId, String userName, String userMail, String userToken) {
        this.userId = userId;
        this.userName = userName;
        this.userMail = userMail;
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
