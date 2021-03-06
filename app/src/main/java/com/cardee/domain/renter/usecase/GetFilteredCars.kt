package com.cardee.domain.renter.usecase

import com.cardee.data_source.Error
import com.cardee.data_source.RenterCarsDataSource
import com.cardee.data_source.RenterCarsRepository
import com.cardee.data_source.remote.api.offers.response.OfferResponseBody
import com.cardee.domain.RxUseCase
import com.cardee.domain.renter.entity.FilterRequest
import com.cardee.domain.renter.entity.OfferCar
import com.cardee.domain.renter.entity.mapper.OfferResponseBodyToOfferMapper
import io.reactivex.disposables.Disposable

class GetFilteredCars : RxUseCase<GetFilteredCars.RequestValues, GetFilteredCars.ResponseValues> {
    val repository: RenterCarsRepository = RenterCarsRepository.getInstance()

    override fun execute(values: RequestValues, callback: RxUseCase.Callback<ResponseValues>): Disposable {
        return repository.obtainCarsByFilter(values.filter, object : RenterCarsDataSource.OffersCallback {

            override fun onSuccess(response: Array<OfferResponseBody>) {
                callback.onSuccess(ResponseValues(OfferResponseBodyToOfferMapper.transform(response)))
            }

            override fun onError(error: Error?) {
                callback.onError(error ?: return)
            }
        })
    }

    class RequestValues(val filter: FilterRequest) : RxUseCase.RequestValues
    class ResponseValues(val cars: List<OfferCar>) : RxUseCase.ResponseValues
}