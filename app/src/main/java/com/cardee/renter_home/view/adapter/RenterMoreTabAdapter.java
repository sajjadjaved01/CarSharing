package com.cardee.renter_home.view.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardee.BuildConfig;
import com.cardee.R;
import com.cardee.renter_home.view.RenterProfileContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class RenterMoreTabAdapter extends RecyclerView.Adapter<RenterMoreTabAdapter.MoreTabItemViewHolder> {

    private final List<String> mMenuNames;
    private final List<Integer> mMenuIcons;
    private final LayoutInflater mInflater;
    private final String mCardee;
    private final String mVersionString;

    private final PublishSubject<RenterProfileContract.Action> mEventObservable;

    public RenterMoreTabAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuNames = Arrays.asList(context.getResources().getStringArray(R.array.renter_more_tab_menu));
        mMenuIcons = new ArrayList<>();
        TypedArray array = context.getResources().obtainTypedArray(R.array.renter_more_tab_icons);
        for (int i = 0; i < array.length(); i++) {
            mMenuIcons.add(array.getResourceId(i, -1));
        }
        array.recycle();

        mEventObservable = PublishSubject.create();
        mCardee = context.getResources().getString(R.string.more_tab_cardee);
        mVersionString = context.getResources().getString(R.string.more_tab_version);
    }

    @Override
    public MoreTabItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_list_owner_more_tab, parent, false);
        return new MoreTabItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MoreTabItemViewHolder holder, int position) {
        String text = mMenuNames.get(position);
        holder.setText(text);
        holder.setOnClickListener(mEventObservable, position);

        holder.setIcon(mMenuIcons.get(position));
        if (text.equals(mCardee)) {
            String versionCode = BuildConfig.VERSION_NAME;
            String version = mVersionString + " " + versionCode;
            holder.setItemInfo(version);
        } else {
            holder.setItemInfo("");
        }
    }

    @Override
    public int getItemCount() {
        return mMenuNames.size();
    }

    public static class MoreTabItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mIcon;
        private final TextView mText;
        private final TextView mItemInfo;
        private final View rootView;

        public MoreTabItemViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            mIcon = itemView.findViewById(R.id.item_icon);
            mText = itemView.findViewById(R.id.item_text);
            mItemInfo = itemView.findViewById(R.id.item_info);
        }

        private void setIcon(@DrawableRes int icon) {
            mIcon.setImageResource(icon);
        }

        private void setText(String text) {
            mText.setText(text);
        }

        private void setItemInfo(String info) {
            mItemInfo.setText(info);
        }

        private void setOnClickListener(final PublishSubject<RenterProfileContract.Action> observable, final int position) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (position) {
                        case 0:
                            observable.onNext(RenterProfileContract.Action.ACCOUNT_CLICKED);
                            break;
                        case 1:
                            observable.onNext(RenterProfileContract.Action.SETTINGS_CLICKED);
                            break;
                        case 2:
                            observable.onNext(RenterProfileContract.Action.CHAT_CLICKED);
                            break;
                        case 3:
                            observable.onNext(RenterProfileContract.Action.INVITE_CLICKED);
                            break;
                        case 4:
                            observable.onNext(RenterProfileContract.Action.CARDEE_CLICKED);
                            break;
                        case 5:
                            observable.onNext(RenterProfileContract.Action.SWITCH_CLICKED);
                            break;
                    }
                }
            });
        }
    }

    public void subscribe(Consumer<RenterProfileContract.Action> consumer) {
        mEventObservable.subscribe(consumer);
    }
}
