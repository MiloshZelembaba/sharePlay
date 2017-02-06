package com.musicshare.miloshzelembaba.musicshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.ViewStubCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.content;
import static com.musicshare.miloshzelembaba.musicshare.R.id.container;

public class PartyActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, NavigationView.OnNavigationItemSelectedListener {

    // TODO: Replace with your client ID
    public static final String CLIENT_ID = "75cc7c4b4c6d49388044414a5ba6aaa6";
    // TODO: Replace with your redirect URI
    public static final String REDIRECT_URI = "http://localhost:8888/callback";
    static public AuthenticationResponse auth;
    private LoginWindowInfo loginWindowInfo;

    static private Player mPlayer;
    // Request code that will be used to verify if the result comes from correct activity, can be any integer
    private static final int REQUEST_CODE = 1337;

    static Party party;
    public Context context;
    View baseView;
    SongAdapter adapter;
    SongAdapter searchAdapter;
    ArrayList<Song> listItems=new ArrayList<Song>();
    ArrayList<Song> searchItems=new ArrayList<Song>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"}); // TODO the scope here can change for more privliges: https://developer.spotify.com/web-api/using-scopes/
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        adapter = new SongAdapter(this, R.layout.song_layout, listItems,this);
        searchAdapter = new SongAdapter(this,R.layout.song_layout, searchItems,this);
        ListView searchListView = (ListView) findViewById(R.id.searchList);
        searchListView.setAdapter(searchAdapter);
        ListView songsListView = (ListView) findViewById(R.id.songList);
        songsListView.setAdapter(adapter);
        //songListView.onCreate(savedInstanceState);
//        ViewGroup container = (ViewGroup) findViewById(android.R.id.content);
//        LayoutInflater inflater = getLayoutInflater();
//        baseView = inflater.inflate(R.layout.content_party, container, false);
//        container.addView(baseView);

        if (party.isSongQueueEmpty()) { // should always be true here, i think
            TextView textView = (TextView) findViewById(R.id.emptyPlaylistTextview);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSearchViews();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Song song = new Song("123123", "Boombox Cartel - Be Right There");
        addSong(song);
        addSong(song);
        addSong(song);
    }

    public void setupParty(String partyName, LoginWindowInfo loginWindowInfo, Context context, boolean isPartyOwner){
        this.context = context;
        party = Party.getInstance();
        party.setIsPartyOwner(isPartyOwner);
        party.setInfo(loginWindowInfo);
        party.setPartyName(partyName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            auth = response;
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(PartyActivity.this);
                        mPlayer.addNotificationCallback(PartyActivity.this);
                        onLoggedIn();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    public void playSong(Song song){
        //mPlayer.playUri(null, "spotify:track:5EX8gks8V2wDZanRGAy8pm", 0, 0);
        mPlayer.playUri(null, song.getURI(), 0, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginFailed(Error var1){

    }

    public void setupSearchViews(){
        ListView songsListView = (ListView) findViewById(R.id.songList);
        songsListView.setVisibility(View.INVISIBLE);
        songsListView.setEnabled(false);

        ListView searchListView = (ListView) findViewById(R.id.searchList);
        searchListView.setVisibility(View.VISIBLE);
        findViewById(R.id.searchInput).setVisibility(View.VISIBLE);
        final TextInputEditText searchFeild = (TextInputEditText) findViewById(R.id.editSearchInput);
        searchFeild.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() - 1 >= 0 && s.charAt(s.length() - 1) == '\n') {
                    search(searchFeild.getText().toString());
                }
            }
        });
    }

    public void search(String searchQuery){
        searchItems = SpotifySearch.getResults(searchQuery);
        for (Song song: searchItems){
            searchAdapter.addSong(song);
        }
        searchAdapter.notifyDataSetChanged();

    }

    @Override
    public Intent getIntent(){
        Intent intent = new Intent(context, PartyActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.party, menu);
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

    private void removeEmptyQueueMessage(){
        TextView emptyPlaylistMessage = (TextView) findViewById(R.id.emptyPlaylistTextview);
        emptyPlaylistMessage.setVisibility(View.INVISIBLE);
    }

    public void addSong(Song song){
        party.addSong(song);
        listItems.add(song);
        adapter.addSong(song);
        adapter.notifyDataSetChanged();
        removeEmptyQueueMessage();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        //mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
        //mPlayer.playUri(null, "spotify:track:5EX8gks8V2wDZanRGAy8pm", 0, 0);
        MainPageActivity.logIn();
        //finish();
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}
