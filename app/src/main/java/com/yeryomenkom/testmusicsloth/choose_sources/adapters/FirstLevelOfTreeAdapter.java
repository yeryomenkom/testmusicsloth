package com.yeryomenkom.testmusicsloth.choose_sources.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.VkOfflinePlayerApplication;

/**
 * Created by Misha on 24.05.2015.
 */
public class FirstLevelOfTreeAdapter extends RecyclerView.Adapter<FirstLevelOfTreeAdapter.ViewHolder>{
    private OnItemClickListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_first_level_vk_audio_tree, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Resources resources = VkOfflinePlayerApplication.getAppResources();
        switch (position) {
            case 0:
                holder.tittle.setText(resources.getString(R.string.my_audio));
                break;
            case 1:
                holder.tittle.setText(resources.getString(R.string.friends_audio));
                break;
            case 2:
                holder.tittle.setText(resources.getString(R.string.groups_audio));
                break;
            case 3:
                holder.tittle.setText(resources.getString(R.string.Subscriptions_audio));
                break;
            case 4:
                holder.tittle.setText(resources.getString(R.string.popular_music));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    private void onItemClicked(int position) {
        if(listener == null) return;
        listener.onFirstLevelTreeItemClicked((byte)position);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        TextView tittle;

        public ViewHolder(View itemView) {
            super(itemView);
            tittle = (TextView) itemView.findViewById(R.id.tv_tittle_IFLVAT);

            View view = itemView.findViewById(R.id.cv_container_IFLVAT);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClicked(getPosition());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    }

    public interface OnItemClickListener {
        void onFirstLevelTreeItemClicked(byte position);
    }
}
