package eu.lynxit.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;

import eu.lynxit.popularmovies.BuildConfig;
import eu.lynxit.popularmovies.MainActivity;
import eu.lynxit.popularmovies.R;
import eu.lynxit.popularmovies.adapters.ReviewsRecyclerAdapter;
import eu.lynxit.popularmovies.adapters.VideosRecyclerAdapter;
import eu.lynxit.popularmovies.api.MovieClient;
import eu.lynxit.popularmovies.api.model.MovieDTO;
import eu.lynxit.popularmovies.api.model.ReviewResponse;
import eu.lynxit.popularmovies.api.model.VideoDTO;
import eu.lynxit.popularmovies.api.model.VideoResponse;
import eu.lynxit.popularmovies.database.MovieContract;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lynx on 15/02/18.
 */

public class MovieDetailsFragment extends Fragment {
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy:MM:dd");
    private static final String YOUTUBE_PREFIX = "https://www.youtube.com/watch?v=";
    private Boolean isFavorite = false;
    private TextView mTitle;
    private ImageView mThumbnail;
    private TextView mYear;
    private TextView mDuration;
    private TextView mRating;
    private TextView mFavorite;
    private TextView mDescription;
    private RecyclerView mVideosRecyclerView;
    private VideosRecyclerAdapter mVideosAdapter = new VideosRecyclerAdapter();
    private RecyclerView mReviewsRecyclerView;
    private ReviewsRecyclerAdapter mReviewsAdapter = new ReviewsRecyclerAdapter();
    private VideosRecyclerAdapter.OnItemClickListener mVideosOnItemClickListener = new VideosRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(Integer position, VideoDTO video) {
            shareToYoutube(video.getKey());
        }

        @Override
        public void onShareClicked(VideoDTO videoDTO) {
            shareToFriend(videoDTO.getKey());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitle = view.findViewById(R.id.fragment_movie_details_title);
        mThumbnail = view.findViewById(R.id.fragment_movie_details_thumbnail);
        mYear = view.findViewById(R.id.fragment_movie_details_year);
        mDuration = view.findViewById(R.id.fragment_movie_details_duration);
        mRating = view.findViewById(R.id.fragment_movie_details_rating);
        mFavorite = view.findViewById(R.id.fragment_movie_details_favorite);
        mDescription = view.findViewById(R.id.fragment_movie_details_description);
        final MovieDTO selectedMovie = ((MainActivity) getActivity()).mViewModel.selectedMovie;
        if (selectedMovie != null) {
            RequestBuilder builder = Glide.with(mThumbnail).load(BuildConfig.IMG_ENDPOINT + "w185" + selectedMovie.getPoster_path());
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            options.placeholder(R.drawable.ic_movie);
            builder.apply(options);
            builder.into(mThumbnail);
            mTitle.setText(selectedMovie.getTitle());
            mYear.setText(selectedMovie.getRelease_date() != null ? mDateFormat.format(selectedMovie.getRelease_date()) : "");
            //mDuration.setText(selectedMovie.getTitle()); //TODO duration was on UX but not in model
            if(selectedMovie.getVote_average()!=null) {
                mRating.setText(String.format("%1$,.2f", selectedMovie.getVote_average()));
            }
            mDescription.setText(selectedMovie.getOverview());
            mFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavorite) {
                        getActivity().getContentResolver().delete(
                                MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(selectedMovie.getId().toString()).build(),
                                MovieContract.MovieEntry.COLUMN_ID+"=?",
                                new String[]{selectedMovie.getId().toString()});
                    } else {
                        getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieToContentValues(selectedMovie));
                    }
                    isFavorite = !isFavorite;
                    toggleFavoriteContent();
                }
            });
            mVideosRecyclerView = view.findViewById(R.id.fragment_movie_details_videos);
            mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mVideosAdapter.setOnItemClickListener(mVideosOnItemClickListener);
            mVideosRecyclerView.setAdapter(mVideosAdapter);
            mReviewsRecyclerView = view.findViewById(R.id.fragment_movie_details_reviews);
            mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mReviewsRecyclerView.setAdapter(mReviewsAdapter);
            Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(selectedMovie.getId().toString()).build(), null, MovieContract.MovieEntry.COLUMN_ID, new String[]{selectedMovie.getId().toString()}, null);
            isFavorite = cursor.getCount() > 0;
            toggleFavoriteContent();
            MovieClient.getMovieClient()
                    .getVideos(((MainActivity) getActivity()).mViewModel.query, selectedMovie.getId().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<VideoResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(VideoResponse videoResponse) {
                            mVideosAdapter.replaceItems(videoResponse.getResults());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(MovieDetailsFragment.class.getName(), "onError " + e.getClass().getName());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(MovieDetailsFragment.class.getName(), "onComplete");
                        }
                    });
            MovieClient.getMovieClient().getReviews(((MainActivity) getActivity()).mViewModel.query, selectedMovie.getId().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<ReviewResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ReviewResponse reviewResponse) {
                            mReviewsAdapter.replaceItems(reviewResponse.getResults());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(MovieDetailsFragment.class.getName(), "onErrorR " + e.getClass().getName());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void toggleFavoriteContent() {
        if (isFavorite) {
            mFavorite.setText(R.string.fragment_movie_details_unmark_as_favorite);
        } else {
            mFavorite.setText(R.string.fragment_movie_details_mark_as_favorite);
        }
    }

    private void shareToYoutube(String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(YOUTUBE_PREFIX + key));
        startActivity(intent);
    }

    private void shareToFriend(String key) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, YOUTUBE_PREFIX + key);
        intent.setType("text/plain");
        startActivity(intent);
    }

    private ContentValues movieToContentValues(MovieDTO dto) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, dto.getPoster_path());
        values.put(MovieContract.MovieEntry.COLUMN_ADULT, dto.getAdult());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, dto.getOverview());
        if (dto.getRelease_date() != null) {
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mDateFormat.format(dto.getRelease_date()));
        }
        if (dto.getGenre_ids().length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer item : dto.getGenre_ids()) {
                stringBuilder.append(item);
                stringBuilder.append(",");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            values.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, stringBuilder.toString());
        }
        values.put(MovieContract.MovieEntry.COLUMN_ID, dto.getId());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, dto.getOriginal_title());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, dto.getOriginal_language());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, dto.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, dto.getBackdrop_path());
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, dto.getPopularity());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, dto.getVote_count());
        values.put(MovieContract.MovieEntry.COLUMN_VIDEO, dto.getVideo());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, dto.getVote_average());
        return values;
    }
}
