package com.cardee.renter_book_car.presenter

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cardee.R
import com.cardee.data_source.Error
import com.cardee.data_source.remote.api.booking.request.BookingRequest
import com.cardee.data_source.remote.api.booking.response.entity.BookingCost
import com.cardee.data_source.remote.api.booking.response.entity.CostRequest
import com.cardee.data_source.remote.api.offers.response.OfferByIdResponseBody
import com.cardee.domain.RxUseCase
import com.cardee.domain.bookings.entity.BookCarState
import com.cardee.domain.bookings.usecase.GetCostBreakdown
import com.cardee.domain.bookings.usecase.RequestBooking
import com.cardee.domain.profile.usecase.GetVerifyDetails
import com.cardee.domain.renter.entity.BrowseCarsFilter
import com.cardee.domain.renter.usecase.*
import com.cardee.renter_book_car.BookCarContract
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_cost_breakdown.view.*
import java.util.*

class BookCarPresenter : BookCarContract.BookCarPresenter {

    private var mView: BookCarContract.BookCarView? = null
    private val getOfferById = GetOfferById()
    private val getCostBreakdown = GetCostBreakdown()
    private val getFilter = GetFilter()
    private val saveFilter = SaveFilter()
    private val getBookState = GetBookState()
    private val saveBookState = SaveBookState()
    private val requestBookingCase = RequestBooking()
    private val getVerifyState = GetVerifyDetails()

    private var mGetOfferDisposable: Disposable? = null
    private var mGetCostDisposable: Disposable? = null
    private var mBookDisposable: Disposable? = null
    private var verifyDisposable: Disposable? = null
    private var mCostBreakdown: BookingCost? = null
    private var mCarId: Int? = null
    private var showBreakdown: Boolean = false

    override fun init(bookCarView: BookCarContract.BookCarView) {
        mView = bookCarView
    }

    override fun requestBooking(mState: BookCarState) {
        if (mBookDisposable?.isDisposed == false) {
            mBookDisposable?.dispose()
        }

        val request: BookingRequest = if (mState.bookingHourly == true) {
            BookingRequest(mCarId ?: return,
                    mState.timeBeginHourly ?: return,
                    mState.timeEndHourly ?: return,
                    mState.hourlyCurbsideDelivery.get() && mState.collectionPicked.get(),
                    mState.latitude,
                    mState.longitude,
                    mState.deliveryAddress,
                    mState.paymentSource,
                    if (mState.paymentSource == "cash") null else mState.paymentToken,
                    mState.bookingHourly?.not() ?: return,
                    mState.amountTotal ?: return,
                    mState.amountDiscount ?: 0f,
                    mState.noteText)
        } else {
            BookingRequest(mCarId ?: return,
                    mState.timeBeginDaily ?: return,
                    mState.timeEndDaily ?: return,
                    mState.dailyCurbsideDelivery.get() && mState.collectionPicked.get(),
                    mState.latitude,
                    mState.longitude,
                    mState.deliveryAddress,
                    mState.paymentSource,
                    if (mState.paymentSource == "cash") null else mState.paymentToken,
                    mState.bookingHourly?.not() ?: return,
                    mState.amountTotal ?: return,
                    mState.amountDiscount ?: 0f,
                    mState.noteText)
        }

        mBookDisposable = requestBookingCase.execute(RequestBooking.RequestValues(request), object : RxUseCase.Callback<RequestBooking.ResponseValues> {
            override fun onSuccess(response: RequestBooking.ResponseValues) {
                mView?.showMessage("Request sent")
                mView?.onRequestSuccess()
            }

            override fun onError(error: Error) {
                mView?.showMessage(error.message)
            }
        })
    }

    override fun getOffer(id: Int, state: BookCarState) {
        mCarId = id

        getDataFromFilter(state)
        getBookingOffer(id, state)
        checkVerificationState(state)
    }

    private fun getDataFromFilter(state: BookCarState) {
        val filter = getFilter.getFilter()
        if (getHourly(filter)) {
            state.timeBeginHourly = filter.rentalPeriodBegin

            val delegate = mView?.getDateDelegate()
            var endDate = delegate?.convertDateToDate(filter.rentalPeriodEnd)
            endDate = addOneHour(endDate)

            state.timeEndHourly = delegate?.formatAsIsoDate(endDate)
        } else {
            state.timeBeginDaily = filter.rentalPeriodBegin
            state.timeEndDaily = filter.rentalPeriodEnd
        }
        mView?.displayRentalPeriod()
    }

