package com.yeryomenkom.testmusicsloth.choose_sources;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.choose_sources.adapters.SelectedSourcesAdapter;
import ua.yeryomenkom.musicsloth.sync.SAudioAlbum;


/**
 * Created by Misha on 06.05.2015.
 */
public class SelectedVkAudioAlbumsFragment extends Fragment implements
        SelectedSourcesAdapter.SelectedSourcesAdapterListener {
    private RecyclerView recyclerView;
    private ArrayList<SAudioAlbum> selectedAudioAlbums;

    SourcesDataFragment dataFragment;

    private SelectedSourcesAdapter adapter;

    public void setSelectedAudioAlbums(ArrayList<SAudioAlbum> selectedAudioAlbums) {
        this.selectedAudioAlbums = selectedAudioAlbums;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        adapter = new SelectedSourcesAdapter(selectedAudioAlbums);
        adapter.setListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_sources,null);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_sources_FSS);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);

        recyclerView.setAdapter(adapter);

        return view;
    }

    public void albumsUnSelectedFromTreeFragment() {
        adapter.notifyDataSetChanged();
    }

    public void albumSelectedFromTreeFragment() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoveItemClicked(String uniqString) {
        dataFragment.albumUnSelectedFromSelectedFragment(uniqString);
    }
}
