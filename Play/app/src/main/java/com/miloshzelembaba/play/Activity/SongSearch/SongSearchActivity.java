package com.miloshzelembaba.play.Activity.SongSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifySearch;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SongSearchActivity extends Activity {
    public static final int SONG_SEARCH_RESULT = 1;
    ListView mListView;
    EditText mSearchField;
    TextView mEmptyListview;

    SongSearchAdapter songSearchAdapter;
    SongSearchActivity thisActivity;



    public interface SongSearchResultCallBack{
        void onSuccess(ArrayList<Song> songs);
        void onFailure(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_search);
        thisActivity = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.6));

        init();
    }

    private void init(){
//        songSearchAdapter = new SongSearchAdapter(this, 0); // this might crash
        mListView = (ListView) findViewById(R.id.search_result_listview);
        mSearchField = (EditText) findViewById(R.id.song_search);
        mEmptyListview = (TextView) findViewById(R.id.empty_listview_text);

//        mListView.setAdapter(songSearchAdapter);

        setup();
    }

    private void setup(){
        mSearchField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mSearchField.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            searchSpotify(mSearchField.getText().toString());
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
    }

    private void searchSpotify(String query){
        SpotifySearch.getResults(query, new SongSearchResultCallBack() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                mEmptyListview.setVisibility(GONE);
                mListView.setVisibility(VISIBLE);
                songSearchAdapter = new SongSearchAdapter(thisActivity, 0, songs); // are activities singletons?
                mListView.setAdapter(songSearchAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO: create failure popup
            }
        });
    }

    public void addSong(Song song){
        Intent data = new Intent();
        try {
            data.putExtra("song", song.serialize().toString());
        } catch (Exception e){
            data.putExtra("song", "");
        }
        setResult(SONG_SEARCH_RESULT, data);
        finish();
    }
}
