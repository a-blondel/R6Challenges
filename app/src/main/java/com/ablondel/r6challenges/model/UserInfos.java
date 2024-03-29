package com.ablondel.r6challenges.model;

import com.ablondel.r6challenges.model.auth.Authentication;
import com.ablondel.r6challenges.model.profile.ProfileList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfos {
    private Authentication authentication;
    private ProfileList profileList;
    private String lastSelectedPlatform;
}
