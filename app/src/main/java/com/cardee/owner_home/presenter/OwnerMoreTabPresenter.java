package com.cardee.owner_home.presenter;


import android.view.View;

import com.cardee.data_source.Error;
import com.cardee.data_source.remote.api.profile.response.entity.OwnerProfile;
import com.cardee.domain.UseCase;
import com.cardee.domain.UseCaseExecutor;
import com.cardee.domain.owner.usecase.GetOwnerInfo;
import com.cardee.owner_home.OwnerProfileContract;

import io.reactivex.functions.Consumer;

public class OwnerMoreTabPresenter implements Consumer<OwnerProfileContract.Action> {

    private OwnerProfileContract.View mView;

    private final UseCaseExecutor mExecutor;
    private final GetOwnerInfo mGetInfoUseCase;

    public OwnerMoreTabPresenter(OwnerProfileContract.View view) {
        mView = view;
        mExecutor = UseCaseExecutor.getInstance();
        mGetInfoUseCase = new GetOwnerInfo();
    }

    public void loadProfile() {
        mView.showProgress(true);
        mExecutor.execute(mGetInfoUseCase, null, new UseCase.Callback<GetOwnerInfo.ResponseValues>() {

            @Override
            public void onSuccess(GetOwnerInfo.ResponseValues response) {
                mView.showProgress(false);
                if (mView != null) {
                    OwnerProfile profile = response.getOwnerProfile();
                    if (profile != null) {
                        mView.setProfileImage(profile.getProfilePhotoLink());
                        mView.setProfileName(profile.getName());
                    }
                }
            }

            @Override
            public void onError(Error error) {
                mView.showProgress(false);
                mView.showMessage(error.getMessage());
            }
        });
    }

    public void setOnClickListenerToHeader(View header) {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.openOwnerProfile();
            }
        });
    }

    @Override
    public void accept(OwnerProfileContract.Action action) throws Exception {
        switch (action) {
            case ACCOUNT_CLICKED:
                mView.openAccount();
                break;
            case SETTINGS_CLICKED:
                mView.openSettings();
                break;
            case CREDIT_CLICKED:
                mView.openCreditBalance();
                break;
            case CHAT_CLICKED:
                mView.openLiveChat();
                break;
            case INVITE_CLICKED:
                mView.openInviteFriends();
                break;
            case CARDEE_CLICKED:
                mView.openCardee();
                break;
            case SWITCH_CLICKED:
                mView.openSwitchToRenter();
                break;
        }
    }
}
