package com.cardee.domain;

public interface UseCase<V extends UseCase.RequestValues, R extends UseCase.ResponseValues> {

    void execute(V values, Callback<R> callback);

    interface RequestValues {

    }

    interface ResponseValues<C> {

        C getContent();

    }

    interface Callback<R> {
        void onSuccess(R response);

        void onError();
    }
}
