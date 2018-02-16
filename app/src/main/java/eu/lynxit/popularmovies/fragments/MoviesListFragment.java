package eu.lynxit.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.lynxit.popularmovies.MainActivity;
import eu.lynxit.popularmovies.adapters.MoviesRecyclerAdapter;
import eu.lynxit.popularmovies.R;
import eu.lynxit.popularmovies.api.model.MovieDTO;

/**
 * Created by lynx on 15/02/18.
 */

public class MoviesListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MoviesRecyclerAdapter mAdapter = new MoviesRecyclerAdapter();
    private MoviesRecyclerAdapter.OnItemClickListener onItemClickListener = new MoviesRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(Integer position, MovieDTO movie) {
            ((MainActivity) getActivity()).mViewModel.selectedMovie = movie;
            ((MainActivity) getActivity()).startFragment(new MovieDetailsFragment(), true, false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.fragment_movies_list_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        if(((MainActivity) getActivity()).mViewModel.movies.getValue() != null){
            if(((MainActivity) getActivity()).mViewModel.movies.getValue().size()>0) {
                reapplyMovies(((MainActivity) getActivity()).mViewModel.movies.getValue());
            } else {
                ((MainActivity) getActivity()).mViewModel.loadPopular();
            }
        } else {
            ((MainActivity) getActivity()).mViewModel.loadPopular();
        }
    }

    public void reapplyMovies(List<MovieDTO> movies) {
        mAdapter.replaceItems(movies);
    }
}
