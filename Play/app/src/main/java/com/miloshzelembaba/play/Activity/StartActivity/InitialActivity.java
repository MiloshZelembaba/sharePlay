package com.miloshzelembaba.play.Activity.StartActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miloshzelembaba.play.Activity.PartyActivityStuff.PartyActivity;
import com.miloshzelembaba.play.Models.User;
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

    // Join Party
    private LinearLayout mJoinPartyContainer;
    private EditText mPartyCode;
    private Button mJoinPartyButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // TODO: some sort of login thingy
        user = new User("4", "mike", "dantoni", "mikedantoni@gmail.com");

        init();
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
        mPartyCode = (EditText) findViewById(R.id.party_code);
        mJoinPartyButton = (Button) findViewById(R.id.join_party_button);

        setupViews();
    }

    private void setupViews(){
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

    }

    private void createParty(final User user){
        String partyName = "Fun Timez";
        createPartyService.requestService(user, partyName,
                new CreatePartyService.CreatePartyServiceCallback() {
                    @Override
                    public void onSuccess(String partyId) {
                        startPartyActivity(partyId);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
    }

    private void joinParty(){
        String partyCode = mPartyCode.getText().toString();

        if (partyCode.isEmpty()){
            return;
        }

        joinPartyService.requestService(partyCode, user,
                new JoinPartyService.JoinPartServiceCallback() {
                    @Override
                    public void onSuccess(String partyId) {
                        startPartyActivity(partyId);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });


    }

    private void startPartyActivity(String partyId){
        Intent intent = new Intent(this, PartyActivity.class);
        intent.putExtra(PartyActivity.EXTRA_PARTY_ID, partyId);
        try {
            intent.putExtra(PartyActivity.EXTRA_USER, user.serialize().toString());
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