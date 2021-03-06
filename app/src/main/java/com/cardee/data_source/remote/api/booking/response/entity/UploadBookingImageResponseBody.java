package com.cardee.data_source.remote.api.booking.response.entity;

import com.cardee.data_source.remote.api.ErrorResponseBody;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UploadBookingImageResponseBody extends ErrorResponseBody {

    @Expose
    @SerializedName("id")
    private Integer imageId;

    public UploadBookingImageResponseBody() {

    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
