package com.ablondel.r6challenges.service;

import android.util.Log;

public class LogService {
    private static final int CHUNK_SIZE = 2048;

    public static void displayLongLog(String tag, String message) {
        for (int i = 0; i < message.length(); i += CHUNK_SIZE) {
            Log.d(tag, message.substring(i, Math.min(message.length(), i + CHUNK_SIZE)));
        }
    }
}
