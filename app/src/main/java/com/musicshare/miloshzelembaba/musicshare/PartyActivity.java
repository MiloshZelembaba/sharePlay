package com.musicshare.miloshzelembaba.musicshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
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

public class PartyActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, NavigationView.OnNavigationItemSelectedListener {

    static private Player mPlayer;
    static private SpotifyInfo spotifyInfo;

    static Party party;
    public Context context;
    PlaylistAdapter playList;
    SongAdapter searchAdapter;
    ArrayList<Song> listItems=new ArrayList<Song>();
    ArrayList<Song> searchItems=new ArrayList<Song>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Spotify init stuff
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(spotifyInfo.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                spotifyInfo.REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"}); // TODO the scope here can change for more privliges: https://developer.spotify.com/web-api/using-scopes/
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, spotifyInfo.REQUEST_CODE, request);

        // playlist view and search view init stuff
        playList = new PlaylistAdapter(this, R.layout.song_layout, listItems,this);
        searchAdapter = new SongAdapter(this,R.layout.song_layout, searchItems,this);
        ListView searchListView = (ListView) findViewById(R.id.searchList);
        searchListView.setAdapter(searchAdapter);
        ListView songsListView = (ListView) findViewById(R.id.songList);
        songsListView.setAdapter(playList);

        // empty playlist text
        if (party.isSongQueueEmpty()) { // should always be true here, i think
            hidePlaylist();
            TextView textView = (TextView) findViewById(R.id.emptyPlaylistTextview);
            textView.setVisibility(View.VISIBLE);
        }

        // fab for adding songs
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == spotifyInfo.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            spotifyInfo.auth = response;
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), spotifyInfo.CLIENT_ID);
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
        mPlayer.playUri(null, song.getURI(), 0, 0);
    }

    private void hidePlaylist(){
        findViewById(R.id.playlistHeader).setVisibility(View.INVISIBLE);
        findViewById(R.id.playlistHeader).setEnabled(false);
        ListView songsListView = (ListView) findViewById(R.id.songList);
        songsListView.setVisibility(View.INVISIBLE);
        songsListView.setEnabled(false);
    }

    private void showPlaylist(){
        findViewById(R.id.playlistHeader).setVisibility(View.VISIBLE);
        findViewById(R.id.playlistHeader).setEnabled(true);
        ListView songsListView = (ListView) findViewById(R.id.songList);
        songsListView.setVisibility(View.VISIBLE);
        songsListView.setEnabled(true);
    }



    public void setupSearchViews(){
        removeEmptyQueueMessage();
        hidePlaylist();

        // show the search views
        ListView searchListView = (ListView) findViewById(R.id.searchList);
        searchListView.setVisibility(View.VISIBLE);
        findViewById(R.id.searchInput).setVisibility(View.VISIBLE);
        final TextInputEditText searchFeild = (TextInputEditText) findViewById(R.id.editSearchInput);
        searchFeild.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        searchFeild.setSingleLine(true);
        searchFeild.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchFeild.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search(searchFeild.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    public void search(String searchQuery){
        removeEmptyQueueMessage();
        SpotifySearch.getResults(searchQuery, this);
        // will re-enter at onSearchResult()
    }

    public void onSearchResult(ArrayList<Song> searchItems){
        searchAdapter.clearPreviousSearch();
        for (Song song : searchItems) {
            searchAdapter.songs.add(song);
        }
        searchAdapter.notifyDataSetChanged();
    }

    public void endSearch(){
        // hide the search views
        ListView searchListView = (ListView) findViewById(R.id.searchList);
        searchListView.setVisibility(View.INVISIBLE);
        searchListView.setEnabled(false);
        findViewById(R.id.searchInput).setVisibility(View.INVISIBLE);
        findViewById(R.id.editSearchInput).setVisibility(View.INVISIBLE);

        showPlaylist();
    }



    private void removeEmptyQueueMessage(){
        TextView emptyPlaylistMessage = (TextView) findViewById(R.id.emptyPlaylistTextview);
        emptyPlaylistMessage.setVisibility(View.INVISIBLE);
    }

    public void addSong(Song song){
        party.addSong(song);
        playList.addSong(song);
        playList.notifyDataSetChanged();
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

    public void setupParty(String partyName, Context context, boolean isPartyOwner){
        this.context = context;
        party = Party.getInstance();
        party.setIsPartyOwner(isPartyOwner);
        party.setPartyName(partyName);
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

}
