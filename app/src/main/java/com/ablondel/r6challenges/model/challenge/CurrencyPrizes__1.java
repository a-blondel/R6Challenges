
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class CurrencyPrizes__1 {

    @SerializedName("edges")
    @Expose
    public List<Edge> edges = null;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
