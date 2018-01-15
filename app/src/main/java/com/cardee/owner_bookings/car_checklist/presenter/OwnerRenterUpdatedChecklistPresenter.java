package com.cardee.owner_bookings.car_checklist.presenter;

import android.net.Uri;

import com.cardee.R;
import com.cardee.data_source.Error;
import com.cardee.domain.UseCase;
import com.cardee.domain.UseCaseExecutor;
import com.cardee.domain.bookings.BookingState;
import com.cardee.domain.bookings.entity.Checklist;
import com.cardee.domain.bookings.usecase.ChangeBookingState;
import com.cardee.domain.bookings.usecase.GetChecklist;
import com.cardee.domain.bookings.usecase.SaveChecklist;
import com.cardee.owner_bookings.car_checklist.adapter.CarSquareImagesAdapter;
import com.cardee.owner_bookings.car_checklist.strategy.PresentationStrategy;
import com.cardee.owner_bookings.car_checklist.strategy.RenterUpdatedChecklistStrategy;
import com.cardee.owner_bookings.car_checklist.view.ChecklistView;

import java.util.Arrays;

public class OwnerRenterUpdatedChecklistPresenter implements ChecklistContract.Presenter {

    private ChecklistContract.View mView;
    private ChecklistView mChecklistView;

    private PresentationStrategy mStrategy;

    private UseCaseExecutor mExecutor;

    private Checklist mChecklist;

    private final int mBookingId;

    private View mCallbacks;


    public OwnerRenterUpdatedChecklistPresenter(int bookingId) {
        mExecutor = UseCaseExecutor.getInstance();
        mBookingId = bookingId;
    }

    @Override
    public void setView(ChecklistContract.View view) {
        mView = view;
        if (view instanceof ChecklistView) {
            mChecklistView = (ChecklistView) view;
            mStrategy = new RenterUpdatedChecklistStrategy(mChecklistView, this);
        }
    }

    @Override
    public void setViewCallbacks(OwnerChecklistPresenter.View callbacks) {

    }

    @Override
    public void setViewRenterUpdatedCallbacks(View callbacks) {
        mCallbacks = callbacks;
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
        mView.showProgress(true);

        mExecutor.execute(new GetChecklist(), new GetChecklist.RequestValues(mBookingId),
                new UseCase.Callback<GetChecklist.ResponseValues>() {
                    @Override
                    public void onSuccess(GetChecklist.ResponseValues response) {
                        mView.showProgress(false);
                        if (response.isSuccess()) {
                            mChecklist = response.getChecklist();
                            fillData();
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

    }

    @Override
    public void init() {
        getChecklist();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onHandover() {

    }

    @Override
    public void onAccurateCancel() {

        if (mView != null) {
            mView.showProgress(true);
        }

        SaveChecklist.RequestValues values = new SaveChecklist.RequestValues(mBookingId, mChecklist.getRemarks(),
                mChecklist.getTank(), mChecklist.getMasterMileage(), mChecklist.getImageIds());

        mExecutor.execute(new SaveChecklist(), values,
                new UseCase.Callback<SaveChecklist.ResponseValues>() {
                    @Override
                    public void onSuccess(SaveChecklist.ResponseValues response) {
                        if (mView != null && response.isSuccess()) {
                            mView.showProgress(false);
                            mView.showMessage(R.string.saved_successfully);
                            mCallbacks.onCancelled();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        if (mView != null) {
                            mView.showProgress(false);
                            mView.showMessage(R.string.error_occurred);
                        }
                    }
                });
    }

    @Override
    public void onAccurateConfirm() {

        if (mView != null) {
            mView.showProgress(true);
        }

        ChangeBookingState.RequestValues request =
                new ChangeBookingState.RequestValues(mBookingId, BookingState.COLLECTED);
        mExecutor.execute(new ChangeBookingState(), request, new UseCase.Callback<ChangeBookingState.ResponseValues>() {
            @Override
            public void onSuccess(ChangeBookingState.ResponseValues response) {
                if (mView != null && response.isSuccessful()) {
                    mView.showProgress(false);
                    mCallbacks.onConfirmed();
                }
            }

            @Override
            public void onError(Error error) {
                if (mView != null) {
                    mView.showProgress(false);
                    mView.showMessage(error.getMessage());
                }
            }
        });
    }

    private void fillData() {
        if (mChecklist.isByMileage()) {
            String s = mChecklist.getMasterMileage() + " " +
                    mChecklistView.getContext().getString(R.string.car_rental_rates_per_km) + ". " +
                    mChecklistView.getContext().getString(R.string.car_rental_fuel_policy_by_mileage) + ".";
            mChecklistView.setMileagePetrolDesc(s);
        } else {
            String s = mChecklist.getMasterMileage() + " " +
                    mChecklistView.getContext().getString(R.string.owner_handover_tank_filled) +
                    mChecklistView.getContext().getString(R.string.car_rental_fuel_policy_similar_lvl) + ".";
            mChecklistView.setMileagePetrolDesc(s);
        }
        mChecklistView.setFirstTitle(mChecklist.isByMileage());
        CarSquareImagesAdapter adapter = new CarSquareImagesAdapter(mChecklistView.getContext());
        adapter.setItems(Arrays.asList(mChecklist.getImages()));
        mChecklistView.setImagesAdapter(adapter);

        mChecklistView.setRemarksText(mChecklist.getRemarks());
    }

    public interface View {
        void onConfirmed();
        void onCancelled();
    }
}