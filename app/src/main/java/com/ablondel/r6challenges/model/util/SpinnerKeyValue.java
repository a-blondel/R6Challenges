package com.ablondel.r6challenges.model.util;

import androidx.annotation.NonNull;

import lombok.Data;

@Data
public class SpinnerKeyValue {
    public String key;
    public String value;

    public SpinnerKeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
