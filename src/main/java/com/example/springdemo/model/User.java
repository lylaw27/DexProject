package com.example.springdemo.model;

import org.web3j.crypto.Credentials;

public class User extends UserInfo{
    Credentials credentials;

    public User(String privateKey) {
        this.credentials = Credentials.create(privateKey);
        this.userId = credentials.getAddress();
    }

    Credentials getCredentials() {
        return credentials;
    }

}
