//package com.example.recordapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.List;
//
//public class AudioFilesAdapter extends RecyclerView.Adapter<AudioFilesAdapter.AudioFileViewHolder> {
//
//    private final List<AudioFile> audioFiles;
//    private final OnItemClickListener listener;
//    private final OnDownloadClickListener downloadListener;
//
//    public interface OnItemClickListener {
//        void onItemClick(AudioFile audioFile);
//    }
//
//    public interface OnDownloadClickListener {
//        void onDownloadClick(AudioFile audioFile);
//    }
//
//    public AudioFilesAdapter(List<AudioFile> audioFiles, OnItemClickListener listener, OnDownloadClickListener downloadListener) {
//        this.audioFiles = audioFiles;
//        this.listener = listener;
//        this.downloadListener = downloadListener;
//    }
//
//    @NonNull
//    @Override
//    public AudioFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_layout, parent, false);
//        return new AudioFileViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AudioFileViewHolder holder, int position) {
//        AudioFile audioFile = audioFiles.get(position);
//        holder.bind(audioFile, listener, downloadListener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return audioFiles.size();
//    }
//
//    static class AudioFileViewHolder extends RecyclerView.ViewHolder {
//        private final TextView nameTextView;
//        private final ImageView downloadImageView;
//
//        public AudioFileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
//        }
//
//        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener) {
//            nameTextView.setText(audioFile.getName());
//            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
//            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
//        }
//    }
//}
package com.example.recordapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

//public class AudioFilesAdapter extends RecyclerView.Adapter<AudioFilesAdapter.AudioFileViewHolder> {
//
//    private final List<AudioFile> audioFiles;
//    private final OnItemClickListener listener;
//    private final OnDownloadClickListener downloadListener;
//    private final OnPlayClickListener playListener;
//    private final OnStopClickListener stopListener;
//
//    public interface OnItemClickListener {
//        void onItemClick(AudioFile audioFile);
//    }
//
//    public interface OnDownloadClickListener {
//        void onDownloadClick(AudioFile audioFile);
//    }
//
//    public interface OnPlayClickListener {
//        void onPlayClick(AudioFile audioFile, SeekBar seekBar);
//    }
//
//    public interface OnStopClickListener {
//        void onStopClick();
//    }
//
//    public AudioFilesAdapter(List<AudioFile> audioFiles, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener) {
//        this.audioFiles = audioFiles;
//        this.listener = listener;
//        this.downloadListener = downloadListener;
//        this.playListener = playListener;
//        this.stopListener = stopListener;
//    }
//
//    @NonNull
//    @Override
//    public AudioFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_layout, parent, false);
//        return new AudioFileViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AudioFileViewHolder holder, int position) {
//        AudioFile audioFile = audioFiles.get(position);
//        holder.bind(audioFile, listener, downloadListener, playListener, stopListener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return audioFiles.size();
//    }
//
//    static class AudioFileViewHolder extends RecyclerView.ViewHolder {
//        private final TextView nameTextView;
//        private final SeekBar seekBar;
//        private final ImageView playImageView;
//        private final ImageView stopImageView;
//        private final ImageView downloadImageView;
//
//        public AudioFileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            seekBar = itemView.findViewById(R.id.seekBar);
//            playImageView = itemView.findViewById(R.id.imageViewPlay);
//            stopImageView = itemView.findViewById(R.id.imageViewStop);
//            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
//        }
//
//        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener) {
//            nameTextView.setText(audioFile.getName());
//            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
//            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
//            playImageView.setOnClickListener(v -> playListener.onPlayClick(audioFile, seekBar));
//            stopImageView.setOnClickListener(v -> stopListener.onStopClick());
//        }
//    }
//}
//
public class AudioFilesAdapter extends RecyclerView.Adapter<AudioFilesAdapter.AudioFileViewHolder> {

    private final List<AudioFile> audioFiles;
    private final OnItemClickListener listener;
    private final OnDownloadClickListener downloadListener;
    private final OnPlayClickListener playListener;
    private final OnStopClickListener stopListener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(AudioFile audioFile);
    }

    public interface OnDownloadClickListener {
        void onDownloadClick(AudioFile audioFile);
    }

    public interface OnPlayClickListener {
        void onPlayClick(AudioFile audioFile, SeekBar seekBar);
    }

    public interface OnStopClickListener {
        void onStopClick();
    }

    public AudioFilesAdapter(List<AudioFile> audioFiles, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener) {
        this.audioFiles = audioFiles;
        this.listener = listener;
        this.downloadListener = downloadListener;
        this.playListener = playListener;
        this.stopListener = stopListener;
    }

    @NonNull
    @Override
    public AudioFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_layout, parent, false);
        return new AudioFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioFileViewHolder holder, int position) {
        AudioFile audioFile = audioFiles.get(position);
        holder.bind(audioFile, listener, downloadListener, playListener, stopListener, position == selectedPosition);
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onItemClick(audioFile);
        });
    }

    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    static class AudioFileViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final SeekBar seekBar;
        private final ImageView playImageView;
        private final ImageView stopImageView;
        private final ImageView downloadImageView;
        private final LinearLayout controlsLayout;

        public AudioFileViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            seekBar = itemView.findViewById(R.id.seekBar);
            playImageView = itemView.findViewById(R.id.imageViewPlay);
            stopImageView = itemView.findViewById(R.id.imageViewStop);
            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
            controlsLayout = itemView.findViewById(R.id.controlsLayout);
        }

        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener, boolean isSelected) {
            nameTextView.setText(audioFile.getName());
            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
            playImageView.setOnClickListener(v -> playListener.onPlayClick(audioFile, seekBar));
            stopImageView.setOnClickListener(v -> stopListener.onStopClick());
            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));

            seekBar.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            controlsLayout.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        }
    }
}
