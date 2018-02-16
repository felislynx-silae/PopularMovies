package eu.lynxit.popularmovies;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.lynxit.popularmovies.api.MovieClient;
import eu.lynxit.popularmovies.api.model.MovieDTO;
import eu.lynxit.popularmovies.api.model.MovieResponse;
import eu.lynxit.popularmovies.database.MovieContract;
import eu.lynxit.popularmovies.database.MovieProvider;
import eu.lynxit.popularmovies.fragments.MovieDetailsFragment;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lynx on 15/02/18.
 */

public class MainViewModel extends ViewModel {
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy:MM:dd");
    public Map<String, String> query = new HashMap<>();
    public MovieDTO selectedMovie = null;
    public MutableLiveData<List<MovieDTO>> movies = new MutableLiveData<>();

    public MainViewModel() {
        query.put(MovieClient.QUERY_KEY, BuildConfig.API_KEY);
    }

    public void loadPopular() {
        MovieClient.getMovieClient()
                .getPopular(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {
                        movies.setValue(movieResponse.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(MovieDetailsFragment.class.getName(), "Error " + e.getClass().getName() + ", " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadTopRated() {
        MovieClient.getMovieClient()
                .getTopRated(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {
                        movies.setValue(movieResponse.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(MovieDetailsFragment.class.getName(), "Error " + e.getClass().getName() + ", " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadFavorites(Context context) {
        Cursor dbCursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if(dbCursor.getCount()>0) {
            dbCursor.moveToFirst();
            List<MovieDTO> movieDTOs = new ArrayList<>();
            do {
                movieDTOs.add(convertFromCursorToMovie(dbCursor));
            } while (dbCursor.moveToNext());
            movies.setValue(movieDTOs);
            dbCursor.close();
        } else {
            movies.setValue(new ArrayList<MovieDTO>());
        }
    }

    private MovieDTO convertFromCursorToMovie(Cursor cursor) {
        MovieDTO dto = new MovieDTO();
        dto.setPoster_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
        dto.setAdult(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ADULT)) == 1);
        dto.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
        try {
            Date date = mDateFormat.parse(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            dto.setRelease_date(date);
        } catch (ParseException exception) {
            Log.d("VieModel", "ParseException");
        }
        try {
            String genreIds = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_GENRE_IDS));
            String[] genreArray = genreIds.split(",");
            Integer[] genreIdsInt = new Integer[genreArray.length];
            for (int i = 0; i < genreArray.length; i++) {
                genreIdsInt[i] = Integer.parseInt(genreArray[i]);
            }
            dto.setGenre_ids(genreIdsInt);
        } catch (Exception exception) {
            Log.d("VieModel", "GenreParseException");
        }
        dto.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
        dto.setOriginal_title(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
        dto.setOriginal_language(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE)));
        dto.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        dto.setBackdrop_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
        dto.setPopularity(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)));
        dto.setVote_count(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)));
        dto.setVideo(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VIDEO)) == 1);
        dto.setVote_average(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
        return dto;
    }
}
