package com.cardee.owner_profile_info.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cardee.R;
import com.cardee.domain.owner.entity.Car;
import com.cardee.domain.owner.entity.CarReview;
import com.cardee.owner_car_details.OwnerCarDetailsContract;
import com.cardee.owner_car_details.view.OwnerCarDetailsActivity;
import com.cardee.owner_profile_info.presenter.OwnerProfileInfoPresenter;
import com.cardee.owner_profile_info.view.adapter.CarPreviewListAdapter;
import com.cardee.owner_profile_info.view.adapter.ReviewListAdapter;
import com.cardee.util.display.ActivityHelper;
import com.cardee.util.glide.CircleTransform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerProfileInfoActivity extends AppCompatActivity implements ProfileInfoView {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static int MAX_IMAGE_DIMENSION = 720;

    public final static String TAG = OwnerProfileInfoActivity.class.getCanonicalName();
    private OwnerProfileInfoPresenter mPresenter;
    private CarPreviewListAdapter mCarsAdapter;
    private ReviewListAdapter mReviewAdapter;
    private Toast mCurrentToast;
    private byte[] mPictureByteArray;
    private boolean mEditable;
    private int mProfileId;

    @BindView(R.id.profile_info_container)
    View mContainer;

    @BindView(R.id.profile_name)
    TextView mProfileNameText;

    @BindView(R.id.profile_rating)
    TextView mProfileRatingText;

    @BindView(R.id.acceptance_percentage)
    TextView mProfileAcceptance;

    @BindView(R.id.response_time)
    TextView mProfileResponse;

    @BindView(R.id.response_minutes)
    TextView mResponseMins;

    @BindView(R.id.bookings_count)
    TextView mProfileBookings;

    @BindView(R.id.profile_image)
    ImageView mProfilePhoto;

    @BindView(R.id.profile_image_edit)
    TextView mProfilePhotoEdit;

    @BindView(R.id.note_edit)
    TextView mNoteEdit;

    @BindView(R.id.note_text)
    TextView mNoteText;

    @BindView(R.id.note_title)
    TextView mNoteTitle;

    @BindView(R.id.cars_count)
    TextView mCarsCount;

    @BindView(R.id.reviews_count)
    TextView mReviewsCount;

    @BindView(R.id.cars_list)
    RecyclerView mCarsListView;

    @BindView(R.id.cars_layout)
    CardView mCarsCard;

    @BindView(R.id.reviews_list)
    RecyclerView mReviewsListView;

    @BindView(R.id.owner_info_progress)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile_info);
        ButterKnife.bind(this);

        getIntentExtras();
        initPresenter();
        initToolBar();
        initAdapters();
        initCarList(mCarsListView);
        initReviewList(mReviewsListView);

        if (mEditable) {
            mPresenter.getOwnerInfo();
        } else {
            if (mProfileId != -1) {
                mPresenter.getOwnerById(mProfileId);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initEditableState();
        initPhotoEditButton();
    }

    private void initEditableState() {
        if (mEditable) {
            mNoteEdit.setVisibility(View.VISIBLE);
            mNoteEdit.setOnClickListener(view -> mPresenter.changeNote(OwnerProfileInfoActivity.this));
            mCarsCard.setVisibility(View.VISIBLE);
        } else {
            mNoteEdit.setOnClickListener(null);
            mNoteEdit.setVisibility(View.GONE);
            mCarsCard.setVisibility(View.GONE);
        }
    }

    private void initPhotoEditButton() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (mEditable) {
                mProfilePhotoEdit.setVisibility(View.VISIBLE);
                mProfilePhotoEdit.setOnClickListener(view -> verifyPermissionAndChangeImage());
            } else {
                mProfilePhotoEdit.setOnClickListener(null);
                mProfilePhotoEdit.setVisibility(View.GONE);
            }
        }, 500);

    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        mEditable = intent.getBooleanExtra("editable", false);
        mProfileId = intent.getIntExtra("profile_id", -1);
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

    }

    private void verifyPermissionAndChangeImage() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        } else {
            ActivityHelper.pickImageIntent(OwnerProfileInfoActivity.this, ActivityHelper.PICK_IMAGE);
        }
    }

    private void initAdapters() {
        mCarsAdapter = new CarPreviewListAdapter(this);
        mReviewAdapter = new ReviewListAdapter(this);
        if (mEditable) {
            mCarsAdapter.subscribe(mPresenter);
        }
    }

    private void initReviewList(RecyclerView mReviewsListView) {
        mReviewsListView.setAdapter(mReviewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsListView.setLayoutManager(layoutManager);
        mReviewsListView.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mReviewsListView.getContext(),
                LinearLayoutManager.VERTICAL);
        mReviewsListView.addItemDecoration(dividerItemDecoration);
    }

    private void initCarList(RecyclerView mCarsListView) {
        mCarsListView.setHasFixedSize(true);
        mCarsListView.setAdapter(mCarsAdapter);
        mCarsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initPresenter() {
        mPresenter = new OwnerProfileInfoPresenter(this);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            mContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        mContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    @Override
    public void showMessage(int messageId) {
        showMessage(getString(messageId));
    }

    @Override
    public void setProfileName(String name) {
        mProfileNameText.setText(name);
    }

    @Override
    public void setProfileRating(String rating) {
        mProfileRatingText.setText(rating);
    }

    @Override
    public void setAcceptance(String percent) {
        mProfileAcceptance.setText(percent);
    }

    @Override
    public void setResponseText(String time) {
        mProfileResponse.setText(time);
    }

    @Override
    public void setBookings(String count) {
        mProfileBookings.setText(count);
    }

    @Override
    public void setProfileImage(String photoLink) {
        if (!this.isDestroyed()) {
            Glide.with(this)
                    .load(photoLink)
                    .placeholder(getResources().getDrawable(R.drawable.ic_photo_placeholder))
                    .error(getResources().getDrawable(R.drawable.ic_photo_placeholder))
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .into(mProfilePhoto);
        }
    }

    public void setProfileImage(byte[] pictureByteArray) {
        if (!this.isDestroyed()) {
            Glide.with(this)
                    .load(pictureByteArray)
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .into(mProfilePhoto);
        }
    }

    @Override
    public void setNote(String note) {
        mNoteText.setText(note);
    }

    @Override
    public void setCarsCount(String text) {
        mCarsCount.setText(text);
    }

    @Override
    public void setReviewsCount(String text) {
        mReviewsCount.setText(text);
    }

    @Override
    public void setCars(List<Car> items) {
        mCarsAdapter.insert(items);
    }

    @Override
    public void setCarReviews(List<CarReview> reviews) {
        mReviewAdapter.insert(reviews);
    }

    @Override
    public void openItem(Car car) {
        Intent intent = new Intent(this, OwnerCarDetailsActivity.class);
        Bundle args = new Bundle();
        args.putInt(OwnerCarDetailsContract.CAR_ID, car.getCarId() == null ? -1 : car.getCarId());
        args.putString(OwnerCarDetailsContract.CAR_NUMBER, car.getLicenceNumber());
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void setMinutes(String minutes) {
        mResponseMins.setText(minutes);
    }

    @Override
    public void setNoteTitle(String address) {
        mNoteTitle.setText(address);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityHelper.pickImageIntent(OwnerProfileInfoActivity.this, ActivityHelper.PICK_IMAGE);
                }
                break;
        }
    }

    @Override
    public void onChangeImageSuccess() {
        if (mPictureByteArray != null) {
            setProfileImage(mPictureByteArray);
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityHelper.PICK_IMAGE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    int width = bitmap.getWidth();
                    if (width > MAX_IMAGE_DIMENSION) {
                        float ratio = (float) MAX_IMAGE_DIMENSION / (float) width;

                        width = MAX_IMAGE_DIMENSION;
                        int height = (int) ((float) bitmap.getHeight() * ratio);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    byte[] pictureByteArray = stream.toByteArray();
                    mPictureByteArray = pictureByteArray;
                    mPresenter.setProfilePicture(convertByteArrayToFile(pictureByteArray));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File convertByteArrayToFile(byte[] byteArray) {
        if (byteArray != null) {
            FileOutputStream fos;
            File f = null;
            try {
                f = new File(this.getCacheDir(), "picture");
                fos = new FileOutputStream(f);
                fos.write(byteArray);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReviewAdapter.destroy();
        mCarsAdapter.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
