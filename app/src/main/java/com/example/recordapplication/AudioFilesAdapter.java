//package com.example.recordapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.List;
//public class AudioFilesAdapter extends RecyclerView.Adapter<AudioFilesAdapter.AudioFileViewHolder> {
//
//    private final List<AudioFile> audioFiles;
//    private final OnItemClickListener listener;
//    private final OnDownloadClickListener downloadListener;
//    private final OnPlayClickListener playListener;
//    private final OnStopClickListener stopListener;
//
//    private int selectedPosition = RecyclerView.NO_POSITION;
//
//
//
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
//        holder.bind(audioFile, listener, downloadListener, playListener, stopListener, position == selectedPosition);
//        holder.itemView.setOnClickListener(v -> {
//            int previousPosition = selectedPosition;
//            selectedPosition = holder.getAdapterPosition();
//            notifyItemChanged(previousPosition);
//            notifyItemChanged(selectedPosition);
//            listener.onItemClick(audioFile);
//        });
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
//        private final LinearLayout controlsLayout;
//
//
//        public AudioFileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            seekBar = itemView.findViewById(R.id.seekBar);
//            playImageView = itemView.findViewById(R.id.imageViewPlay);
//            stopImageView = itemView.findViewById(R.id.imageViewStop);
//            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
//            controlsLayout = itemView.findViewById(R.id.controlsLayout);
//        }
//
//        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener, boolean isSelected) {
//            nameTextView.setText(audioFile.getName());
//            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
//            playImageView.setOnClickListener(v -> playListener.onPlayClick(audioFile, seekBar));
//            stopImageView.setOnClickListener(v -> stopListener.onStopClick());
//            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
//
//            seekBar.setVisibility(isSelected ? View.VISIBLE : View.GONE);
//            controlsLayout.setVisibility(isSelected ? View.VISIBLE : View.GONE);
//        }
//
//    }
//
//}
//package com.example.recordapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
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
//    private final OnPlayClickListener playListener;
//    private final OnStopClickListener stopListener;
//
//    private int selectedPosition = RecyclerView.NO_POSITION;
//    private int playingPosition = RecyclerView.NO_POSITION;
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
//        boolean isPlaying = position == playingPosition && position == selectedPosition;
//        holder.bind(audioFile, listener, downloadListener, playListener, stopListener, isPlaying, position == selectedPosition);
//
//        holder.itemView.setOnClickListener(v -> {
//            int previousSelectedPosition = selectedPosition;
//            selectedPosition = holder.getAdapterPosition();
//            notifyItemChanged(previousSelectedPosition);
//            notifyItemChanged(selectedPosition);
//            listener.onItemClick(audioFile);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return audioFiles.size();
//    }
//
//    class AudioFileViewHolder extends RecyclerView.ViewHolder {
//        private final TextView nameTextView;
//        private final SeekBar seekBar;
//        private final ImageView playImageView;
//        private final ImageView stopImageView;
//        private final ImageView downloadImageView;
//        private final LinearLayout controlsLayout;
//
//        public AudioFileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            seekBar = itemView.findViewById(R.id.seekBar);
//            playImageView = itemView.findViewById(R.id.imageViewPlay);
//            stopImageView = itemView.findViewById(R.id.imageViewStop);
//            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
//            controlsLayout = itemView.findViewById(R.id.controlsLayout);
//        }
//
//        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener, boolean isPlaying, boolean isSelected) {
//            nameTextView.setText(audioFile.getName());
//            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
//            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
//
//            playImageView.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
//            stopImageView.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
//
//            playImageView.setOnClickListener(v -> {
//                int previousPlayingPosition = playingPosition;
//                playingPosition = getAdapterPosition();
//
//                notifyItemChanged(previousPlayingPosition);
//                notifyItemChanged(playingPosition);
//
//                playListener.onPlayClick(audioFile, seekBar);
//            });
//
//            stopImageView.setOnClickListener(v -> {
//                int previousPlayingPosition = playingPosition;
//                playingPosition = RecyclerView.NO_POSITION;
//
//                notifyItemChanged(previousPlayingPosition);
//                stopListener.onStopClick();
//            });
//
//            seekBar.setVisibility(isSelected ? View.VISIBLE : View.GONE);
//            controlsLayout.setVisibility(isSelected ? View.VISIBLE : View.GONE);
//        }
//    }
//}

