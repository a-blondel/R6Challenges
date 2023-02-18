
package com.ablondel.r6challenges.model.challenge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Viewer__1 {

    @SerializedName("meta")
    @Expose
    public Meta__1 meta;
    @SerializedName("__typename")
    @Expose
    public String typename;

}
