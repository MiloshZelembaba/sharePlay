package com.miloshzelembaba.play.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.api.Services.LoginService;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class InitialActivity extends Activity {
    private TextView mJoinAParty;
    private LoginService loginService;

    LinearLayout mLoginContainer;
    EditText mEmailInput;
    EditText mPasswordInput;
    Button  mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();
    }

    private void init() {
        loginService = new LoginService();
        mJoinAParty = (TextView) findViewById(R.id.join_party);
        mLoginContainer = (LinearLayout) findViewById(R.id.login_container);
        mEmailInput = (EditText) findViewById(R.id.login_email);
        mPasswordInput = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_login_button);

        setupViews();
    }

    private void setupViews(){
        mLoginContainer.setVisibility(GONE);

        mJoinAParty.setText(getString(R.string.join_party));
        mJoinAParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinAParty.setVisibility(GONE);
                mLoginContainer.setVisibility(VISIBLE);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginService.requestService("miloshzelembaba@gmail.com", "fire",
                        new LoginService.LoginServiceCallback() {
                            @Override
                            public void onSuccess(String result) {
                                mJoinAParty.setText(result);
                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        });
            }
        });
    }
}



//        request.requestService("http://10.0.3.2:8000/hello/", new APIRequest.APIRequestCallBack() {
//            @Override
//            public void onSuccess(String result) {
//                mJoinAParty.setText(result);
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//        });