package eu.lynxit.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.lynxit.popularmovies.R;
import eu.lynxit.popularmovies.api.model.VideoDTO;

/**
 * Created by lynx on 15/02/18.
 */

public class VideosRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VideoDTO> mItems = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((VideoHolder) (holder)).bind(mItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void replaceItems(List<VideoDTO> newList) {
        mItems.clear();
        mItems.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        private ImageView playView;
        private TextView titleView;
        private ImageView shareView;

        public VideoHolder(View itemView) {
            super(itemView);
            playView = itemView.findViewById(R.id.listview_item_video_play);
            titleView = itemView.findViewById(R.id.listview_item_video_name);
            shareView = itemView.findViewById(R.id.listview_item_video_share);
        }

        public void bind(final VideoDTO videoDTO, final Integer position) {
            titleView.setText(videoDTO.getName());
            playView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClicked(position, videoDTO);
                    }
                }
            });
            shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onShareClicked(videoDTO);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Integer position, VideoDTO video);

        void onShareClicked(VideoDTO videoDTO);
    }
}