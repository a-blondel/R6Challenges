package com.ablondel.r6challenges.model.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String profileId;
    private String platformType;
    private String nameOnPlatform;
}
