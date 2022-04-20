
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RewardPrizes {

    @SerializedName("nodes")
    @Expose
    public List<Object> nodes = null;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
