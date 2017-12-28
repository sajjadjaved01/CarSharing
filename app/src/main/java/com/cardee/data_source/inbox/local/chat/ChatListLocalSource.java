package com.cardee.data_source.inbox.local.chat;

import android.util.Log;

import com.cardee.CardeeApp;
import com.cardee.data_source.inbox.local.chat.entity.Chat;
import com.cardee.data_source.inbox.local.db.LocalInboxDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatListLocalSource implements LocalData.ChatListSource {

    private static final String TAG = ChatListLocalSource.class.getSimpleName();
    private final LocalInboxDatabase mDataBase;

    public ChatListLocalSource() {
        mDataBase = LocalInboxDatabase.getInstance(CardeeApp.context);
    }

    @Override
    public Flowable<List<Chat>> getLocalChats(String attachment) {
        return mDataBase.getChatDao()
                .getChats(attachment);
    }

    @Override
    public void addChat(Chat chat) {
        Completable
                .fromRunnable(() -> mDataBase.getChatDao().addChat(chat))
                .doOnComplete(() -> Log.d(TAG, "Chat added: " + chat.getRecipientName() + " " + chat.getLastMessageText()))
                .doOnError(throwable -> Log.e(TAG, throwable.toString()))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Single<Chat> getChat(Chat chat) {
        return mDataBase.getChatDao()
                .getChat(chat.getChatServerId(), chat.getChatAttachment())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void fetchUpdates(List<Chat> oldChatList, List<Chat> newChatList) {
        Completable.fromRunnable(() -> {
            List<Chat> listToUpdate = new ArrayList<>();
            for (Chat remoteChat : newChatList) {
                if (!oldChatList.contains(remoteChat)) {
                    int position = oldChatList.indexOf(remoteChat);
                    if (isExistChat(position)) {
                        remoteChat.setChatLocalId(oldChatList.get(position).getChatLocalId());
                    }
                    listToUpdate.add(remoteChat);
                }
            }
            if (!listToUpdate.isEmpty()) {
                saveChats(listToUpdate);
            }
        }).subscribeOn(Schedulers.io()).subscribe(() -> Log.e(TAG, "Starting fetch data..."), throwable -> Log.e(TAG, throwable.getMessage()));
    }

    private boolean isExistChat(int position) {
        return position != -1;
    }

    @Override
    public void saveChats(List<Chat> chats) {
        Completable
                .fromRunnable(() -> mDataBase.getChatDao().addChats(chats))
                .doOnError(throwable -> Log.e(TAG, throwable.getMessage()))
                .doOnComplete(() -> Log.e(TAG, "All chats persist"))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void updateChats(List<Chat> chats) {
        Completable
                .fromRunnable(() -> mDataBase.getChatDao().updateChats(chats))
                .doOnComplete(() -> Log.d(TAG, "Chat updated"))
                .doOnError(throwable -> Log.e(TAG, throwable.toString()))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}