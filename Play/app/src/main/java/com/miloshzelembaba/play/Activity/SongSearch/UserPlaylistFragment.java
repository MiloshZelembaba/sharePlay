package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Spotify.SpotifySearch;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class UserPlaylistFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private int offset;
    private PlaylistAdapter mAdapter;

    // Views
    ListView mListView;

    public UserPlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserPlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserPlaylistFragment newInstance() {
        UserPlaylistFragment fragment = new UserPlaylistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offset = 0;
        setup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_user_playlist, container, false);
        View parent = inflater.inflate(R.layout.fragment_user_playlist, container, false);
        mListView = (ListView) parent.findViewById(R.id.playlist_listview);

        return parent;
    }

    private void setup() {
        fetchUserPLaylists(50);
    }

    private void fetchUserPLaylists(final int limit){
        SpotifySearch.getUserPlaylists(offset, limit, new SongSearchActivity.PlaylistSearchResultCallBack() {
            @Override
            public void onSuccess(ArrayList<PlaylistSimple> playlists) {
                mListView.setVisibility(View.VISIBLE);
                if (mAdapter == null) {
                    mAdapter = new PlaylistAdapter((SongSearchActivity) getContext(), 0, playlists, mListView); // are activities singletons?
                } else {
                    mAdapter.addAll(playlists);
                }
                mListView.setAdapter(mAdapter);
                mListView.setSelection(offset);

                offset += playlists.size();
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
