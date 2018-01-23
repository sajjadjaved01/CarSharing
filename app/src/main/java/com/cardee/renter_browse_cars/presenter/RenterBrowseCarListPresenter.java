package com.cardee.renter_browse_cars.presenter;


import android.support.v4.app.FragmentActivity;

import com.cardee.custom.modal.SortBookingDialog;
import com.cardee.custom.modal.SortRenterOffersDialog;
import com.cardee.data_source.Error;
import com.cardee.domain.UseCase;
import com.cardee.domain.UseCaseExecutor;
import com.cardee.domain.owner.entity.Car;
import com.cardee.domain.renter.entity.OfferCar;
import com.cardee.domain.renter.usecase.AddCarToFavorites;
import com.cardee.domain.renter.usecase.GetCars;
import com.cardee.domain.renter.usecase.GetFavorites;
import com.cardee.settings.Settings;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.reactivex.functions.Consumer;

public class RenterBrowseCarListPresenter implements Consumer<RenterBrowseCarListContract.CarEvent>,
        RenterBrowseCarListContract.Presenter, SortRenterOffersDialog.SortSelectListener {

    private final UseCaseExecutor mExecutor;
    private RenterBrowseCarListContract.View mView;

    private boolean firstStart = true;

    private boolean isFavorites = false;

    private final Settings mSettings;


    public RenterBrowseCarListPresenter(RenterBrowseCarListContract.View view, Settings settings) {
        mView = view;
        mExecutor = UseCaseExecutor.getInstance();
        mSettings = settings;
    }

    @Override
    public void accept(RenterBrowseCarListContract.CarEvent carEvent) throws Exception {

        switch (carEvent.getAction()) {
            case UPDATED:
                break;

            case OPEN:
                break;

            case FAVORITE:
                addCarToFavorites(carEvent.getCar().getCarId());
                break;
        }

    }

    public void loadItems() {
        if (firstStart && mView != null) {
            mView.showProgress(true);
        }

        mExecutor.execute(new GetCars(), new GetCars.RequestValues(), new UseCase.Callback<GetCars.ResponseValues>() {
            @Override
            public void onSuccess(GetCars.ResponseValues response) {
                List<OfferCar> cars = response.getOfferCars();
                if (firstStart && mView != null) {
                    mView.showProgress(false);
                    firstStart = false;
                } else {
                    Crashlytics.logException(new Throwable("Success: View is null"));
                }
                mView.setItems(cars);
            }

            @Override
            public void onError(Error error) {
                handleError(error);
            }
        });

    }

    private void addCarToFavorites(int carId) {
        if (mView != null) {
            mView.showProgress(true);
        }
        mExecutor.execute(new AddCarToFavorites(), new AddCarToFavorites.RequestValues(carId), new UseCase.Callback<AddCarToFavorites.ResponseValues>() {
            @Override
            public void onSuccess(AddCarToFavorites.ResponseValues response) {
                mView.showProgress(false);
                if (isFavorites) {
                    showFavorites(true);
                    return;
                }
                loadItems();
            }

            @Override
            public void onError(Error error) {
                mView.showProgress(false);
                handleError(error);
            }
        });
    }

    public void showFavorites(boolean show) {
        isFavorites = show;
        if (show) {
            if (mView != null) {
                mView.showProgress(true);
            }
            mExecutor.execute(new GetFavorites(), new GetFavorites.RequestValues(true),
                    new UseCase.Callback<GetFavorites.ResponseValues>() {
                        @Override
                        public void onSuccess(GetFavorites.ResponseValues response) {
                            List<OfferCar> cars = response.getOfferCars();
                            if (mView != null) {
                                mView.showProgress(false);
                                mView.setItems(cars);
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            mView.showProgress(false);
                            handleError(error);
                        }
                    });
            return;
        }
        loadItems();
    }

    public void refresh() {
        firstStart = true;
    }

    private void handleError(Error error) {

        if (error.isAuthError()) {
            mView.onUnauthorized();
        } else if (error.isConnectionError()) {
            mView.onConnectionLost();
        } else {
            mView.showMessage(error.getMessage());
        }
    }

    @Override
    public void showSort(FragmentActivity activity) {
        SortRenterOffersDialog sortDialog = SortRenterOffersDialog.getInstance(mSettings.getSortOffers());
        sortDialog.show(activity.getSupportFragmentManager(), sortDialog.getTag());
        sortDialog.setSortSelectListener(this);
    }

    @Override
    public void setSort(RenterBrowseCarListContract.Sort sort) {
        mSettings.setSortOffers(sort);
    }

    @Override
    public void onSortSelected(RenterBrowseCarListContract.Sort sort) {

    }
}
