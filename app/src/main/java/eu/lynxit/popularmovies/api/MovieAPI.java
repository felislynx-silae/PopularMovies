package eu.lynxit.popularmovies.api;

import java.util.Map;

import eu.lynxit.popularmovies.api.model.MovieResponse;
import eu.lynxit.popularmovies.api.model.ReviewResponse;
import eu.lynxit.popularmovies.api.model.VideoResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by lynx on 15/02/18.
 */

public interface MovieAPI {
    @GET("movie/popular")
    Observable<MovieResponse> getPopular(@QueryMap Map<String, String> query);

    @GET("movie/top_rated")
    Observable<MovieResponse> getTopRated(@QueryMap Map<String, String> query);

    @GET("movie/{id}/videos")
    Observable<VideoResponse> getVideos(@Path("id") String id, @QueryMap Map<String, String> query);

    @GET("movie/{id}/reviews")
    Observable<ReviewResponse> getReviews(@Path("id") String id, @QueryMap Map<String, String> query);
}
