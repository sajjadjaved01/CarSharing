package com.cardee.data_source.inbox.repository;

import io.reactivex.functions.Consumer;

public interface NotificationContract {

    void fetchNotifications();

    void setRelevantChatUnreadCount(Integer readCount);

    void setRelevantAlertUnreadCount(Integer readCount);

    void updateChatUnreadCount(Integer count);

    void updateInboxUnreadCount(Integer count);


    void setCurrentChatSession(int chatId);

    void resetCurrentChatSession();

    boolean isCurrentChatSession(int chatId);

    void subscribeToNotificationUpdates(Consumer<Integer> consumer);

    void subscribeToAlertUpdates(Consumer<Boolean> consumer);

    void subscribeToChatUpdates(Consumer<Boolean> consumer);

    void saveSessionData();
}
