package com.cardee.data_source.cache;

import android.net.Uri;

import com.cardee.data_source.CarEditDataSource;
import com.cardee.data_source.remote.api.cars.request.CarTitleEntity;
import com.cardee.data_source.remote.api.cars.request.NewCarData;
import com.cardee.data_source.remote.api.common.entity.CarRuleEntity;
import com.cardee.data_source.remote.api.common.entity.DeliveryRatesEntity;
import com.cardee.data_source.remote.api.common.entity.FuelPolicyEntity;
import com.cardee.data_source.remote.api.common.entity.MinRentalDurationEntity;
import com.cardee.data_source.remote.api.common.entity.RentalRatesEntity;
import com.cardee.data_source.remote.api.common.entity.RentalTermsAdditionalEntity;
import com.cardee.data_source.remote.api.common.entity.RentalTermsInsuranceEntity;
import com.cardee.data_source.remote.api.common.entity.RentalTermsRequirementsEntity;
import com.cardee.data_source.remote.api.common.entity.RentalTermsSecurityDepositEntity;

public class LocalCarEditDataSource implements CarEditDataSource {

    private static LocalCarEditDataSource INSTANCE;

    private LocalCarEditDataSource() {

    }

    public static LocalCarEditDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalCarEditDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void updateLocation(Integer id, NewCarData carData, CarEditDataSource.Callback callback) {

    }

    @Override
    public void updateInfo(Integer id, NewCarData carData, CarEditDataSource.Callback callback) {

    }

    @Override
    public void updateCarTitle(Integer id, CarTitleEntity title, Callback callback) {

    }

    @Override
    public void updateRentalRequirements(Integer id, RentalTermsRequirementsEntity requirements, Callback callback) {

    }

    @Override
    public void updateRentalRules(Integer id, CarRuleEntity rules, Callback callback) {

    }

    @Override
    public void updateRentalSecurityDeposit(Integer id, RentalTermsSecurityDepositEntity securityDeposit, Callback callback) {

    }

    @Override
    public void updateRentalInsuranceExcess(Integer id, RentalTermsInsuranceEntity insuranceExcess, Callback callback) {

    }

    @Override
    public void updateRentalAdditionalTerms(Integer id, RentalTermsAdditionalEntity additionalEntity, Callback callback) {

    }

    @Override
    public void updateRentalRatesDaily(Integer id, RentalRatesEntity ratesEntity, Callback callback) {

    }

    @Override
    public void updateRentalRatesHourly(Integer id, RentalRatesEntity ratesEntity, Callback callback) {

    }

    @Override
    public void updateMinRentDaily(Integer id, MinRentalDurationEntity durationEntity, Callback callback) {

    }

    @Override
    public void updateMinRentHourly(Integer id, MinRentalDurationEntity durationEntity, Callback callback) {

    }

    @Override
    public void updateDescription(Integer id, String description, Callback callback) {

    }

    @Override
    public void updateDeliveryRates(Integer id, DeliveryRatesEntity deliveryRatesEntity, Callback callback) {

    }

    @Override
    public void updateInstantBookingDaily(Integer id, boolean isInstantBooking, Callback callback) {

    }

    @Override
    public void updateInstantBookingHourly(Integer id, boolean isInstantBooking, Callback callback) {

    }

    @Override
    public void updateInstantBookingDailyCount(Integer id, int count, Callback callback) {

    }

    @Override
    public void updateInstantBookingHourlyCount(Integer id, int count, Callback callback) {

    }

    @Override
    public void updateCurbsideDeliveryDaily(Integer id, boolean isCurbsideDelivery, Callback callback) {

    }

    @Override
    public void updateCurbsideDeliveryHourly(Integer id, boolean isCurbsideDelivery, Callback callback) {

    }

    @Override
    public void updateAcceptCashDaily(Integer id, boolean isAcceptCash, Callback callback) {

    }

    @Override
    public void updateAcceptCashHourly(Integer id, boolean isAcceptCash, Callback callback) {

    }

    @Override
    public void updateFuelPolicyDaily(Integer id, FuelPolicyEntity fuelPolicy, Callback callback) {

    }

    @Override
    public void updateFuelPolicyHourly(Integer id, FuelPolicyEntity fuelPolicy, Callback callback) {

    }

    @Override
    public void uploadImage(Integer id, Uri uri, ImageCallback callback) {

    }

    @Override
    public void deleteImage(Integer id, Integer imageId, Callback callback) {

    }

    @Override
    public void setPrimaryImage(Integer id, Integer imageId, Callback callback) {

    }
}