    private fun getBookingOffer(id: Int, state: BookCarState) {
        if (mGetOfferDisposable?.isDisposed == false) {
            mGetOfferDisposable?.dispose()
        }
        mGetOfferDisposable = getOfferById.execute(GetOfferById.RequestValues(id), object : RxUseCase.Callback<GetOfferById.ResponseValues> {
            override fun onSuccess(response: GetOfferById.ResponseValues) {
                val offer = response.offer ?: return
                setView(offer, state)
            }

            override fun onError(error: Error) {
                mView?.showMessage(error.message)
            }
        })
    }

    private fun checkVerificationState(state: BookCarState) {
        if (verifyDisposable?.isDisposed == false) {
            verifyDisposable?.dispose()
        }
        verifyDisposable = getVerifyState.execute(GetVerifyDetails.RequestValues(), object : RxUseCase.Callback<GetVerifyDetails.ResponseValues> {
            override fun onSuccess(response: GetVerifyDetails.ResponseValues) {
                val resp = response.verifyState
                state.accVerified.set(resp.particularsVerified &&
                        resp.identityCardVerified &&
                        resp.driverLicenceVerified &&
                        resp.driverPhotoVerified &&
                        resp.creditCardVerified &&
                        resp.depositVerified)
            }

            override fun onError(error: Error) {
                mView?.showMessage(error.message)
            }
        })
    }

    private fun setView(offer: OfferByIdResponseBody, state: BookCarState) {
        val carTitle = offer.carDetails?.carTitle
        mView?.setCarTitle(carTitle)
        val carYear = offer.carDetails?.carYear
        mView?.setCarYear(carYear)
        val hourlyCurb = offer.orderHourlyDetails?.curbsideDelivery
        val dailyCurb = offer.orderDailyDetails?.curbsideDelivery
        state.hourlyCurbsideDelivery.set(hourlyCurb ?: false)
        state.dailyCurbsideDelivery.set(dailyCurb ?: false)
        val hourlyInstant = offer.orderHourlyDetails?.instantBooking
        val dailyInstant = offer.orderDailyDetails?.instantBooking
        state.hourlyInstantBooking.set(hourlyInstant ?: false)
        state.dailyInstantBooking.set(dailyInstant ?: false)
        val acceptCashHourly = offer.orderHourlyDetails?.acceptCash
        val acceptCashDaily = offer.orderDailyDetails?.acceptCash
        state.acceptCashHourly.set(acceptCashHourly ?: false)
        state.acceptCashDaily.set(acceptCashDaily ?: false)
        state.availabilityDaily = offer.carAvailabilityDaily
        state.availabilityDailyPickup = offer.orderDailyDetails?.timePickup
        state.availabilityDailyReturn = offer.orderDailyDetails?.timeReturn
        state.availabilityHourly = offer.carAvailabilityHourly
        state.availabilityHourlyBegin = offer.carAvailabilityTimeBegin
        state.availabilityHourlyEnd = offer.carAvailabilityTimeEnd
        state.paymentByMileage.set(offer.orderHourlyDetails?.fuelPolicy?.fuelPolicyId == 0)

        mView?.resetCost()

        getCost(mCarId ?: return, state, null)
    }

    private fun getHourly(filter: BrowseCarsFilter): Boolean {
        if (filter.bookingHourly == null) {
            return true
        }
        return filter.bookingHourly == true
    }

    override fun getCost(carId: Int, state: BookCarState, context: AppCompatActivity?) {
        val request = createRequest(carId, state)

        if (mGetCostDisposable?.isDisposed == false) {
            mGetCostDisposable?.dispose()
        }

        mGetCostDisposable = getCostBreakdown.execute(
                GetCostBreakdown.RequestValues(request ?: return),
                object : RxUseCase.Callback<GetCostBreakdown.ResponseValues> {
                    override fun onSuccess(response: GetCostBreakdown.ResponseValues) {
                        mCostBreakdown = response.costBreakdown

                        parseCostBreakdown(state, context)
                    }

                    override fun onError(error: Error) {
                        mView?.showMessage(error.message)
                    }
                })
    }

    private fun createRequest(carId: Int, state: BookCarState): CostRequest? {
        var timeBegin: String?
        var timeEnd: String?
        if (state.bookingHourly == true) {
            timeBegin = state.timeBeginHourly ?: return null
            timeEnd = state.timeEndHourly ?: return null
        } else {
            timeBegin = state.timeBeginDaily ?: return null
            timeEnd = state.timeEndDaily ?: return null
        }

        var curbDel = if (state.bookingHourly == true) state.hourlyCurbsideDelivery.get() else state.dailyCurbsideDelivery.get()
        if (state.collectionPicked.get().not()) {
            curbDel = false
        }
        val latitude = state.latitude
        val longitude = state.longitude
        val bookingDaily = state.bookingHourly == false

        return CostRequest(carId, timeBegin, timeEnd, curbDel, latitude, longitude, bookingDaily)
    }

