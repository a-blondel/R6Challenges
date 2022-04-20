
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrencyPrizes__2 {

    @SerializedName("totalCount")
    @Expose
    public Integer totalCount;
    @SerializedName("collectedValuesCount")
    @Expose
    public Integer collectedValuesCount;
    @SerializedName("totalValuesCount")
    @Expose
    public Integer totalValuesCount;
    @SerializedName("edges")
    @Expose
    public List<Edge__1> edges = null;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
