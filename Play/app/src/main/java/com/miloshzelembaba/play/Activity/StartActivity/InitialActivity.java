package com.miloshzelembaba.play.Activity.StartActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miloshzelembaba.play.Activity.PartyActivityStuff.AdminPartyActivity;
import com.miloshzelembaba.play.Activity.PartyActivityStuff.GuestPartyActivity;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.RequestListener;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
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

import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class InitialActivity extends Activity {
    private Context mContext;
    private TextView mJoinAParty;
    private TextView mCreateAParty;

    // local fields
    private boolean onJoinPartyView;

    // Services
    private LoginService loginService;
    private JoinPartyService joinPartyService;
    private CreatePartyService createPartyService;
    private SpotifyManager mSpotifyManager;
    private LoginService mLoginService;
    private LeavePartyService mLeavePartyService;

    // Views
    private TextView mCurrentEmail;
    private TextView mLogoutButton;

    // Join Party
    private LinearLayout mJoinPartyContainer;
    private EditText mPartyId;
    private Button mJoinPartyButton;

    public interface SpotifyResultCallback{
        void onSuccess(Object result);
        void onFailure();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;

        init();
        setupViews();

        SpotifyManager.attemptSpotifyLogin(this);
        mSpotifyManager = SpotifyManager.getInstance(); // getInstance only works once logged in
    }

    @Override
    public void onResume() {
        super.onResume();
        mSpotifyManager.pauseSong();
        mContext = this;
        setupViews();
        if (ApplicationUtil.getInstance().getUser() != null) {
            showCurrentLoginInfo(ApplicationUtil.getInstance().getUser());
        }

        if (ApplicationUtil.getInstance().getUser() != null) {
            mLeavePartyService.requestService(ApplicationUtil.getInstance().getUser(), null);
        }
    }

    @Override
    public void onBackPressed() {
        if (onJoinPartyView) {
            setupViews();
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        loginService = new LoginService();
        joinPartyService = new JoinPartyService();
        createPartyService = new CreatePartyService();
        mLoginService = new LoginService();
        mLeavePartyService = new LeavePartyService();
        mJoinAParty = (TextView) findViewById(R.id.join_party);
        mCreateAParty = (TextView) findViewById(R.id.create_party);
        mJoinPartyContainer = (LinearLayout) findViewById(R.id.join_party_container);
        mPartyId = (EditText) findViewById(R.id.party_code);
        mJoinPartyButton = (Button) findViewById(R.id.join_party_button);
        mCurrentEmail = (TextView) findViewById(R.id.current_user_email);
        mLogoutButton = (TextView) findViewById(R.id.logout);
    }

    private void setupViews(){
        onJoinPartyView = false;
        mJoinAParty.setVisibility(VISIBLE);
        mCreateAParty.setVisibility(VISIBLE);
        mJoinPartyContainer.setVisibility(GONE);
        mCurrentEmail.setVisibility(GONE);
        mLogoutButton.setVisibility(GONE);

        mJoinAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJoinPartyView = true;
                mJoinAParty.setVisibility(GONE);
                mCreateAParty.setVisibility(GONE);
                mJoinPartyContainer.setVisibility(VISIBLE);
            }
        });

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
    }

    private void createParty(final User user){
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

        if (partyId.isEmpty()){
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

    private void startLoginTasks() {
        mSpotifyManager.getUserDetails(new SpotifyResultCallback(){
            @Override
            public void onSuccess(Object result) {
                HashMap<String, String> userDetails = (HashMap<String,String>) result;
                String email = userDetails.get(User.EMAIL);
                String displayName = userDetails.get(User.DISPLAY_NAME);

                mLoginService.requestService(email, displayName,
                        new LoginService.LoginServiceCallback() {
                            @Override
                            public void onSuccess(User user) {
                                ApplicationUtil.getInstance().setUser(user);
                                completeLoginTasks(user);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                ErrorService.showErrorMessage(mContext,
                                        "Login Failed!",
                                        ErrorService.ErrorSeverity.HIGH);
                            }
                        });
            }
            @Override
            public void onFailure() {
                ErrorService.showErrorMessage(mContext,
                        "Couldn't get user's spotify email",
                        ErrorService.ErrorSeverity.HIGH);
            }
        });
    }

    private void showCurrentLoginInfo(User user) {
        mCurrentEmail.setVisibility(VISIBLE);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.current_login_email), user.getEmail());
        mCurrentEmail.setText(text);

        mLogoutButton.setVisibility(VISIBLE);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyManager.relogin();
            }
        });
    }

    private void completeLoginTasks(User user) {
        showCurrentLoginInfo(user);

        RequestListener requestListener = new RequestListener(user, this);
        Thread newThread = new Thread(requestListener);
        newThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SpotifyManager.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                SpotifyManager.setAccessToken(response.getAccessToken());
                mSpotifyManager.createSpotifyApi(); // create here since at this point we have the access token
                startLoginTasks();

                Config playerConfig = new Config(this, SpotifyManager.ACCESS_TOKEN, SpotifyManager.CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mSpotifyManager.setPlayer(spotifyPlayer);
                        mSpotifyManager.getPlayer().addConnectionStateCallback(mSpotifyManager);
                        mSpotifyManager.getPlayer().addNotificationCallback(mSpotifyManager);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }

    }
}