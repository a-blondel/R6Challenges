
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PeriodicChallenges {

    @SerializedName("totalXpCount")
    @Expose
    public Integer totalXpCount;
    @SerializedName("xpEarnedCount")
    @Expose
    public Integer xpEarnedCount;
    @SerializedName("totalCount")
    @Expose
    public Integer totalCount;
    @SerializedName("challenges")
    @Expose
    public List<Challenge__1> challenges = null;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
