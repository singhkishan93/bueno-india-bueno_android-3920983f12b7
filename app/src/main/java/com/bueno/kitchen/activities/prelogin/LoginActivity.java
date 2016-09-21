package com.bueno.kitchen.activities.prelogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.utils.Config;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int REQUEST_EMAIL = 202;
    private static final int REQUEST_CODE_OTP = 201;

    //Google Sign-in
    private static final int RC_SIGN_IN = 200;
    //Facebook
    @Bind(R.id.login_button)
    public LoginButton loginButton;
    public SignInButton googleSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private String email;
    private String name;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setUpFacebook();
        setUpGoogleSignIn();
        enableBackButton();
    }

    private void setUpFacebook() {
        loginButton.setReadPermissions(Config.FACEBOOK_PERMISSIONS);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private void facebookAuth(final AccessToken accessToken) {
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (graphResponse.getError() == null) {
                            loadOTPScreen(jsonObject.optString("email", null),
                                    jsonObject.optString("name", null),
                                    "http://graph.facebook.com/" + accessToken.getUserId() + "/picture?type=normal");
                        } else {
                            utilitySingleton.ShowToast(R.string.toast_login_faliure);
                        }
                        LoginManager.getInstance().logOut();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult.getAccessToken() != null) {
                    facebookAuth(loginResult.getAccessToken());
                } else {
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onCancel() {
                utilitySingleton.ShowToast(R.string.toast_login_faliure);
            }

            @Override
            public void onError(FacebookException exception) {
                utilitySingleton.ShowToast(R.string.toast_login_faliure);
            }
        });
    }

    private void setUpGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(this);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        googleSignInButton.setScopes(gso.getScopeArray());
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void loadOTPScreen(String email,
                               String name,
                               String imageUrl) {
        if (!TextUtils.isEmpty(email))
            this.email = email;
        if (!TextUtils.isEmpty(name))
            this.name = name;
        if (!TextUtils.isEmpty(imageUrl))
            this.imageUrl = imageUrl;

        if (!TextUtils.isEmpty(this.email)) {
            Intent intent = new Intent(this, OTPActivity.class);
            intent.putExtra(Config.Intents.INTENT_EMAIL, this.email);
            if (!TextUtils.isEmpty(this.name))
                intent.putExtra(Config.Intents.INTENT_NAME, this.name);
            if (!TextUtils.isEmpty(this.imageUrl))
                intent.putExtra(Config.Intents.INTENT_IMAGE, this.imageUrl);
            startActivityForResult(intent, REQUEST_CODE_OTP);
        } else {
            startActivityForResult(new Intent(this, EmailActivity.class), REQUEST_EMAIL);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        utilitySingleton.ShowToast(R.string.toast_login_faliure);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                if (mGoogleApiClient.isConnected()) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_OTP:
                if (resultCode == RESULT_OK) {
                    utilitySingleton.ShowToast(R.string.toast_login_successful);
                    setResult(RESULT_OK);
                    finish();
                } else performLogout();
                break;
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
            case REQUEST_EMAIL:
                if (resultCode == RESULT_OK)
                    loadOTPScreen(data.getStringExtra(Config.Intents.INTENT_EMAIL),
                            null,
                            null);
                else performLogout();
                break;
        }
    }

    private void performLogout() {
        this.name = null;
        this.email = null;
        this.imageUrl = null;
        signOutGoogle();
        LoginManager.getInstance().logOut();
        utilitySingleton.ShowToast(R.string.toast_login_faliure);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            signOutGoogle();
            if (acct != null) {
                loadOTPScreen(acct.getEmail(),
                        acct.getDisplayName(),
                        acct.getPhotoUrl() + "");
                return;
            }
        }
        utilitySingleton.ShowToast(R.string.toast_login_faliure);
    }

    private void signOutGoogle() {
        if (mGoogleApiClient.isConnected())
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }
}
