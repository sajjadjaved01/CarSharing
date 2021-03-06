package com.cardee.auth.register.view;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.cardee.CardeeApp;
import com.cardee.R;
import com.cardee.auth.login.view.LoginActivity;
import com.cardee.auth.register.presenter.RegisterPresenter;
import com.cardee.data_source.remote.api.auth.request.SocialLoginRequest;
import com.cardee.data_source.remote.service.AccountManager;
import com.cardee.data_source.util.DialogHelper;
import com.cardee.owner_home.view.OwnerHomeActivity;
import com.cardee.renter_home.view.RenterHomeActivity;
import com.cardee.util.display.ActivityHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

import java.io.File;
import java.util.Arrays;

import static com.cardee.data_source.remote.service.AccountManager.OWNER_SESSION;
import static com.cardee.data_source.remote.service.AccountManager.RENTER_SESSION;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    //    private final int PICK_IMAGE = 1;
    private static final int RC_SIGN_IN = 9001;
    private final int CROP_IMAGE = 2;

    private RegisterPresenter mPresenter;

    private RegisterFirstStepFragment mFirstStepFragment;
    private RegisterFinalStepFragment mFinalStepFragment;
    private FragmentManager mFragmentManager;

    private CallbackManager mFacebookCM;

    private LoginButton mButtonFacebook;

    private ProgressDialog mProgress;

    private String mLogin, mPass, mName;
    private LoginResult mLoginResult;

    private GoogleApiClient mGoogleClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initFragments();
        initFacebookButton();
        mProgress = DialogHelper.getProgressDialog(this, getString(R.string.loading), false);
        initGoogleApi();
    }

    private void initFragments() {
        mPresenter = new RegisterPresenter(this);

        mFragmentManager = getSupportFragmentManager();

        mFirstStepFragment = new RegisterFirstStepFragment();
        mFirstStepFragment.setViewListener(this);

        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFirstStepFragment)
                .commit();
    }

    private void initFacebookButton() {
        mFacebookCM = CallbackManager.Factory.create();
        mButtonFacebook = new LoginButton(this);
        mButtonFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));
        mButtonFacebook.registerCallback(mFacebookCM, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mLoginResult = loginResult;
                setFacebookData(loginResult);
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                showMessage(error.getMessage());
                Log.d("REGISTER FACEBOOK", error.getMessage());
            }
        });
    }

    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    try {
                        String email = response.getJSONObject().getString("email");
                        String firstName = response.getJSONObject().getString("first_name");

                        Log.i("Login" + "Email", email);
                        Log.i("Login" + "FirstName", firstName);

                        mName = firstName;
                        mLogin = email;

                        mPresenter.checkUniqueLogin(mLogin, null, null, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    public void registerSocial() {
        if (mLoginResult != null) {
            mPresenter.registerSocial(SocialLoginRequest.FACEBOOK,
                    mLoginResult.getAccessToken().getToken());
        }
    }

    @Override
    public void logOut() {
        AccountManager.getInstance(this).onLogout();
    }

    private void initGoogleApi() {
        mGoogleClient = CardeeApp.initLoginGoogleApi(this, connectionResult -> showMessage(connectionResult.getErrorMessage()));
    }

    private void cropImageIntent(Uri imgUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(imgUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_IMAGE);

        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment finalF = mFragmentManager.findFragmentByTag(RegisterFinalStepFragment.TAG);
        if (finalF != null && finalF.isVisible()) {
            onBackToFirstStep();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCM.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            case ActivityHelper.PICK_IMAGE:
//                if (resultCode == RESULT_OK && data.getData() != null) {
//                    if (data.getExtras() != null) {
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
//                            if (mFinalStepFragment != null && mFinalStepFragment.isVisible()) {
//                                mFinalStepFragment.setUserPhoto(bitmap);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                break;
//            case CROP_IMAGE:
//                if (data.getExtras() != null) {
//                    Bundle extras = data.getExtras();
//                    Bitmap bitmap = extras.getParcelable("data");
//                    if (mFinalStepFragment != null && mFinalStepFragment.isVisible()) {
//                        mFinalStepFragment.setUserPhoto(bitmap);
//                    }
//                }
//                break;
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    mPresenter.loginGoogle(result);
                }
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSignUp(String login, String password, String name) {
        mPresenter.checkUniqueLogin(login, password, name, false);
    }

    @Override
    public void onTakePhoto() {
        ActivityHelper.pickImageIntent(this, ActivityHelper.PICK_IMAGE);
    }

    @Override
    public void onBackToFirstStep() {
        logOut();

        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFirstStepFragment, RegisterFirstStepFragment.TAG)
                .commit();
    }


    @Override
    public void onTermsOfService() {

    }

    @Override
    public void onFacebook() {
        mButtonFacebook.performClick();
    }

    @Override
    public void onGoogle() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleClient), RC_SIGN_IN);
    }

    @Override
    public void onProceedGoogleLogin(final String accessToken) {
        runOnUiThread(() -> mPresenter.registerSocial(SocialLoginRequest.GOOGLE, accessToken));
    }

    @Override
    public void onSignUpAsRenter(String name, File picture) {
        mPresenter.setAccountState(RENTER_SESSION);
        mPresenter.signUp(mLogin, mPass, name, picture, RENTER_SESSION);
    }

    @Override
    public void onSignUpAsOwner(String name, File picture) {
        mPresenter.setAccountState(OWNER_SESSION);
        mPresenter.signUp(mLogin, mPass, name, picture, OWNER_SESSION);
    }

    @Override
    public void onValidationSuccess(String login, String password, String name) {
        mLogin = login;
        mPass = password;
        mName = name;
        ActivityHelper.hideSoftKeyboard(this);

        signUp();
    }

    private void signUp() {
        mPresenter.signUp(mLogin, mPass, mName);
    }

    @Override
    public void onSignUpSuccess() {
        mFinalStepFragment = RegisterFinalStepFragment.Companion.newInstance(mName);
        mFinalStepFragment.setViewListener(this);

        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFinalStepFragment, RegisterFinalStepFragment.TAG)
                .commit();
    }

    @Override
    public void loginAsRenter() {
        mPresenter.setAccountState(RENTER_SESSION);
        Intent intent = new Intent(this, RenterHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginAsOwner() {
        mPresenter.setAccountState(OWNER_SESSION);
        Intent intent = new Intent(this, OwnerHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegistrationSuccess(String session) {
        Intent intent = null;
        switch (session) {
            case OWNER_SESSION:
                intent = new Intent(this, OwnerHomeActivity.class);
                break;
            case RENTER_SESSION:
                intent = new Intent(this, RenterHomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
