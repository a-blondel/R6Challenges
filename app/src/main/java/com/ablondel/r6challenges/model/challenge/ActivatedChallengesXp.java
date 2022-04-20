
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivatedChallengesXp {

    @SerializedName("totalXpCount")
    @Expose
    public Integer totalXpCount;
    @SerializedName("xpEarnedCount")
    @Expose
    public Integer xpEarnedCount;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
