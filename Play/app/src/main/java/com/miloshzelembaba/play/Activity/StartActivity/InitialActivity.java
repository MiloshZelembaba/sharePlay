package com.miloshzelembaba.play.Activity.StartActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.miloshzelembaba.play.api.Services.CreatePartyService;
import com.miloshzelembaba.play.api.Services.JoinPartyService;
import com.miloshzelembaba.play.api.Services.LoginService;

import org.json.JSONException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class InitialActivity extends Activity {
    private TextView mJoinAParty;
    private TextView mCreateAParty;
    private User user;

    // Services
    private LoginService loginService;
    private JoinPartyService joinPartyService;
    private CreatePartyService createPartyService;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // TODO: some sort of login thingy
        user = new User("3", "milosh", "zelembaba", "miloshzelembaba@gmail.com");

        // Spin off the request listener thread
        // TODO: should probably move this out of the activity, and into some sort of init code
        RequestListener requestListener = new RequestListener(user);
        Thread newThread = new Thread(requestListener);
        newThread.start();  //should be start();

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViews();
    }

    private void init() {
        loginService = new LoginService();
        joinPartyService = new JoinPartyService();
        createPartyService = new CreatePartyService();
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

        setupViews();
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
                createParty(user);
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

        joinPartyService.requestService(partyId, user,
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
            intent.putExtra(AdminPartyActivity.EXTRA_USER, user.serialize().toString());
        } catch (JSONException e) {
            // error message
        }
        startActivity(intent);
    }

    private void startGuestPartyActivity(String partyId){
        Intent intent = new Intent(this, GuestPartyActivity.class);
        intent.putExtra(GuestPartyActivity.EXTRA_PARTY_ID, partyId);
        try {
            intent.putExtra(GuestPartyActivity.EXTRA_USER, user.serialize().toString());
        } catch (JSONException e) {
            // error message
        }
        startActivity(intent);
    }


    // will be used later for logging in
    private void loginButtonClicked(){
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()){
            return;
        }

        loginService.requestService(email, password,
                new LoginService.LoginServiceCallback() {
                    @Override
                    public void onSuccess(User user) {


                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
    }
}