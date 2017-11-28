package com.cardee.data_source.remote;


import android.util.Log;

import com.cardee.CardeeApp;
import com.cardee.data_source.Error;
import com.cardee.data_source.OwnerProfileDataSource;
import com.cardee.data_source.remote.api.BaseResponse;
import com.cardee.data_source.remote.api.profile.Profile;
import com.cardee.data_source.remote.api.profile.response.OwnerProfileResponse;

import io.reactivex.functions.Consumer;

public class RemoteOwnerProfileDataSource implements OwnerProfileDataSource {

    private static final String TAG = RemoteOwnerProfileDataSource.class.getSimpleName();
    private static RemoteOwnerProfileDataSource instance;
    private final Profile mApi;

    private RemoteOwnerProfileDataSource() {
        mApi = CardeeApp.retrofit.create(Profile.class);
    }

    public static RemoteOwnerProfileDataSource getInstance() {
        if (instance == null) {
            instance = new RemoteOwnerProfileDataSource();
        }
        return instance;
    }

    @Override
    public void loadOwnerProfile(final Callback callback) {
        mApi.loadOwnerProfile().subscribe(new Consumer<OwnerProfileResponse>() {
            @Override
            public void accept(OwnerProfileResponse ownerProfileResponse) throws Exception {
                if (ownerProfileResponse.isSuccessful()) {
                    callback.onSuccess(ownerProfileResponse.getOwnerProfile());
                    return;
                }
                handleErrorResponse(callback, ownerProfileResponse);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, throwable.getMessage());
                callback.onError(new Error(Error.Type.LOST_CONNECTION, throwable.getMessage()));
            }
        });
    }

    private void handleErrorResponse(RemoteOwnerProfileDataSource.Callback callback, BaseResponse response) {
        if (response.getResponseCode() == BaseResponse.ERROR_CODE_INTERNAL_SERVER_ERROR) {
            callback.onError(new Error(Error.Type.SERVER, "Server error"));
        } else if (response.getResponseCode() == BaseResponse.ERROR_CODE_UNAUTHORIZED) {
            callback.onError(new Error(Error.Type.AUTHORIZATION, "Unauthorized"));
        } else {
            callback.onError(new Error(Error.Type.OTHER, "Undefined error"));
            throw new IllegalArgumentException("Unsupported response code: " + response.getResponseCode());
        }
    }
}
