package com.cardee.owner_bookings.car_checklist;

import android.net.Uri;

import com.cardee.R;
import com.cardee.data_source.Error;
import com.cardee.domain.UseCase;
import com.cardee.domain.UseCaseExecutor;
import com.cardee.domain.bookings.entity.Checklist;
import com.cardee.domain.bookings.usecase.AddImageToChecklist;
import com.cardee.domain.bookings.usecase.GetChecklist;
import com.cardee.domain.bookings.usecase.SaveChecklist;
import com.cardee.domain.owner.entity.Image;
import com.cardee.owner_bookings.car_checklist.adapter.CarSquareImagesAdapter;
import com.cardee.owner_bookings.car_checklist.strategy.ChecklistByMileageStrategy;
import com.cardee.owner_bookings.car_checklist.strategy.ChecklistStrategy;
import com.cardee.owner_bookings.car_checklist.strategy.PresentationStrategy;
import com.cardee.owner_bookings.car_checklist.view.ChecklistView;
import com.cardee.owner_car_details.view.listener.ImageViewListener;

import java.util.Arrays;


public class OwnerChecklistPresenter implements ChecklistContract.Presenter, ImageViewListener {

    private ChecklistContract.View mView;
    private ChecklistView mChecklistView;

    private int mBookingId;
    private final UseCaseExecutor mExecutor;
    private boolean isNotFetched = true;
    private PresentationStrategy mStrategy;

    private Checklist mChecklistObj;

    private View mCallbacks;

    public OwnerChecklistPresenter(int bookingId) {
        mBookingId = bookingId;
        mExecutor = UseCaseExecutor.getInstance();
    }

    @Override
    public void setView(ChecklistContract.View view) {
        mView = view;
        if (view instanceof ChecklistView) {
            mChecklistView = (ChecklistView) view;
        }
    }

    @Override
    public void setStrategy(PresentationStrategy strategy) {
        mStrategy = strategy;
    }

    @Override
    public void getChecklist() {
        if (mView == null) {
            return;
        }

        mView.showProgress(isNotFetched);
        mExecutor.execute(new GetChecklist(), new GetChecklist.RequestValues(mBookingId),
                new UseCase.Callback<GetChecklist.ResponseValues>() {
                    @Override
                    public void onSuccess(GetChecklist.ResponseValues response) {
                        if (response.isSuccess()) {
                            isNotFetched = false;
                            mView.showProgress(isNotFetched);
                            mChecklistObj = response.getChecklist();
                            chooseStrategy();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        mView.showProgress(false);
                        mView.showMessage(R.string.error_occurred);
                    }
                });
    }

    @Override
    public void onAddNewImage(Uri uri) {
        if (uri == null) {
            return;
        }

        mExecutor.execute(new AddImageToChecklist(), new AddImageToChecklist.RequestValues(mBookingId, uri),
                new UseCase.Callback<AddImageToChecklist.ResponseValues>() {
            @Override
            public void onSuccess(AddImageToChecklist.ResponseValues response) {
                init();
            }

            @Override
            public void onError(Error error) {
                mView.showMessage(R.string.error_occurred);
            }
        });
    }

    @Override
    public void init() {
        if (isNotFetched) {
            getChecklist();
        }
    }

    @Override
    public void onDestroy() {
        isNotFetched = true;
    }

    @Override
    public void onHandover() {
        mExecutor.execute(new SaveChecklist(),
                new SaveChecklist.RequestValues(mBookingId, mChecklistView.getRemarksText(),
                        mChecklistView.getTankFullness(), mChecklistView.getMileage(), mChecklistObj.getImageIds()),
                new UseCase.Callback<SaveChecklist.ResponseValues>() {
                    @Override
                    public void onSuccess(SaveChecklist.ResponseValues response) {
                        if (response.isSuccess()) {
                            mCallbacks.onHandover(mBookingId);
                        }
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    private void chooseStrategy() {
        if (mChecklistObj.isByMileage()) {
            mStrategy = new ChecklistByMileageStrategy(mChecklistView, this);
            mChecklistView.setMasterMileageValue(String.valueOf(mChecklistObj.getMasterMileage()) + " km");
        } else {
            mStrategy = new ChecklistStrategy(mChecklistView, this);
            mChecklistView.setMasterMileageValue(mChecklistObj.getTank());
        }
        CarSquareImagesAdapter adapter = new CarSquareImagesAdapter(mChecklistView.getContext());
        adapter.setImageViewListener(this);
        adapter.setItems(Arrays.asList(mChecklistObj.getImages()));
        mChecklistView.setImagesAdapter(adapter);
    }

    @Override
    public void onImageClick(Image image) {

    }

    @Override
    public void onAddNewClick() {
        if (mCallbacks == null) {
            return;
        }
        mCallbacks.onPickPhoto();
    }


    @Override
    public void setViewCallbacks(View view) {
        mCallbacks = view;
    }

    public interface View {
        void onPickPhoto();
        void onHandover(int bookingId);
    }

}
