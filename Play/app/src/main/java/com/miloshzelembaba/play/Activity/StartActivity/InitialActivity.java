package com.miloshzelembaba.play.Activity.StartActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miloshzelembaba.play.Activity.PartyActivityStuff.AdminPartyActivity;
import com.miloshzelembaba.play.Activity.PartyActivityStuff.GuestPartyActivity;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.Utils.SharedPreferenceUtil;
import com.miloshzelembaba.play.api.Services.AuthenticationServices.GetAccessTokenService;
import com.miloshzelembaba.play.api.Services.CreatePartyService;
import com.miloshzelembaba.play.api.Services.JoinPartyService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;
import com.miloshzelembaba.play.api.Services.LoginService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.miloshzelembaba.play.R.color.black;

public class InitialActivity extends FragmentActivity {
    private Context mContext;
    private TextView mJoinAParty;
    private TextView mCreateAParty;

    // local fields
    private boolean onJoinPartyView;

    // Services
    private JoinPartyService joinPartyService;
    private CreatePartyService createPartyService;
    private SpotifyManager mSpotifyManager = SpotifyManager.getInstance();
    private LoginService mLoginService;
    private LeavePartyService mLeavePartyService;
    private GetAccessTokenService getAccessTokenService = new GetAccessTokenService();
    private SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(this);
    LoadingFragment loadingFragment;

    // Views
    private TextView mCurrentUserDisplayName;
    private TextView mLogoutButton;
    private ImageView mProfileLogo;
    private View mDivider;
    private TextView mLoginTemporaryUser;

    // Join Party
    private LinearLayout mJoinPartyContainer;
    private EditText mPartyId;
    private Button mJoinPartyButton;
    public interface AuthResult {
        void onSuccess(String accessToken);
        void onFailure();
    }

