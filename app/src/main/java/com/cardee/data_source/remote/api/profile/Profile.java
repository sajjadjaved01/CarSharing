package com.cardee.data_source.remote.api.profile;


import com.cardee.data_source.remote.api.profile.response.CarsResponse;
import com.cardee.data_source.remote.api.profile.response.NoDataResponse;
import com.cardee.data_source.remote.api.profile.response.OwnerProfileResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface Profile {

    @GET("profiles/owner")
    @Headers("Content-Type: application/json")
    Observable<OwnerProfileResponse> loadOwnerProfile();

    @GET("profiles/owner/cars")
    @Headers("Content-Type: application/json")
    Observable<CarsResponse> loadOwnersCarList();

    //TODO: implement
    void uploadProfilePhoto();

    @PUT("profiles/owner/note")
    @Headers("Content-Type: application/json")
    Observable<NoDataResponse> updateOwnerNote();

    @PUT("profiles/settings")
    @Headers("Content-Type: application/json")
    Observable<NoDataResponse> updateNotificationSettings();
}