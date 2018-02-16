package eu.lynxit.popularmovies.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lynx on 15/02/18.
 */

public abstract class ServerCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {
            onSuccess(call, response);
        } else {
            error(response.message());
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        failure(t);
    }


    abstract void onSuccess(Call<T> call, Response<T> response);

    abstract void failure(Throwable throwable);

    abstract void error(String message);
}
