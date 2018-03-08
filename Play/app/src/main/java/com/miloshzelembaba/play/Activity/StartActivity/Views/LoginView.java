package com.miloshzelembaba.play.Activity.StartActivity.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miloshzelembaba.play.R;

/**
 * Created by miloshzelembaba on 2018-03-07.
 */

public class LoginView extends LinearLayout {
    Context mContext;

    TextView mFirstName;

    public LoginView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.login_layout, this, true);

        mFirstName = (TextView) findViewById(R.id.first_name);
        mFirstName.setText("Milosh Zelembaba");
    }
}

