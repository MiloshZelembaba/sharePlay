package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Spotify.SpotifySearch;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SongFragmentUpdate{
    SongSearchAdapter songSearchAdapter; // TODO: to be deleted?
    ListView mListView;
    EditText mSearchField;
    TextView mEmptyListview;

    int offset;
    String query;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offset = 0;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_search, container, false);
        mListView = (ListView) parent.findViewById(R.id.search_result_listview);
        mSearchField = (EditText) parent.findViewById(R.id.song_search);
        mEmptyListview = (TextView) parent.findViewById(R.id.empty_listview_text);

        setupViews();

        return parent;
    }

    private void setupViews() {
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
                            if (songSearchAdapter != null) {
                                songSearchAdapter.clear();
                            }
                            offset = 0;
                            searchSpotify(mSearchField.getText().toString(), 50);
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
    }

    private void searchSpotify(String q, final int limit){
        query = q;
        SpotifySearch.getResults(query, offset, limit, new SongSearchActivity.SongSearchResultCallBack() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                mEmptyListview.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                if (songSearchAdapter == null) {
                    songSearchAdapter = new SongSearchAdapter((SongSearchActivity) getContext(), 0, songs, mListView); // are activities singletons?
                } else {
                    songSearchAdapter.addAll(songs);
                }
                mListView.setAdapter(songSearchAdapter);
                mListView.setSelection(offset);
                offset += songs.size();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (errorMessage.contains("401")) {
                    // TODO
                    SpotifyManager.getAuthCode(); // this is a temporary fix
                    // this is the fix that should work but it doesn't
//                    RefreshSpotifyAccessTokenService service = new RefreshSpotifyAccessTokenService();
//                    service.requestService();
                }
                ErrorService.showErrorMessage(getContext(),
                        errorMessage,
                        ErrorService.ErrorSeverity.HIGH);
            }
        });
    }

    @Override
    public void updateFragment() {
        searchSpotify(query, 50);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
