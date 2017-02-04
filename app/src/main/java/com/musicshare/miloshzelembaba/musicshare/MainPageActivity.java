package com.musicshare.miloshzelembaba.musicshare;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class MainPageActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    static LoginWindowInfo loginWindowInfo;
    static Party party;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private static final int SECTION_TEXT_SIZE = 30;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //  *viewPageIndicator*  // mSectionsPagerAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Set the pager with an adapter
        //  *viewPageIndicator*  // mViewPager.setAdapter(new TestAdapter(getSupportFragmentManager()));

        //Bind the title indicator to the adapter
        //  *viewPageIndicator*  // LinePageIndicator linePageIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        //  *viewPageIndicator*  // linePageIndicator.setViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWindowInfo = new LoginWindowInfo(getApplicationContext());
                startActivity(loginWindowInfo.getLoginWindowIntent());
                LoginActivity.setLogInWindowInfo(loginWindowInfo);
                party = Party.getInstance();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        View joinPartyView;
        View createPartyView;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void wipeText(TextInputEditText partyName){
            partyName.setText("");
            partyName.setTextColor(Color.BLACK);
        }

        public void createAParty(){
            if (loginWindowInfo != null && loginWindowInfo.isUserLoggedIn()){
                createPartyView.findViewById(R.id.partyNameInput).setVisibility(View.VISIBLE);
                final TextView sectionText = (TextView) createPartyView.findViewById(R.id.section_label);
                final TextInputEditText partyName = (TextInputEditText) createPartyView.findViewById(R.id.editInputPartyName);
                sectionText.setText("Please enter the name of your party above");
                partyName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wipeText(partyName);
                    }
                });
                partyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override // Triggered when user is done entering the party's name
                        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                            party = Party.getInstance();
                            party.setPartyName(partyName.getText().toString());
                            party.setInfo(loginWindowInfo);
                            return true;
                        }
                    });
            } else {
                Toast.makeText(getContext(), "Must be logged in to create a party, please log in", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_page, container, false);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                        case 1:
                            //joinAParty();
                            break;
                        case 2:
                            createAParty();
                            break;
                    }
                }
            });
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setTextSize(SECTION_TEXT_SIZE);
            String sectionText = getSectionText(getArguments().getInt(ARG_SECTION_NUMBER));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                joinPartyView = rootView;
            } else {
                createPartyView = rootView;
            }
            textView.setText(sectionText);
            return rootView;
        }

        public String getSectionText(int section){
            switch (section) {
                case 1:
                    return "Join A Party";
                case 2:
                    return "Create A Party";
            }
            return null;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
