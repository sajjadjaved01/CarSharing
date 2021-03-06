package com.cardee.domain.owner.usecase;

import com.cardee.data_source.CarEditDataSource;
import com.cardee.data_source.CarEditRepository;
import com.cardee.data_source.Error;
import com.cardee.domain.UseCase;


public class UpdateHourlyCurbsideDeliveryState implements UseCase<UpdateHourlyCurbsideDeliveryState.RequestValues,
        UpdateHourlyCurbsideDeliveryState.ResponseValues> {

    private CarEditRepository mRepository;

    public UpdateHourlyCurbsideDeliveryState() {
        mRepository = CarEditRepository.getInstance();
    }

    @Override
    public void execute(RequestValues values, Callback<ResponseValues> callback) {
        mRepository.updateCurbsideDeliveryHourly(values.getId(), values.isCurbsideDelivery(),
                new CarEditDataSource.Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess(new UpdateHourlyCurbsideDeliveryState.ResponseValues(true));
                    }

                    @Override
                    public void onError(Error error) {
                        callback.onError(error);
                    }
                });
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final int mId;
        private final boolean isCurbsideDelivery;

        public RequestValues(int id, boolean isCurbsideDelivery) {
            mId = id;
            this.isCurbsideDelivery = isCurbsideDelivery;
        }

        public int getId() {
            return mId;
        }

        public boolean isCurbsideDelivery() {
            return isCurbsideDelivery;
        }
    }

    public static class ResponseValues implements UseCase.ResponseValues {
        private final boolean isSuccess;

        public ResponseValues(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }
}
