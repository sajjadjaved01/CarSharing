package com.cardee.owner_bookings.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cardee.R;
import com.cardee.domain.bookings.entity.Booking;
import com.cardee.owner_bookings.OwnerBookingContract;
import com.cardee.util.glide.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingView extends CoordinatorLayout implements OwnerBookingContract.View {

    private OwnerBookingContract.Presenter presenter;
    private Toast currentToast;
    private Unbinder unbinder;
    private RequestManager imageManager;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    public TextView toolbarTitle;
    @BindView(R.id.booking_status)
    public TextView bookingStatus;
    @BindView(R.id.booking_payment)
    public TextView bookingPayment;
    @BindView(R.id.booking_car_picture)
    public ImageView carPhotoView;
    @BindView(R.id.booking_progress)
    public ProgressBar imgProgress;
    @BindView(R.id.booking_car_title)
    public TextView carTitle;
    @BindView(R.id.booking_car_year)
    public TextView carYear;
    @BindView(R.id.booking_car_plate_number)
    public TextView carPlateNumber;
    @BindView(R.id.booking_date_created)
    public TextView bookingCreated;
    @BindView(R.id.rental_period_title)
    public TextView rentalPeriodTitle;
    @BindView(R.id.rental_period)
    public TextView rentalPeriod;
    @BindView(R.id.deliver_to_title)
    public TextView deliverToTitle;
    @BindView(R.id.deliver_to)
    public TextView deliverTo;
    @BindView(R.id.handover_on_title)
    public TextView handoverOnTitle;
    @BindView(R.id.handover_on)
    public TextView handoverOn;
    @BindView(R.id.return_by_title)
    public TextView returnByTitle;
    @BindView(R.id.return_by)
    public TextView returnBy;
    @BindView(R.id.handover_at_title)
    public TextView handoverAtTitle;
    @BindView(R.id.handover_at)
    public TextView handoverAt;
    @BindView(R.id.total_cost_title)
    public TextView totalCostTitle;
    @BindView(R.id.total_cost)
    public TextView totalCost;
    @BindView(R.id.renter_photo)
    public ImageView renterPhoto;
    @BindView(R.id.renter_name)
    public TextView renterName;
    @BindView(R.id.renter_message)
    public TextView renterMessage;
    @BindView(R.id.renter_call_title)
    public TextView renterCallTitle;
    @BindView(R.id.renter_call)
    public ImageView renterCall;
    @BindView(R.id.renter_chat_title)
    public TextView renterChatTitle;
    @BindView(R.id.renter_chat)
    public ImageView renterChat;
    @BindView(R.id.btn_cancel)
    public TextView btnCancel;
    @BindView(R.id.btn_accept)
    public TextView btnAccept;
    @BindView(R.id.booking_accept_message)
    public TextView acceptMessage;
    @BindView(R.id.booking_cancel_message)
    public TextView cancelMessage;
    @BindView(R.id.booking_loading_indicator)
    public View progressView;

    public BookingView(Context context) {
        super(context);
    }

    public BookingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this, this);
        imageManager = Glide.with(getContext());
    }

    @Override
    public void showProgress(boolean show) {
        progressView.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void showMessage(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    @Override
    public void showMessage(int messageId) {
        showMessage(getContext().getString(messageId));
    }

    @Override
    public void setPresenter(OwnerBookingContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bind(Booking booking) {
        toolbarTitle.setText(booking.getBookingNum());
        loadImageIntoView(booking.getPrimaryImage().getLink(),
                R.drawable.img_no_car, carPhotoView, imgProgress, false);
        carTitle.setText(booking.getCarTitle());
        carYear.setText(booking.getManufactureYear());
        carPlateNumber.setText(booking.getPlateNumber());
        String timeBegin = booking.getTimeBegin();
        String timeEnd = booking.getTimeEnd();
        String timePeriod = null;
        bookingCreated.setText(booking.getDateCreated());
        if (timeBegin != null && timeEnd != null) {
            timePeriod = timeBegin + " to " + timeEnd;
        }
        rentalPeriod.setText(timePeriod);
        String amountString = booking.getTotalAmount() == null ? "$0" : "$" + booking.getTotalAmount();
        totalCost.setText(amountString);
        handoverOn.setText(timeBegin);
        returnBy.setText(timeEnd);
        handoverAt.setText(booking.getDeliveryAddress());
        renterMessage.setText(booking.getNote());
        renterName.setText(booking.getRenterName());
        loadImageIntoView(booking.getRenterPhoto(),
                R.drawable.placeholder_user_img, renterPhoto, null, true);
    }

    private void loadImageIntoView(String imgUrl, int placeholderRes, ImageView view, ProgressBar progress, boolean circle) {
        imgUrl = imgUrl == null ? "" : imgUrl;
        if (progress != null) {
            progress.setVisibility(VISIBLE);
        }
        DrawableRequestBuilder<String> builder = imageManager
                .load(imgUrl).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        if (progress != null) {
                            progress.setVisibility(GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (progress != null) {
                            progress.setVisibility(GONE);
                        }
                        return false;
                    }
                }).error(placeholderRes);
        if (circle) {
            builder.transform(new CircleTransform(getContext()));
        }
        builder.into(view);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        presenter = null;
    }
}