    private fun parseCostBreakdown(state: BookCarState, context: AppCompatActivity?) {
        val total: Number = mCostBreakdown?.total ?: return
        val discount: Number = mCostBreakdown?.discount ?: 0
        state.amountTotal = total.toFloat()
        state.amountDiscount = discount.toFloat()
        var totalString: String?
        totalString = if (total.toFloat() % 1f == 0f) {
            total.toInt().toString()
        } else {
            "%.2f".format(total.toFloat())
        }
        totalString = if (state.bookingHourly == true && state.paymentByMileage.get()) {
            "$$totalString++"
        } else {
            "$$totalString"
        }

        mView?.setTotalCost(totalString)
        if (showBreakdown) {
            continueShowBreakdown(context, state)
        }
    }

    override fun showCostBreakdown(context: AppCompatActivity?, state: BookCarState) {
        mCostBreakdown = null
        showBreakdown = true
        getCost(mCarId ?: return, state, context)
    }

    private fun continueShowBreakdown(context: AppCompatActivity?, state: BookCarState) {
        val builder = AlertDialog.Builder(context ?: return)
        val root = context.layoutInflater.inflate(R.layout.dialog_cost_breakdown, null)
        builder.setView(root)
        val dialog = builder.create()
        root.buttonOk.setOnClickListener { dialog.dismiss() }

        prepareDialogContent(context, state, root)

        dialog.show()
    }

    private fun prepareDialogContent(context: AppCompatActivity, state: BookCarState, root: View) {

        val nonPeakCount = (mCostBreakdown ?: return).nonPeakCount
        val nonPeakCost = mCostBreakdown?.nonPeakCost
        val peakCount = mCostBreakdown?.peakCount
        val peakCost = mCostBreakdown?.peakCost
        val delivery = mCostBreakdown?.delivery
        val fee = mCostBreakdown?.fee
        val discount = mCostBreakdown?.discount
        val total = mCostBreakdown?.total

        if (nonPeakCount == null || nonPeakCount == 0) {
            root.non_peak_container.visibility = View.GONE
        } else {
            var count = if (state.bookingHourly == true) {
                "$nonPeakCount hour"
            } else {
                "$nonPeakCount day"
            }
            if (nonPeakCount != 1) count += "s"

            root.non_peak_days.text = String.format(context.resources.getString(R.string.cost_breakdown_non_peak_template), count)
            root.non_peak_amount.text = "$%.2f".format(nonPeakCost)
        }

        if (peakCount == null || peakCount == 0) {
            root.peak_container.visibility = View.GONE
        } else {
            var count = if (state.bookingHourly == true) {
                "$peakCount hour"
            } else {
                "$peakCount day"
            }
            if (peakCount != 1) count += "s"

            root.peak_days.text = String.format(context.resources.getString(R.string.cost_breakdown_peak_template), count)
            root.peak_amount.text = "$%.2f".format(peakCost)
        }

        if (delivery == null || delivery == 0f) {
            root.delivery_container.visibility = View.GONE
        } else {
            root.delivery_amount.text = "$%.2f".format(delivery)
        }

        if (fee == null || fee == 0f) {
            root.fee_container.visibility = View.GONE
        } else {
            root.fee_amount.text = "$%.2f".format(fee)
        }

        if (discount == null || discount == 0f) {
            root.discount_container.visibility = View.GONE
        } else {
            if (discount % 1f == 0f) {
                root.discount_amount.text = "%.0f%%".format(discount)
            } else {
                root.discount_amount.text = "$discount%"
            }
        }

        root.total_amount.text = "$%.2f".format(total)
    }

    override fun getState(): BookCarState {
        return getBookState.getBookState()
    }

    override fun saveSate(state: BookCarState) {
        saveBookState.saveBookState(state)
    }

    override fun getFilter(): BrowseCarsFilter {
        return getFilter.getFilter()
    }

    override fun saveFilter(filter: BrowseCarsFilter) {
        saveFilter.saveFilter(filter)
    }

    override fun addOneHour(end: Date?): Date? {
        val calendar = Calendar.getInstance()
        calendar.time = end ?: return null
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        return calendar.time
    }

    override fun onDestroy() {
        mView = null
        mGetOfferDisposable?.dispose()
        mGetCostDisposable?.dispose()
        mCostBreakdown = null
        mCarId = null
    }
}