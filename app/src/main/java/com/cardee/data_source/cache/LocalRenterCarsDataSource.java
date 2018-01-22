package com.cardee.data_source.cache;

import com.cardee.data_source.RenterCarsDataSource;


public class LocalRenterCarsDataSource implements RenterCarsDataSource {

    private static LocalRenterCarsDataSource INSTANCE;

    public static LocalRenterCarsDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalRenterCarsDataSource();
        }
        return INSTANCE;
    }


    @Override
    public void obtainCars(Callback callback) {

    }
}
