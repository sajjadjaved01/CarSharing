package com.cardee.data_source.inbox.repository;

import com.cardee.data_source.inbox.local.chat.ChatItemLocalSource;
import com.cardee.data_source.inbox.local.chat.LocalData;
import com.cardee.data_source.inbox.local.chat.entity.ChatMessage;
import com.cardee.data_source.inbox.remote.chat.ChatItemRemoteSource;
import com.cardee.data_source.inbox.remote.chat.RemoteData;
import com.cardee.domain.inbox.usecase.entity.ChatInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class ChatRepository implements ChatContract {

    private static ChatRepository INSTANCE;

    private final LocalData.ChatSingleSource mLocalSource;
    private final RemoteData.ChatSingleSource mRemoteSource;

    private int serverId;
    private int databaseId;
    private String attachment;

    public static ChatRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatRepository();
        }
        return INSTANCE;
    }

    private ChatRepository() {
        mLocalSource = new ChatItemLocalSource();
        mRemoteSource = new ChatItemRemoteSource();
    }

    @Override
    public void sendChatIdentifier(Integer serverId, Integer databaseId, String attachment) {
        this.serverId = serverId;
        this.databaseId = databaseId;
        this.attachment = attachment;
    }

    @Override
    public Single<ChatInfo> getChatInfo() {
        return mLocalSource.getChatInfo(databaseId, serverId);
    }

    @Override
    public Flowable<List<ChatMessage>> getMessages() {
        Flowable<List<ChatMessage>> localFlowable = mLocalSource.getMessages(databaseId);
        Single<List<ChatMessage>> remoteSingle = mRemoteSource.getMessages(databaseId, serverId);
        //test returning type
        return remoteSingle.toFlowable();
    }

    @Override
    public void addNewMessage(ChatMessage chatMessage) {
        mLocalSource.addNewMessage(chatMessage);
    }

    @Override
    public Completable sendMessage(String message) {
        return mRemoteSource.sendMessage(message);
    }

    @Override
    public Completable markAsRead(int messageId) {
        return mRemoteSource.markAsRead(messageId);
    }
}