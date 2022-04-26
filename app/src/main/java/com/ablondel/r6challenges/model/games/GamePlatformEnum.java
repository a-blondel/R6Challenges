package com.ablondel.r6challenges.model.games;

public enum GamePlatformEnum {
    PS4("psn", "05bfb3f7-6c21-4c42-be1f-97a33fb5cf66"),
    XONE("xbl", "5172a557-50b5-4665-b7db-e3f2e8c5041d"),
    PC("uplay", "98a601e5-ca91-4440-b1c5-753f601a2c90");

    private String platform;
    private String spaceId;

    GamePlatformEnum(String platform, String spaceId) {
        this.platform = platform;
        this.spaceId = spaceId;
    }

    public String getPlatform() {
        return platform;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public static GamePlatformEnum getPlatformByKey(String key) {
        return GamePlatformEnum.valueOf(key);
    }
}