// seek bar progreesss update
//package com.example.recordapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
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
//    private final OnPlayClickListener playListener;
//    private final OnStopClickListener stopListener;
//
//    private int selectedPosition = RecyclerView.NO_POSITION;
//    private int playingPosition = RecyclerView.NO_POSITION;
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
//        boolean isPlaying = position == playingPosition;
//        holder.bind(audioFile, listener, downloadListener, playListener, stopListener, isPlaying, position == selectedPosition);
//
//        holder.itemView.setOnClickListener(v -> {
//            int previousSelectedPosition = selectedPosition;
//            selectedPosition = holder.getAdapterPosition();
//            notifyItemChanged(previousSelectedPosition);
//            notifyItemChanged(selectedPosition);
//            listener.onItemClick(audioFile);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return audioFiles.size();
//    }
//
//    class AudioFileViewHolder extends RecyclerView.ViewHolder {
//        private final TextView nameTextView;
//        private final SeekBar seekBar;
//        private final ImageView playImageView;
//        private final ImageView stopImageView;
//        private final ImageView downloadImageView;
//        private final LinearLayout controlsLayout;
//
//        public AudioFileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            seekBar = itemView.findViewById(R.id.seekBar);
//            playImageView = itemView.findViewById(R.id.imageViewPlay);
//            stopImageView = itemView.findViewById(R.id.imageViewStop);
//            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
//            controlsLayout = itemView.findViewById(R.id.controlsLayout);
//        }
//
//        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener, OnPlayClickListener playListener, OnStopClickListener stopListener, boolean isPlaying, boolean isSelected) {
//            nameTextView.setText(audioFile.getName());
//            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
//            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
//
//            playImageView.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
//            stopImageView.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
//
//            playImageView.setOnClickListener(v -> {
//                int previousPlayingPosition = playingPosition;
//                playingPosition = getAdapterPosition();
//
//                notifyItemChanged(previousPlayingPosition);
//                notifyItemChanged(playingPosition);
//
//                playListener.onPlayClick(audioFile, seekBar);
//            });
//
//            stopImageView.setOnClickListener(v -> {
//                int previousPlayingPosition = playingPosition;
//                playingPosition = RecyclerView.NO_POSITION;
//
//                notifyItemChanged(previousPlayingPosition);
//                stopListener.onStopClick();
//            });
//
//            seekBar.setVisibility(isSelected ? View.VISIBLE : View.GONE);
//            controlsLayout.setVisibility(isSelected ? View.VISIBLE : View.GONE);
//        }
//    }
//}
//
//package com.example.recordapplication;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Environment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.core.content.FileProvider;
//import androidx.recyclerview.widget.RecyclerView;
//import java.io.File;
//import java.io.FileOutputStream;
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
//    class AudioFileViewHolder extends RecyclerView.ViewHolder {
//        private final TextView nameTextView;
//        private final ImageView downloadImageView;
//        private final LinearLayout controlsLayout;
//
//        public AudioFileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
//            controlsLayout = itemView.findViewById(R.id.controlsLayout);
//        }
//
//        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener) {
//            nameTextView.setText(audioFile.getName());
//            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
//            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
//        }
//    }
//}
package com.example.recordapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AudioFilesAdapter extends RecyclerView.Adapter<AudioFilesAdapter.AudioFileViewHolder> {

    private final List<AudioFile> audioFiles;
    private final OnItemClickListener listener;
    private final OnDownloadClickListener downloadListener;

    public interface OnItemClickListener {
        void onItemClick(AudioFile audioFile);
    }

    public interface OnDownloadClickListener {
        void onDownloadClick(AudioFile audioFile);
    }

    public AudioFilesAdapter(List<AudioFile> audioFiles, OnItemClickListener listener, OnDownloadClickListener downloadListener) {
        this.audioFiles = audioFiles;
        this.listener = listener;
        this.downloadListener = downloadListener;
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
        holder.bind(audioFile, listener, downloadListener);
    }

    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    static class AudioFileViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final ImageView downloadImageView;

        public AudioFileViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            downloadImageView = itemView.findViewById(R.id.imageViewDownload);
        }

        public void bind(AudioFile audioFile, OnItemClickListener listener, OnDownloadClickListener downloadListener) {
            nameTextView.setText(audioFile.getName());
            downloadImageView.setOnClickListener(v -> downloadListener.onDownloadClick(audioFile));
            itemView.setOnClickListener(v -> listener.onItemClick(audioFile));
        }
    }
}
