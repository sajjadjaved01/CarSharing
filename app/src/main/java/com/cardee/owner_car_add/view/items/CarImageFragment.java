package com.cardee.owner_car_add.view.items;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cardee.R;
import com.cardee.owner_car_add.view.NewCarFormsContract;
import com.cardee.owner_car_details.view.listener.DetailsChangedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CarImageFragment extends Fragment {

    private DetailsChangedListener parentListener;

    private UploadImageListener mUploadListener;

    private Unbinder mUnbinder;

    private CarPresenterImpl mPresenter;

    @BindView(R.id.iv_addCarImage)
    public ImageView addCarImage;

    public static Fragment newInstance() {
        Fragment fragment = new CarImageFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsChangedListener) {
            parentListener = (DetailsChangedListener) context;
        }
        if (context instanceof UploadImageListener) {
            mUploadListener = (UploadImageListener) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DetailsChangedListener) {
            parentListener = (DetailsChangedListener) activity;
        }
        if (activity instanceof UploadImageListener) {
            mUploadListener = (UploadImageListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_car_image, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        mPresenter = new CarPresenterImpl(getActivity());
        return v;
    }

    @OnClick(R.id.fl_addCarUploadImage)
    public void onUploadClicked() {
        mUploadListener.onImageUpload();
    }

    public void setUserPhoto(Bitmap bmp) {
        mPresenter.saveCarImageToCache(bmp);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Glide.with(getActivity())
                .load(stream.toByteArray())
                .placeholder(R.drawable.placeholder_user_img)
                .into(addCarImage);
    }

    @Override
    public void onStart() {
        super.onStart();
        parentListener.onModeDisplayed(NewCarFormsContract.Mode.IMAGE);

        if (mPresenter == null)
            return;

        File picFile = mPresenter.getCarImageFromCache();
        if (picFile == null)
            return;

        Glide.with(getActivity())
                .load(picFile)
                .placeholder(R.drawable.placeholder_user_img)
                .into(addCarImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
