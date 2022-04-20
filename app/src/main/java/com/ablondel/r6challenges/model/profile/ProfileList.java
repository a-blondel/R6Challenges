package com.ablondel.r6challenges.model.profile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileList {
    private List<Profile> profiles;
}
