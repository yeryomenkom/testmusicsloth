package com.yeryomenkom.testmusicsloth.choose_sources.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.VkOfflinePlayerApplication;
import ua.yeryomenkom.musicsloth.choose_sources.VkSource;

/**
 * Created by Misha on 24.05.2015.
 */
public class SecondLevelOfTreeAdapter extends RecyclerView.Adapter<SecondLevelOfTreeAdapter.ViewHolder>{
    private ArrayList<VkSource> sources, visibleSources;
    private OnItemClickListener listener;

    public SecondLevelOfTreeAdapter() {
        visibleSources = new ArrayList<>();
    }

    public void setSources(ArrayList<VkSource> sources) {
        this.sources = sources;
        visibleSources.clear();
        visibleSources.addAll(sources);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_second_level_vk_audio_tree, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VkSource vkSource = visibleSources.get(position);
        holder.tittle.setText(vkSource.getName());

        Glide.with(VkOfflinePlayerApplication.mainContext)
                .load(vkSource.getImageUrl())
                .placeholder(vkSource.getID() > 0 ? R.drawable.camera_200 : R.drawable.community_200)
                .into(holder.icon);

    }

    @Override
    public int getItemCount() {
        return visibleSources.size();
    }

    private void onItemClicked(int position) {
        if(listener != null) {
            listener.onSecondLevelTreeItemClicked(visibleSources.get(position));
        }
    }

    public void filter(CharSequence s) {
        if(sources == null) return;
        visibleSources.clear();
        if (s.length()==0) {
            visibleSources.addAll(sources);
        } else {
            String str = s.toString().toLowerCase();
            for (VkSource info : sources)
                if(info.getName().toLowerCase().contains(str)) visibleSources.add(info);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onSecondLevelTreeItemClicked(VkSource vkSource);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView tittle;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon_ISLVAT);
            tittle = (TextView) itemView.findViewById(R.id.tv_tittle_ISLVAT);

            itemView.findViewById(R.id.ll_container_ISLVAT).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClicked(getPosition());
        }
    }

}
