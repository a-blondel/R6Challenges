
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class ItemPrizes__1 {

    @SerializedName("nodes")
    @Expose
    public List<Node__4> nodes = null;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
