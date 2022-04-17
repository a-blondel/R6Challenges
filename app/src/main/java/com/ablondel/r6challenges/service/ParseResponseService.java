package com.ablondel.r6challenges.service;

import com.ablondel.r6challenges.model.Authentication;
import com.ablondel.r6challenges.model.ProfileList;
import com.ablondel.r6challenges.model.UserInfos;
import com.google.gson.Gson;

public class ParseResponseService {

    public static UserInfos getUserInfos(String authenticationJson, String profilesJson) {
        UserInfos userInfos = new UserInfos();
        userInfos.setAuthentication(new Gson().fromJson(authenticationJson, Authentication.class));
        userInfos.setProfileList(new Gson().fromJson(profilesJson, ProfileList.class));
        return userInfos;
    }
}
