package com.cardee.inbox.chat.single.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.cardee.CardeeApp;
import com.cardee.R;
import com.cardee.inbox.chat.list.adapter.UtcDateFormatter;
import com.cardee.util.glide.CircleTransform;

public class ChatViewHolder implements ActivityViewHolder {

    private final RequestManager mRequestManager;
    private final UtcDateFormatter mDateFormatter;

    private TextView mRecipientName;
    private ImageView mRecipientPhoto;
    private ImageView mCarPhoto;
    private TextView mCarTitle;
    private TextView mCarLicenseNumber;
    private TextView mCarAvailability;


    public ChatViewHolder(View rootView) {
        mRequestManager = Glide.with(rootView.getContext());
        mDateFormatter = new ChatActivityDateFormatter(rootView.getContext());

        mRecipientName = rootView.findViewById(R.id.toolbar_title);
        mRecipientPhoto = rootView.findViewById(R.id.toolbar_photo);
        mCarPhoto = rootView.findViewById(R.id.car_photo);
        mCarTitle = rootView.findViewById(R.id.car_title);
        mCarLicenseNumber = rootView.findViewById(R.id.car_licence_plate_number);
        mCarAvailability = rootView.findViewById(R.id.car_availability);
    }

    @Override
    public void setUserData(String recipientName, String photoUrl) {
        mRecipientName.setText(recipientName);
        mRequestManager
                .load(photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_photo_placeholder)
                .transform(new CircleTransform(CardeeApp.context))
                .into(mRecipientPhoto);
    }

    @Override
    public void setCarData(String carPhotoUrl, String carTitle, String licenseNumber) {
        mRequestManager
                .load(carPhotoUrl)
                .placeholder(R.drawable.img_no_car)
                .centerCrop()
                .into(mCarPhoto);

        mCarTitle.setText(carTitle);
        mCarLicenseNumber.setText(licenseNumber);
    }

    @Override
    public void setCarBookingData(String mStartDate, String mEndDate) {
        String availability = mDateFormatter.formatDate(mStartDate) + mDateFormatter.getDivider() + mDateFormatter.formatDate(mEndDate);
        mCarAvailability.setText(availability);
    }
}