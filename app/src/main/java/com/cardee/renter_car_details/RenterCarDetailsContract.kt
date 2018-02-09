package com.cardee.renter_car_details

import com.cardee.domain.renter.entity.RenterDetailedCar
import com.cardee.mvp.BaseView
import com.google.android.gms.maps.model.LatLng

interface RenterCarDetailsContract {

    interface View : BaseView {

        fun setCarLocationString(locationString: String)

        fun setDetailedCar(renterDetailedCar: RenterDetailedCar)

        fun setFavorite(favorite: Boolean)

    }

    interface Presenter {

        fun attachView(view: View)

        fun fetchLocation(callback: (LatLng) -> Unit)

        fun getDetailedCar(carId: Int?, lat: Double? = null, lng: Double? = null)

        fun addCarToFavorites(carId: Int?, favorite: Boolean)

        fun onDestroy()
    }
}
