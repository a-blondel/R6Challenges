package com.ablondel.r6challenges.model.games;

public enum GamePlatformEnum {
    PS4("psn"), XONE("xbl"), PC("uplay");

    private String platform;

    GamePlatformEnum(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public static GamePlatformEnum getPlatformByKey(String key) {
        return GamePlatformEnum.valueOf(key);
    }
}
