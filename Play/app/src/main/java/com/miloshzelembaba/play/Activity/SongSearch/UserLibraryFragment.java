package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifySearch;
import com.miloshzelembaba.play.Utils.ApplicationUtil;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserLibraryFragment extends Fragment implements SongFragmentUpdate {
    private OnFragmentInteractionListener mListener;
    SongSearchAdapter songSearchAdapter; // TODO: to be deleted?
    int offset;

    // views
    ListView mListView;

    public UserLibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserLibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserLibraryFragment newInstance() {
        UserLibraryFragment fragment = new UserLibraryFragment();
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
        View parent = inflater.inflate(R.layout.fragment_user_library, container, false);
        mListView = (ListView) parent.findViewById(R.id.search_result_listview);

        setup();

        return parent;
    }

    private void setup() {
        if (ApplicationUtil.getInstance().getUser().isTemporaryUser()) {
            mListView.setVisibility(View.GONE);
            return;
        }


        fetchUserLibrary(50);

    }

    private void fetchUserLibrary(final int limit){
        SpotifySearch.getUserLibrary(offset, limit, new SongSearchActivity.SongSearchResultCallBack() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                mListView.setVisibility(View.VISIBLE);
                if (songSearchAdapter == null) {
                    songSearchAdapter = new SongSearchAdapter((SongSearchActivity) getContext(), 0, songs,mListView); // are activities singletons?
                } else {
                    songSearchAdapter.addAll(songs);
                }
                mListView.setAdapter(songSearchAdapter);
                mListView.setSelection(offset);
                offset += limit;
            }

            @Override
            public void onFailure(String errorMessage) {
                ErrorService.showErrorMessage(getContext(),
                        errorMessage,
                        ErrorService.ErrorSeverity.HIGH);
            }
        });
    }

    @Override
    public void updateFragment() {
        fetchUserLibrary(50);
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

    public interface UserLibraryCallback {
        void onResult(ArrayList<Song> songs);
    }

}
