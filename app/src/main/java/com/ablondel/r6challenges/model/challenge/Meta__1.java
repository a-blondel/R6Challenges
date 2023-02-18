
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Meta__1 {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("isCollected")
    @Expose
    public Boolean isCollected;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
