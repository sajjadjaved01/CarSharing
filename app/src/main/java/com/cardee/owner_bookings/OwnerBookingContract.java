package com.cardee.owner_bookings;


import com.cardee.mvp.BasePresenter;
import com.cardee.mvp.BaseView;

public interface OwnerBookingContract {

    String BOOKING_ID = "_booking_id";
    String BOOKING_STATE = "_booking_state";

    interface View extends BaseView {

        void setPresenter(Presenter presenter);

        void onDestroy();

    }

    interface Presenter extends BasePresenter {

        void setView(View view);

        void onDestroy();

    }

}