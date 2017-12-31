package com.cardee.data_source.inbox.repository;

import com.cardee.data_source.inbox.local.chat.entity.ChatMessage;
import com.cardee.domain.inbox.usecase.entity.ChatInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface ChatContract {

    void sendChatIdentifier(Integer chatId, String attachment);

    Single<ChatInfo> getChatInfo();

    Flowable<List<ChatMessage>> getLocalMessages();

    Completable getRemoteMessages();

    void removeChatUnreadStatus(int chatId);

    void sendMessage(String message);

    Single<List<ChatMessage>> getNewChat();

    void addNewMessage(String message);

}
