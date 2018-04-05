package com.miloshzelembaba.play.Activity.StartActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miloshzelembaba.play.Activity.PartyActivityStuff.AdminPartyActivity;
import com.miloshzelembaba.play.Activity.PartyActivityStuff.GuestPartyActivity;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.RequestListener;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyInfo;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.api.Services.CreatePartyService;
import com.miloshzelembaba.play.api.Services.JoinPartyService;
import com.miloshzelembaba.play.api.Services.LoginService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class InitialActivity extends Activity {
    private TextView mJoinAParty;
    private TextView mCreateAParty;

    // Services
    private LoginService loginService;
    private JoinPartyService joinPartyService;
    private CreatePartyService createPartyService;
    private SpotifyManager mSpotifyManager;
    private LoginService mLoginService;

    // Login
    private LinearLayout mLoginContainer;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button  mLoginButton;
    private TextView mSimpleLogin;
    private LinearLayout mSimpleLoginContainer;

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

        init();
        setupViews();

        SpotifyManager.attemptSpotifyLogin(this);
        mSpotifyManager = SpotifyManager.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        loginService = new LoginService();
        joinPartyService = new JoinPartyService();
        createPartyService = new CreatePartyService();
        mLoginService = new LoginService();
        mJoinAParty = (TextView) findViewById(R.id.join_party);
        mCreateAParty = (TextView) findViewById(R.id.create_party);
        mLoginContainer = (LinearLayout) findViewById(R.id.login_container);
        mEmailInput = (EditText) findViewById(R.id.login_email);
        mPasswordInput = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_login_button);
        mJoinPartyContainer = (LinearLayout) findViewById(R.id.join_party_container);
        mPartyId = (EditText) findViewById(R.id.party_code);
        mJoinPartyButton = (Button) findViewById(R.id.join_party_button);
        mSimpleLogin = (TextView) findViewById(R.id.simple_login);
        mSimpleLoginContainer = (LinearLayout) findViewById(R.id.simple_login_container);
    }

    private void setupViews(){
        mJoinAParty.setVisibility(VISIBLE);
        mCreateAParty.setVisibility(VISIBLE);
        mLoginContainer.setVisibility(GONE);
        mJoinPartyContainer.setVisibility(GONE);

        mJoinAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        mSimpleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleLogin();
            }
        });
    }

    private void createParty(final User user){
        String partyName = "Fun Timez";
        createPartyService.requestService(user, partyName,
                new CreatePartyService.CreatePartyServiceCallback() {
                    @Override
                    public void onSuccess(String partyId) {
                        startAdminPartyActivity(partyId);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
    }

    private void simpleLogin(){
        mJoinAParty.setVisibility(GONE);
        mCreateAParty.setVisibility(GONE);
        mSimpleLoginContainer.setVisibility(VISIBLE);

        mSimpleLogin.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                        }
                        // Return true if you have consumed the action, else false.
                        return false;
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
        mSpotifyManager.getEmail(new SpotifyResultCallback(){
            @Override
            public void onSuccess(Object result) {
                String email = (String) result;

                mLoginService.requestService(email, "",
                        new LoginService.LoginServiceCallback() {
                            @Override
                            public void onSuccess(User user) {
                                completeLoginTasks(user);
                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        });
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void completeLoginTasks(User user) {
        ApplicationUtil.getInstance().setUser(user);
        // TODO: should move this out of the activity, and into some sort of init code
        RequestListener requestListener = new RequestListener(user, this);
        Thread newThread = new Thread(requestListener);
        newThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SpotifyInfo.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                SpotifyInfo.setAccessToken(response.getAccessToken());
                mSpotifyManager.createSpotifyApi(); // create here since at this point we have the access token
                startLoginTasks();
                Config playerConfig = new Config(this, response.getAccessToken(), SpotifyInfo.CLIENT_ID);
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