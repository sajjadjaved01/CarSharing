package com.cardee.owner_bookings.car_checklist.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;


import com.cardee.R;
import com.cardee.owner_bookings.car_checklist.ChecklistContract;
import com.cardee.owner_bookings.car_checklist.OwnerChecklistPresenter;
import com.cardee.owner_bookings.car_checklist.strategy.ChecklistByMileageStrategy;
import com.cardee.owner_bookings.car_checklist.strategy.ChecklistStrategy;
import com.cardee.owner_bookings.car_checklist.strategy.RenterUpdatedChecklistStrategy;
import com.cardee.owner_bookings.view.BookingActivity;


public class OwnerChecklistActivity extends AppCompatActivity implements OwnerChecklistPresenter.View {

    private static final int IMAGE_REQUEST_CODE = 102;
    private static final int REQUEST_PERMISSION_CODE = 103;

    public final static String KEY_BOOKING_ID = "booking_id";

    private ChecklistContract.Presenter mPresenter;

    private ChecklistContract.View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChecklistView view = (ChecklistView) LayoutInflater
                .from(this).inflate(R.layout.activity_owner_handover_checklist, null);
        mPresenter = new OwnerChecklistPresenter(getIntent().getIntExtra(KEY_BOOKING_ID, -1));
        mView = view;
        mView.setPresenter(mPresenter);
        mPresenter.setView(mView);
        mPresenter.setViewCallbacks(this);
        setContentView(view);
        Toolbar toolbar = view.getToolbar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        mView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE && data != null) {
            Uri uri = data.getData();
            mPresenter.onAddNewImage(uri);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPickPhoto();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPickPhoto() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onHandover(int bookingId) {
        finish();
    }

    private boolean hasPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
