package com.miloshzelembaba.play.Activity.PartyMembers;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.R;

import org.json.JSONObject;

public class PartyMembersActivity extends Activity {
    public static final String PARTY = "party";
    private PartyMembersActivity thisActivity;


    private Party mParty;
    private PartyMembersAdapter adapter;

    // Views
    TextView mPartyMembersHeader;
    ListView mPartyMembersListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_members);
        thisActivity = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.6));

        init();
    }

    private void init() {
        initViews();
        mParty = null;
        try {
            mParty = new Party(new JSONObject(getIntent().getStringExtra(PARTY)));
        } catch (Exception e) {} // shouldn't ever throw exception unless savedInstance doesn't have PARTY in it

        adapter = new PartyMembersAdapter(this, 0, mParty.getPartyMembers(), mParty.getHost());
        mPartyMembersListview.setAdapter(adapter);
    }

    private void initViews() {
        mPartyMembersHeader = (TextView) findViewById(R.id.party_members_header);
        mPartyMembersListview = (ListView) findViewById(R.id.party_members_listview);
    }
}
