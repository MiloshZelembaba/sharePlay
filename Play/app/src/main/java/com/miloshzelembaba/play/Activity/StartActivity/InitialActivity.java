package com.miloshzelembaba.play.Activity.StartActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private TextView mCurrentUserDisplayName;
    private TextView mLogoutButton;
    private ImageView mProfileLogo;
    private View mDivider;

    // Join Party
    private LinearLayout mJoinPartyContainer;
    private EditText mPartyId;
    private Button mJoinPartyButton;

    public interface SpotifyResultCallback{
        void onSuccess(Object result);
        void onFailure();
    }


    @Override
    @TargetApi(21) // TODO: this is stupid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.gray1));

        init();
        setupViews();

        SpotifyManager.getAuthCode(this);
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
        mCurrentUserDisplayName = (TextView) findViewById(R.id.current_user_display_name);
        mLogoutButton = (TextView) findViewById(R.id.logout);
        mProfileLogo = (ImageView) findViewById(R.id.profile_logo);
        mDivider = findViewById(R.id.divider);
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

        if (ApplicationUtil.getInstance().getUser() != null) {
            showCurrentLoginInfo(ApplicationUtil.getInstance().getUser());
        }
    }

    private void createParty(final User user){
        if (user == null) {
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

    private void startLoginTasks() {
        mSpotifyManager.getUserDetails(new SpotifyResultCallback(){
            @Override
            public void onSuccess(Object result) {
                HashMap<String, String> userDetails = (HashMap<String,String>) result;
                String email = userDetails.get(User.EMAIL);
                String displayName = userDetails.get(User.DISPLAY_NAME);
                String product = userDetails.get(User.PRODUCT);

                mLoginService.requestService(email, displayName, product,
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

    private void updateViewsByPermissions(User user) {
        if (user.isTemporaryUser() || mSpotifyManager == null || !mSpotifyManager.getProduct().toLowerCase().equals("premium")) {
            mCreateAParty.setTextColor(ContextCompat.getColor(this, R.color.gray2));
            mCreateAParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ErrorService.showErrorMessage(mContext,
                            "you must be a premium spotify user to be create a party, sorry",
                            ErrorService.ErrorSeverity.LOW);
                }
            });
        } else {
            mCreateAParty.setTextColor(ContextCompat.getColor(this, R.color.black));
            mCreateAParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createParty(ApplicationUtil.getInstance().getUser());
                }
            });
        }
    }

    private void showCurrentLoginInfo(User user) {
        updateViewsByPermissions(user);
        mCurrentUserDisplayName.setVisibility(VISIBLE);

        String text = user.getDisplayName();
        mCurrentUserDisplayName.setText(text);

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

    private void createTemporaryUser() {
        String displayName = User.TEMPORARY_USER_DISPLAY_NAME;
        String email = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mLoginService.requestService(email, displayName, "none",
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

    private void getAccessAndRefreshToken(final LoginCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String urlRequest = "https://accounts.spotify.com/api/token";
                    String body = "grant_type=authorization_code";
                    body += "&code=" + SpotifyManager.AUTH_CODE;
                    body += "&redirect_uri=" + SpotifyManager.REDIRECT_URI;
                    body += "&client_id=" + SpotifyManager.CLIENT_ID;
                    body += "&client_secret=01c3be40faec40cda92fe6af6810ce2c"; // client secret should come from server
                    byte[] postData       = body.getBytes("UTF-8");
                    int    postDataLength = postData.length;

                    StringBuilder result = new StringBuilder();
                    URL url = new URL(urlRequest);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length",String.valueOf(postDataLength));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postData);


                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    rd.close();

                    JSONObject obj = new JSONObject(result.toString());
                    callback.onSuccess(obj);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SpotifyManager.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.CODE) {
                SpotifyManager.setAuthCode(response.getCode());
                getAccessAndRefreshToken(new LoginCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        SpotifyManager.setAccessToken(response.optString("access_token"));
                        SpotifyManager.setRefreshToken(response.optString("refresh_token"));
                        mSpotifyManager = SpotifyManager.getInstance(); // getInstance only works once logged in
                        mSpotifyManager.createSpotifyApi(); // create here since at this point we have the access token
                        startLoginTasks();
                        Config playerConfig = new Config(mContext, SpotifyManager.ACCESS_TOKEN, SpotifyManager.CLIENT_ID);
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
                });
            } else if (response.getType() == AuthenticationResponse.Type.EMPTY) {
                createTemporaryUser();
            }
        }

    }

    private interface LoginCallback {
        void onSuccess(JSONObject obj);
    }
}