    AuthResult authResultCallback = new AuthResult() {
        @Override
        public void onSuccess(String accessToken) {
            sharedPreferenceUtil.setUser(ApplicationUtil.getInstance().getUser());
            setupSpotifyPlayer(accessToken);
            showLoginSucces(ApplicationUtil.getInstance().getUser());
            hideLoadingSpinner();
        }
        @Override
        public void onFailure() {
            sharedPreferenceUtil.setUser(null);
            ErrorService.showErrorMessage(InitialActivity.this, "Failed to login", ErrorService.ErrorSeverity.HIGH);
        }
    };


    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.gray1));
        loadingFragment = new LoadingFragment();

        init();
        setupViews();

        User user = sharedPreferenceUtil.getUser();
        ApplicationUtil.getInstance().setUser(user);
        if (user != null) {
            showLoginSucces(user);
        }
        showLoadingSpinner();
        mSpotifyManager.authorize(this, authResultCallback);
    }

    private void showLoadingSpinner() {
        if (loadingFragment == null) {
            loadingFragment = new LoadingFragment();
        }
        loadingFragment.setCancelable(false);
        FragmentManager fm = getSupportFragmentManager();
        loadingFragment.show(fm ,"");
    }

    private void hideLoadingSpinner() {
        if (loadingFragment == null) {
            return;
        }

        loadingFragment.dismiss();
    }

    private void setupSpotifyPlayer(String accessToken) {
        Config playerConfig = new Config(mContext, accessToken, SpotifyManager.CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mSpotifyManager.setPlayer(spotifyPlayer);
                mSpotifyManager.getPlayer().addConnectionStateCallback(mSpotifyManager);
                mSpotifyManager.getPlayer().addNotificationCallback(mSpotifyManager);
            }

            @Override
            public void onError(Throwable throwable) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSpotifyManager != null) {
            mSpotifyManager.pauseSong();
        }
        mContext = this;
        setupViews();
        if (ApplicationUtil.getInstance().getUser() != null) {
            mLeavePartyService.requestService(ApplicationUtil.getInstance().getUser(), null);
            showLoginSucces(ApplicationUtil.getInstance().getUser());
        } else {
            showLoginViews();
        }
    }

    @Override
    public void onBackPressed() {
        if (onJoinPartyView) {
            User user = ApplicationUtil.getInstance().getUser();
            setupViews();
            if (user != null) {
                showLoginSucces(user);
            } else {
                showLoginViews();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        joinPartyService = new JoinPartyService();
        createPartyService = new CreatePartyService();
        mLoginService = new LoginService();
        mLeavePartyService = new LeavePartyService();
        mJoinAParty = (TextView) findViewById(R.id.join_party);
        mCreateAParty = (TextView) findViewById(R.id.create_party);
        mJoinPartyContainer = (LinearLayout) findViewById(R.id.join_party_container);
        mPartyId = (EditText) findViewById(R.id.party_code);
        mJoinPartyButton = (Button) findViewById(R.id.join_party_button);
        mCurrentUserDisplayName = (TextView) findViewById(R.id.current_user_display_name);
        mLogoutButton = (TextView) findViewById(R.id.logout);
        mProfileLogo = (ImageView) findViewById(R.id.profile_logo);
        mDivider = findViewById(R.id.divider);
        mLoginTemporaryUser = (TextView) findViewById(R.id.temporary_user_login);
    }

    private void setupViews(){
        onJoinPartyView = false;
        mJoinAParty.setVisibility(VISIBLE);
        mCreateAParty.setVisibility(VISIBLE);
        mDivider.setVisibility(VISIBLE);
        mJoinPartyContainer.setVisibility(GONE);
        mCurrentUserDisplayName.setVisibility(GONE);
        mLogoutButton.setVisibility(GONE);
        mProfileLogo.setImageResource(R.drawable.baseline_face_black_24dp);
        mJoinAParty.setTextColor(ContextCompat.getColor(this, black));
        mJoinAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InitialActivity.this, "You must log in", Toast.LENGTH_SHORT).show();
            }
        });
        mCreateAParty.setTextColor(ContextCompat.getColor(this, black));
        mCreateAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createParty(ApplicationUtil.getInstance().getUser());
            }
        });
        mJoinPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinParty();
            }
        });
        mLoginTemporaryUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTemporaryUser();
            }
        });

        if (ApplicationUtil.getInstance().getUser() != null) {
            showCurrentLoginInfo(ApplicationUtil.getInstance().getUser());
        }
    }

    private void createParty(final User user){
        if (user == null) {
            Toast.makeText(InitialActivity.this, "You must log in", Toast.LENGTH_SHORT).show();
            return;
        }
        String partyName = "Fun Times";
        createPartyService.requestService(user, partyName,
                new CreatePartyService.CreatePartyServiceCallback() {
                    @Override
                    public void onSuccess(String partyId) {
                        startAdminPartyActivity(partyId);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        ErrorService.showErrorMessage(mContext,
                                errorMessage,
                                ErrorService.ErrorSeverity.HIGH);
                    }
                });
    }

    private void joinParty(){
        String partyId = mPartyId.getText().toString();

        if (partyId.isEmpty() || ApplicationUtil.getInstance().getUser() == null){
            return;
        }

        joinPartyService.requestService(partyId, ApplicationUtil.getInstance().getUser(),
                new JoinPartyService.JoinPartServiceCallback() {
                    @Override
                    public void onSuccess(String partyId) {
                        startGuestPartyActivity(partyId);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        ErrorService.showErrorMessage(mContext,
                                errorMessage,
                                ErrorService.ErrorSeverity.HIGH);
                    }
                });
    }

    private void startAdminPartyActivity(String partyId){
        Intent intent = new Intent(this, AdminPartyActivity.class);
        intent.putExtra(AdminPartyActivity.EXTRA_PARTY_ID, partyId);
        try {
            intent.putExtra(AdminPartyActivity.EXTRA_USER, ApplicationUtil.getInstance().getUser().serialize().toString());
        } catch (JSONException e) {
            // error message
        }
        startActivity(intent);
    }

    private void startGuestPartyActivity(String partyId){
        Intent intent = new Intent(this, GuestPartyActivity.class);
        intent.putExtra(GuestPartyActivity.EXTRA_PARTY_ID, partyId);
        try {
            intent.putExtra(GuestPartyActivity.EXTRA_USER, ApplicationUtil.getInstance().getUser().serialize().toString());
        } catch (JSONException e) {
            // error message
        }
        startActivity(intent);
    }

    private void showLoginSucces(User user) {
        mJoinAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJoinPartyView = true;
                mJoinAParty.setVisibility(GONE);
                mCreateAParty.setVisibility(GONE);
                mDivider.setVisibility(GONE);
                mJoinPartyContainer.setVisibility(VISIBLE);
                mPartyId.setHint("Enter Party ID");
            }
        });
        mLoginTemporaryUser.setVisibility(GONE);
        if (user.isTemporaryUser() || !user.isPremium()) {
            showCurrentLoginInfo(user);
            mJoinAParty.setTextColor(ContextCompat.getColor(this, black));
            mJoinAParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onJoinPartyView = true;
                    mJoinAParty.setVisibility(GONE);
                    mCreateAParty.setVisibility(GONE);
                    mDivider.setVisibility(GONE);
                    mJoinPartyContainer.setVisibility(VISIBLE);
                    mPartyId.setHint("Enter Party ID");
                }
            });
            mCreateAParty.setTextColor(ContextCompat.getColor(this, R.color.gray2));
            mCreateAParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ErrorService.showErrorMessage(mContext,
                            "you must be a premium spotify user to be create a party, sorry",
                            ErrorService.ErrorSeverity.LOW);
                }
            });
        } else if (user.isPremium()){
            showCurrentLoginInfo(user);
            mJoinAParty.setTextColor(ContextCompat.getColor(this, black));
            mCreateAParty.setTextColor(ContextCompat.getColor(this, black));
            mCreateAParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createParty(ApplicationUtil.getInstance().getUser());
                }
            });
            mJoinAParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onJoinPartyView = true;
                    mJoinAParty.setVisibility(GONE);
                    mCreateAParty.setVisibility(GONE);
                    mDivider.setVisibility(GONE);
                    mJoinPartyContainer.setVisibility(VISIBLE);
                    mPartyId.setHint("Enter Party ID");
                }
            });
        } else {
            ErrorService.showErrorMessage(this, "invalid user logged in", ErrorService.ErrorSeverity.HIGH);
        }
    }

    private void showCurrentLoginInfo(User user) {
        mCurrentUserDisplayName.setVisibility(VISIBLE);

        String text = user.getDisplayName();
        mCurrentUserDisplayName.setText(text);

        mLogoutButton.setText(getString(R.string.logout));
        mLogoutButton.setVisibility(VISIBLE);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationUtil.getInstance().setUser(null);
                sharedPreferenceUtil.setUser(null);
                mSpotifyManager.logout((Activity)v.getContext());
                showLoginViews();
            }
        });
    }

    private void showLoginViews() {
        setupViews();
        mCurrentUserDisplayName.setVisibility(GONE);
        mLogoutButton.setText(getString(R.string.login));
        mLogoutButton.setTextSize(16);
        mLogoutButton.setVisibility(VISIBLE);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyManager.authorize(InitialActivity.this, authResultCallback);
            }
        });
        mLoginTemporaryUser.setVisibility(VISIBLE);

        mJoinAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InitialActivity.this, "You must log in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTemporaryUser() {
        String displayName = User.TEMPORARY_USER_DISPLAY_NAME;
        String email = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mLoginService.requestService(email, displayName, "none",
                new LoginService.LoginServiceCallback() {
                    @Override
                    public void onSuccess(User user) {
                        ApplicationUtil.getInstance().setUser(user);
                        sharedPreferenceUtil.setUser(null); // we purposely set it to null so that the next time they log in they'll be promted to sign in through spotify again
                        showLoginSucces(user);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        showLoginViews();
                        ErrorService.showErrorMessage(mContext,
                                "Login Failed!",
                                ErrorService.ErrorSeverity.HIGH);
                    }
                });
    }

    private void getAccessToken(String authCode) {
        getAccessTokenService.requestService(authCode, new GetAccessTokenService.GetAccessTokenServiceCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    User user = new User(response.optJSONObject("user"));
                    ApplicationUtil.getInstance().setUser(user);
                } catch (Exception e) {}

                String accessToken = response.optString("access_token");
                int timeUntilExpire = response.optInt("expires_in");
                mSpotifyManager.createSpotifyApi(accessToken, timeUntilExpire);
                sharedPreferenceUtil.setUser(ApplicationUtil.getInstance().getUser());

                setupSpotifyPlayer(accessToken);
                showLoginSucces(ApplicationUtil.getInstance().getUser());
                hideLoadingSpinner();
            }

            @Override
            public void onFailure(String errorMessage) {
                ErrorService.showErrorMessage(InitialActivity.this, "Error logging in", ErrorService.ErrorSeverity.HIGH);
                hideLoadingSpinner();
//                showLoginViews();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SpotifyManager.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            // todo: this new flow probably breaks the offline mode and other cases.
            if (response.getType() == AuthenticationResponse.Type.CODE) {
                String authCode = response.getCode();
                getAccessToken(authCode);
            } else if (response.getType() == AuthenticationResponse.Type.EMPTY) {
                sharedPreferenceUtil.setUser(null);
                createTemporaryUser();
                hideLoadingSpinner();
            } else if (response.getType() == AuthenticationResponse.Type.ERROR) {
                sharedPreferenceUtil.setUser(null);
                showLoginViews();
                if (response.getError().equals("OFFLINE_MODE_ACTIVE")) {
                    ErrorService.showErrorMessage(this,
                            "You're account has offline mode activated, please open spotify and turn it off",
                            ErrorService.ErrorSeverity.HIGH);
                }
                hideLoadingSpinner();
            } else {
                sharedPreferenceUtil.setUser(null);
                showLoginViews();
                ErrorService.showErrorMessage(this,
                        "Uknown Error signing into spotify",
                        ErrorService.ErrorSeverity.HIGH);
                hideLoadingSpinner();
            }
        }

    }

}