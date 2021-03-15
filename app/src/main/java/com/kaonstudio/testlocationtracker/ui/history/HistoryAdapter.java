package com.kaonstudio.testlocationtracker.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kaonstudio.testlocationtracker.R;
import com.kaonstudio.testlocationtracker.domain.TrackDomain;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemHolder> {

    public final static String DATE_FORMAT_FULL = "yyyy-mm-dd hh:mm:ss";
    private List<TrackDomain> tracks = new ArrayList<>();
    private OnTrackClickListener listener;

    public HistoryAdapter(OnTrackClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new ItemHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemHolder holder, int position) {
        holder.bind(tracks.get(position));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView nameTv;
        private final TextView countTv;
        private final TextView dateTv;
        private final OnTrackClickListener listener;

        public ItemHolder(@NonNull @NotNull View itemView, OnTrackClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.item_track_name);
            countTv = itemView.findViewById(R.id.item_track_count);
            dateTv = itemView.findViewById(R.id.item_track_date);
            this.listener = listener;
        }


        void bind(TrackDomain trackDomain) {
            itemView.setOnClickListener(v -> {
                listener.onTrackClicked(trackDomain);
            });
            nameTv.setText(itemView.getContext().getString(R.string.item_track_name, trackDomain.name));
            countTv.setText(itemView.getContext().getString(R.string.item_track_count, trackDomain.coordinates.size()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(trackDomain.date);
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.getDefault());
            dateTv.setText(itemView.getContext().getString(R.string.item_track_date, dateFormat.format(calendar.getTime())));
        }
    }

    public void setTracks(List<TrackDomain> newTracks) {
        final DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return tracks.size();
            }

            @Override
            public int getNewListSize() {
                return newTracks.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return tracks.get(oldItemPosition).name.equals(newTracks.get(newItemPosition).name);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return tracks.get(oldItemPosition).hashCode() == newTracks.get(newItemPosition).hashCode();
            }
        };
        final DiffUtil.DiffResult duffResult = DiffUtil.calculateDiff(diffCallback);
        tracks = newTracks;
        duffResult.dispatchUpdatesTo(this);
    }

    public interface OnTrackClickListener {
        void onTrackClicked(TrackDomain track);
    }
}
