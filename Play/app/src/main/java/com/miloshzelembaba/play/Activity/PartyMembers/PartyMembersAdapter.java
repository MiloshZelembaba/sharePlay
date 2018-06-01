package com.miloshzelembaba.play.Activity.PartyMembers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.R;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-05-31.
 */

public class PartyMembersAdapter extends ArrayAdapter {
    private PartyMembersActivity mBaseActivity;
    private User mHost;

    public PartyMembersAdapter(PartyMembersActivity context, int textViewResourceId, ArrayList<User> listItems, User host){
        super(context, textViewResourceId, listItems);
        mBaseActivity = context;
        mHost = host;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mBaseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.party_member_layout, parent, false);
        }

        User user = ((User)getItem(position));
        String displayNameText = user.getDisplayName();

        if (user.getId().equals(mHost.getId())) {
            displayNameText += " - Host";
        }

        ((TextView)convertView.findViewById(R.id.party_member_display_name)).setText(displayNameText);
        return convertView;
    }
}
