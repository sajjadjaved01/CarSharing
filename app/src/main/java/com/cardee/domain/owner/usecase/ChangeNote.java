package com.cardee.domain.owner.usecase;

import com.cardee.data_source.Error;
import com.cardee.data_source.OwnerProfileDataSource;
import com.cardee.data_source.remote.RemoteOwnerProfileDataSource;
import com.cardee.data_source.remote.api.profile.request.OwnerNoteRequest;
import com.cardee.domain.UseCase;

public class ChangeNote implements UseCase<ChangeNote.RequestValues, ChangeNote.ResponseValues> {
    private final OwnerProfileDataSource mRepository;

    public ChangeNote() {
        mRepository = RemoteOwnerProfileDataSource.getInstance();
    }

    @Override
    public void execute(ChangeNote.RequestValues values, final Callback<ChangeNote.ResponseValues> callback) {
        mRepository.changeNote(values.getNewNote(), new OwnerProfileDataSource.OnChangeNoteCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess(new ResponseValues());
            }

            @Override
            public void onError(Error error) {
                callback.onError(error);
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {
        OwnerNoteRequest mNoteRequest;

        public RequestValues(OwnerNoteRequest ownerNoteRequest) {
            mNoteRequest = ownerNoteRequest;
        }

        public OwnerNoteRequest getNewNote() {
            return mNoteRequest;
        }
    }

    public static class ResponseValues implements UseCase.ResponseValues {
    }
}
