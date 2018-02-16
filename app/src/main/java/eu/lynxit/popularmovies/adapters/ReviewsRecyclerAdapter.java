package eu.lynxit.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.lynxit.popularmovies.R;
import eu.lynxit.popularmovies.api.model.ReviewDTO;

/**
 * Created by lynx on 15/02/18.
 */

public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ReviewDTO> mItems = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ReviewHolder) (holder)).bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void replaceItems(List<ReviewDTO> newList) {
        mItems.clear();
        mItems.addAll(newList);
        notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        private TextView whoView;
        private TextView whatView;

        public ReviewHolder(View itemView) {
            super(itemView);
            whoView = itemView.findViewById(R.id.listview_item_review_who);
            whatView = itemView.findViewById(R.id.listview_item_review_what);
        }

        public void bind(final ReviewDTO reviewDTO) {
            whoView.setText(reviewDTO.getAuthor());
            whatView.setText(reviewDTO.getContent());
        }
    }
}