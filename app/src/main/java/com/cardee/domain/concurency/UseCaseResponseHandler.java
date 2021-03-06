package com.cardee.domain.concurency;

import android.os.Handler;
import android.os.Looper;

import com.cardee.data_source.Error;
import com.cardee.domain.UseCase;

public final class UseCaseResponseHandler {

    private final Handler mHandler;

    public UseCaseResponseHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    <R extends UseCase.ResponseValues> void notifyResponse(final R response, final UseCase.Callback<R> callback) {
        mHandler.post(() -> callback.onSuccess(response));
    }

    <R extends UseCase.ResponseValues> void onError(final Error error, final UseCase.Callback<R> callback) {
        mHandler.post(() -> callback.onError(error));
    }
}
