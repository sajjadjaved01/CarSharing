package com.cardee.owner_bookings.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cardee.R;
import com.cardee.custom.modal.FilterBookingDialog;
import com.cardee.custom.modal.SortBookingDialog;
import com.cardee.owner_bookings.BookingListAdapter;
import com.cardee.owner_bookings.OwnerBookingListContract;
import com.cardee.owner_bookings.presenter.OwnerBookingListPresenter;

public class BookingListFragment extends Fragment
        implements View.OnClickListener, OwnerBookingListContract.View {

    private OwnerBookingListContract.Presenter presenter;
    private BookingListAdapter adapter;
    private RecyclerView bookingList;
    private View progressLayout;
    private Toast currentToast;

    public static Fragment newInstance() {
        return new BookingListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new OwnerBookingListPresenter(this);
        adapter = new BookingListAdapter(presenter, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_owner_booking_list, container, false);
        bookingList = rootView.findViewById(R.id.booking_list);
        bookingList.setAdapter(adapter);
        bookingList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        bookingList.setItemAnimator(new DefaultItemAnimator());
        progressLayout = rootView.findViewById(R.id.progress_layout);
        rootView.findViewById(R.id.btn_open_filter).setOnClickListener(this);
        rootView.findViewById(R.id.btn_open_sort).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_filter:
                FilterBookingDialog filterDialog = FilterBookingDialog.getInstance(null);
                filterDialog.show(getActivity().getSupportFragmentManager(), filterDialog.getTag());
                break;
            case R.id.btn_open_sort:
                SortBookingDialog sortDialog = SortBookingDialog.getInstance(null);
                sortDialog.show(getActivity().getSupportFragmentManager(), sortDialog.getTag());
                break;
        }
    }

    @Override
    public void showProgress(boolean show) {
        progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        bookingList.setAlpha(show ? .5f : 1f);
    }

    @Override
    public void showMessage(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    @Override
    public void showMessage(int messageId) {
        showMessage(getString(messageId));
    }

    @Override
    public void invalidate() {
        adapter.notifyDataSetChanged();
    }
}