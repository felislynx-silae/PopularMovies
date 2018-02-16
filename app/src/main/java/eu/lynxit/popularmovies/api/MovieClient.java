package eu.lynxit.popularmovies.api;

import java.util.Map;

import eu.lynxit.popularmovies.api.model.MovieResponse;
import eu.lynxit.popularmovies.api.model.ReviewResponse;
import eu.lynxit.popularmovies.api.model.VideoResponse;
import io.reactivex.Observable;

/**
 * Created by lynx on 15/02/18.
 */

public class MovieClient {
    public static final String QUERY_KEY = "api_key";
    public static final String QUERY_LANGUAGE = "language";
    public static final String QUERY_DEFAULT_LANGUAGE = "en-US";
    public static final String QUERY_PAGE = "page";

    private static MovieClient mInstance;
    private MovieAPI mClient;

    private MovieClient() {
        mClient = RestClient.getRetrofit().create(MovieAPI.class);
    }

    public static MovieClient getMovieClient() {
        if (mInstance == null) {
            mInstance = new MovieClient();
        }
        return mInstance;
    }

    public Observable<MovieResponse> getPopular(Map<String, String> query) {
        return mClient.getPopular(query);
    }

    public Observable<MovieResponse> getTopRated(Map<String, String> query) {
        return mClient.getTopRated(query);
    }

    public Observable<VideoResponse> getVideos(Map<String, String> query, String id) {
        return mClient.getVideos(id, query);
    }

    public Observable<ReviewResponse> getReviews(Map<String, String> query, String id) {
        return mClient.getReviews(id, query);
    }
}
