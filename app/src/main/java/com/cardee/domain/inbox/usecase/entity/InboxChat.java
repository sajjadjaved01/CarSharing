package com.cardee.domain.inbox.usecase.entity;

public class InboxChat {

    private Integer mChatId;

    private Integer mUnreadMessageCount;

    private String mRecipientName;

    private String mRecipientPhotoUrl;

    private String mLastMessageText;

    private String mLastMessageTime;

    public InboxChat() {
    }

    public Integer getChatId() {
        return mChatId;
    }

    public Integer getUnreadMessageCount() {
        return mUnreadMessageCount;
    }

    public String getRecipientName() {
        return mRecipientName;
    }

    public String getRecipientPhotoUrl() {
        return mRecipientPhotoUrl;
    }

    public String getLastMessageText() {
        return mLastMessageText;
    }

    public String getLastMessageTime() {
        return mLastMessageTime;
    }

    private static class Builder {

        private InboxChat mInboxChat;

        public Builder() {
            mInboxChat = new InboxChat();
        }

        public Builder withChatId(Integer chatId) {
            mInboxChat.mChatId = chatId;
            return this;
        }

        public Builder withUnreadMessageCount(Integer count) {
            mInboxChat.mUnreadMessageCount = count;
            return this;
        }

        public Builder withName(String name) {
            mInboxChat.mRecipientName = name;
            return this;
        }

        public Builder withPhotoUrl(String url) {
            mInboxChat.mRecipientPhotoUrl = url;
            return this;
        }

        public Builder withLastMessage(String message) {
            mInboxChat.mLastMessageText = message;
            return this;
        }

        public Builder withLastMessageTime(String time) {
            mInboxChat.mLastMessageTime = time;
            return this;
        }

        public InboxChat build() {
            return mInboxChat;
        }
    }
}
