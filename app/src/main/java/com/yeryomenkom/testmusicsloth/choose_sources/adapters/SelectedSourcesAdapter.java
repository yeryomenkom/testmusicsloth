package com.yeryomenkom.testmusicsloth.choose_sources.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.VkOfflinePlayerApplication;
import ua.yeryomenkom.musicsloth.sync.SAudioAlbum;

/**
 * Created by Misha on 06.06.2015.
 */
public class SelectedSourcesAdapter extends RecyclerView.Adapter<SelectedSourcesAdapter.ViewHolder> {
    private ArrayList<SAudioAlbum> sAudioAlbums;
    private SelectedSourcesAdapterListener listener;

    public SelectedSourcesAdapter(ArrayList<SAudioAlbum> sAudioAlbums) {
        this.sAudioAlbums = sAudioAlbums;
    }

    public void setListener(SelectedSourcesAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_selected_album, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Resources resources = VkOfflinePlayerApplication.getAppResources();
        SAudioAlbum audioAlbum = sAudioAlbums.get(position);
        holder.tittle.setText(audioAlbum.vkAudioAlbumItem.title);
        switch (audioAlbum.type) {
            case SAudioAlbum.SIMPLE_ALBUM:
                if(audioAlbum.ownerName == null) {
                    if(audioAlbum.vkAudioAlbumItem.id >= 0)
                        holder.ownerName.setText(resources.getString(R.string.Source)+resources.getString(R.string.my_page));
                    else holder.ownerName.setText(resources.getString(R.string.Source) + resources.getString(R.string.popular_music));
                } else {
                    holder.ownerName.setText(resources.getString(R.string.Source)+audioAlbum.ownerName);
                }
                holder.count.setText(resources.getString(R.string.number_of_audio) + (audioAlbum.count != 0 ?
                        audioAlbum.count : resources.getString(R.string.all)));
                break;
            case SAudioAlbum.WALL_ALBUM:
                if(audioAlbum.ownerName == null) {
                    holder.ownerName.setText(resources.getString(R.string.Source)+resources.getString(R.string.my_page));
                } else {
                    holder.ownerName.setText(resources.getString(R.string.Source)+audioAlbum.ownerName);
                }
                holder.count.setText(resources.getString(R.string.number_of_posts) + audioAlbum.count);
                break;
            case SAudioAlbum.POPULAR_ALBUM:
                holder.ownerName.setText(resources.getString(R.string.Source) + resources.getString(R.string.popular_music));
                holder.count.setText(resources.getString(R.string.number_of_audio) + audioAlbum.count);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sAudioAlbums.size();
    }

    private void onRemoveButtonClick(int position) {
        SAudioAlbum audioAlbum = sAudioAlbums.remove(position);
        notifyItemRemoved(position);
        listener.onRemoveItemClicked(audioAlbum.vkAudioAlbumItem.getUniqString());
    }

    public interface SelectedSourcesAdapterListener {
        void onRemoveItemClicked(String uniqString);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivDelete;
        TextView tittle, ownerName, count;

        public ViewHolder(View itemView) {
            super(itemView);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete_ISA);
            tittle = (TextView) itemView.findViewById(R.id.tv_album_name_ISA);
            ownerName = (TextView) itemView.findViewById(R.id.tv_album_owner_ISA);
            count = (TextView) itemView.findViewById(R.id.tv_songs_count_ISA);

            ivDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRemoveButtonClick(getPosition());
        }
    }
}
