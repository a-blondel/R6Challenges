
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Thresholds {

    @SerializedName("totalCount")
    @Expose
    public Integer totalCount;
    @SerializedName("nodes")
    @Expose
    public List<Node__2> nodes = null;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
