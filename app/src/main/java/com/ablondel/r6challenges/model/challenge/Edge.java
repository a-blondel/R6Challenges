
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Edge {

    @SerializedName("meta")
    @Expose
    public Meta__2 meta;
    @SerializedName("node")
    @Expose
    public Node__3 node;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
