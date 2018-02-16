package eu.lynxit.popularmovies.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import eu.lynxit.popularmovies.BuildConfig;
import eu.lynxit.popularmovies.R;
import eu.lynxit.popularmovies.api.model.MovieDTO;

/**
 * Created by lynx on 15/02/18.
 */

public class MoviesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieDTO> mItems = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MovieHolder) (holder)).bind(mItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void replaceItems(List<MovieDTO> newList) {
        mItems.clear();
        mItems.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailView;

        public MovieHolder(View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.listview_item_movie_thumbnail);
        }

        public void bind(final MovieDTO movieDTO, final Integer position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked(position, movieDTO);
                    }
                }
            });
            RequestBuilder builder = Glide.with(thumbnailView).load(BuildConfig.IMG_ENDPOINT+"w185" + movieDTO.getPoster_path());
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ic_movie);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            builder.apply(options);
            builder.into(thumbnailView);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Integer position, MovieDTO movie);
    }
}
/**
 * "w92", "w154", "w185", "w342", "w500", "w780", or "original"
 **/