package com.ablondel.r6challenges.model.games;

public enum GamePlatformEnum {
    CROSSPLAY("0d2ae42d-4c27-4cb7-af6c-2099062302bb");

    private String spaceId;

    GamePlatformEnum(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceId() {
        return spaceId;
    }
}
