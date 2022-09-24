package com.ablondel.r6challenges.model;

import com.ablondel.r6challenges.model.auth.Authentication;
import com.ablondel.r6challenges.model.games.Game;
import com.ablondel.r6challenges.model.profile.ProfileList;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfos {
    private Authentication authentication;
    private ProfileList profileList;
    private List<Game> games;
    private String lastSelectedPlatform;
}
