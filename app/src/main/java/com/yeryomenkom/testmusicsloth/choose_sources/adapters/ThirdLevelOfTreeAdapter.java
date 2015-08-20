package com.yeryomenkom.testmusicsloth.choose_sources.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.sync.SAudioAlbum;
import ua.yeryomenkom.musicsloth.sync.SPlaylist;
import ua.yeryomenkom.musicsloth.vk_essences.VKAudioAlbumItem;

/**
 * Created by Misha on 24.05.2015.
 */
public class ThirdLevelOfTreeAdapter extends RecyclerView.Adapter<ThirdLevelOfTreeAdapter.ViewHolder>{
    private ArrayList<VKAudioAlbumItem> albumItems;
    private OnItemClickListener listener;
    private HashSet<String> currentPlaylistStrings;

    private SPlaylist currentPlaylist;

    public ThirdLevelOfTreeAdapter() {
        currentPlaylistStrings = new HashSet<>();
    }

    public void setAlbumItems(ArrayList<VKAudioAlbumItem> albumItems) {
        this.albumItems = albumItems;
    }

    public void setCurrentPlaylist(SPlaylist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;

        for (SAudioAlbum albumItem : currentPlaylist.albums) {
            currentPlaylistStrings.add(albumItem.vkAudioAlbumItem.getUniqString());
        }
    }

    public void deleteAlbumFromCache(String uniqString){
        currentPlaylistStrings.remove(uniqString);
        notifyDataSetChanged();
    }

    public void deleteAlbumFromCache(VKAudioAlbumItem albumItem, int position){
        currentPlaylistStrings.remove(albumItem.getUniqString());
        notifyItemChanged(position);
    }

    public void addAlbumToCache(VKAudioAlbumItem albumItem, int position) {
        currentPlaylistStrings.add(albumItem.getUniqString());
        notifyItemChanged(position);
    }


    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_third_level_vk_audio_tree, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VKAudioAlbumItem albumItem = albumItems.get(position);
        holder.tittle.setText(albumItem.title);
        holder.checkBox.setChecked(currentPlaylistStrings.
                contains(albumItem.getUniqString()));
    }

    @Override
    public int getItemCount() {
        return albumItems.size();
    }

    private void onItemClicked(int position, boolean isChecked) {
        if(listener != null)
            listener.onThirdLevelTreeItemClicked(albumItems.get(position),isChecked,position);
    }

    public interface OnItemClickListener {
        void onThirdLevelTreeItemClicked(VKAudioAlbumItem albumItem, boolean isChecked, int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBox;
        TextView tittle;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.chb_ITLVAT);
            tittle = (TextView) itemView.findViewById(R.id.tv_tittle_ITLVAT);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            boolean isChecked = checkBox.isChecked();
            if(isChecked) checkBox.setChecked(false);
            onItemClicked(getPosition(), isChecked);
        }
    }

}
