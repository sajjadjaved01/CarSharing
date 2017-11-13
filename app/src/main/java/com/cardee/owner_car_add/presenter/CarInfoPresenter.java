package com.cardee.owner_car_add.presenter;

import com.cardee.domain.owner.entity.CarData;
import com.cardee.owner_car_add.view.NewCarContract;


public class CarInfoPresenter extends NewCarPresenter {

    private NewCarContract.View view;
    private CarData carData;

    public CarInfoPresenter(NewCarContract.View view) {
        super(view);
        this.view = view;
    }

    public void saveInputs(String make,
                           String model,
                           String year,
                           String title,
                           String license,
                           String seatingCapacity,
                           String engineCapacity,
                           Integer transmissionId,
                           Integer bodyTypeId) {
        carData = carData.newBuilder()
                .setMake(make)
                .setModel(model)
                .setManufactureYear(year == null ? null : Integer.valueOf(year))
                .setTitle(title)
                .setLicencePlateNumber(license)
                .setSeatingCapacity(seatingCapacity)
                .setEngineCapacity(engineCapacity)
                .setTransmissionId(transmissionId)
                .setBodyType(bodyTypeId)
                .build();
        super.saveCar(carData, false);
    }

    @Override
    public void onCarDataResponse(CarData carData) {
        super.onCarDataResponse(carData);
        this.carData = carData;
    }

    @Override
    public void onSaved() {
        if (view != null) {
            view.onFinish();
        }
    }
}
