package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@lombok.Data
public class Challenges {

    @SerializedName("data")
    @Expose
    public Data data;
}
