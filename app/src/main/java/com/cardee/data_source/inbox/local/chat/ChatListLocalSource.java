package com.cardee.data_source.inbox.local.chat;

import android.util.Log;

import com.cardee.CardeeApp;
import com.cardee.data_source.inbox.local.chat.entity.Chat;
import com.cardee.data_source.inbox.local.db.LocalInboxDatabase;
import com.cardee.data_source.inbox.service.model.ChatNotification;

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
    public Completable addChat(Chat chat) {
        return Completable
                .fromRunnable(() -> mDataBase.getChatDao().addChat(chat))
                .doOnComplete(() -> Log.d(TAG, "Chat added: " + chat.getRecipientName() + " " + chat.getLastMessageText()))
                .doOnError(throwable -> Log.e(TAG, throwable.toString()));
    }

    @Override
    public Completable updateChat(ChatNotification chatNotification) {
        int unreadMessages = chatNotification.isCurrentSession() ? 0 : chatNotification.getUnreadMessageCount();
        return Completable.create(emitter -> {
            long result = mDataBase.getChatDao()
                    .updateChatPresentation(
                            chatNotification.getMessageText(),
                            chatNotification.getMessageTime(),
                            unreadMessages,
                            chatNotification.getChatId(),
                            chatNotification.getChatAttachment());
            if (result != 0) {
                emitter.onComplete();
            } else {
                emitter.onError(new Throwable("Local chat doesn't exist"));
            }
        });
    }

    @Override
    public Single<Chat> getChat(Chat chat) {
        return mDataBase.getChatDao()
                .getChat(chat.getChatId(), chat.getChatAttachment())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
}
