package com.cardee.owner_car_rental_info.terms.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cardee.R;
import com.cardee.data_source.util.DialogHelper;
import com.cardee.owner_car_rental_info.RentalContract;
import com.cardee.owner_car_rental_info.terms.presenter.RentalTermsInsurancePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RentalTermsInsuranceActivity extends AppCompatActivity implements View.OnClickListener,
        RentalContract.View {

    public final static String COMPENSATION_EXCESS = "key_compensation_excess";
    public final static String COMPENSATION_OTHER = "key_compensation_other_guidelines";

    @BindView(R.id.et_insuranceText)
    public AppCompatEditText insuranceTextET;

    private ProgressDialog mProgress;

    private RentalTermsInsurancePresenter mPresenter;

    private String mDefText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_car_rental_insurance_excess);
        initToolbar();
        ButterKnife.bind(this);
        initViewState();
        mProgress = DialogHelper
                .getProgressDialog(this, getString(R.string.loading), false);
        mPresenter = new RentalTermsInsurancePresenter(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.car_rental_terms_insurance);
        toolbar.findViewById(R.id.toolbar_action).setOnClickListener(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void initViewState() {
        mDefText = getIntent().getStringExtra(COMPENSATION_EXCESS);
        insuranceTextET.setText(mDefText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_action:
                if (insuranceTextET.getText().toString().equals(mDefText)) {
                    showMessage(R.string.nothing_to_save);
                    return;
                }
                mPresenter.save(insuranceTextET.getText().toString());
                break;
        }
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            mProgress.show();
            return;
        }
        mProgress.dismiss();
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showMessage(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        onBackPressed();
        finish();
    }
}
