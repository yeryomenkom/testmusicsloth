package com.yeryomenkom.testmusicsloth.choose_sources;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import ua.yeryomenkom.musicsloth.OnBackClickListener;
import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.VkOfflinePlayerApplication;
import ua.yeryomenkom.musicsloth.choose_sources.adapters.FirstLevelOfTreeAdapter;
import ua.yeryomenkom.musicsloth.choose_sources.adapters.SecondLevelOfTreeAdapter;
import ua.yeryomenkom.musicsloth.choose_sources.adapters.ThirdLevelOfTreeAdapter;
import ua.yeryomenkom.musicsloth.sync.SAudioAlbum;
import ua.yeryomenkom.musicsloth.vk_essences.VKAudioAlbumItem;

/**
 * Created by Misha on 06.05.2015.
 */
public class VkAudioAlbumsTreeFragment extends Fragment implements SourcesDataFragment.SourcesAvailableListener,
        OnBackClickListener, FirstLevelOfTreeAdapter.OnItemClickListener,
        SecondLevelOfTreeAdapter.OnItemClickListener, ThirdLevelOfTreeAdapter.OnItemClickListener, AddAlbumDialog.AddAlbumDialogListener {

    private String tag = getClass().getSimpleName();
    //константы уровней дерева
    public static final byte FIRST_LEVEL = 1, SECOND_LEVEL = 2, THIRD_LEVEL = 3;
    //константы для первого уровня дерева
    public static final byte MY_AUDIO = 0, FRIENDS = 1, GROUPS = 2, SUBSCRIPTIONS = 3, POPULAR = 4;
    //коды запросов для второго уровня дерева
    public static final int GET_GROUPS = 1, GET_FRIENDS = 2, GET_SUBSCRIPTIONS = 3;

    public static final String TAG_DIALOG = "di";

    private int lastSendingRequestCodeForSecondLevel;
    private long lastSendingRequestCodeForThirdLevel;
    SourcesDataFragment dataFragment;

    private EditText etSearch;

    //метки по которым определяется текущий альбом
    byte firstLevelType; // определяется константами 1го уровня дерева

    private VkSource currentVKSourceOfSecondLevel;

    byte currentLevel = FIRST_LEVEL;

    private ProgressWheel progressWheel;
    private RecyclerView recyclerView;
    private LinearLayout container;

    private FirstLevelOfTreeAdapter firstLevelOfTreeAdapter;
    private SecondLevelOfTreeAdapter secondLevelOfTreeAdapter;
    private ThirdLevelOfTreeAdapter thirdLevelOfTreeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        thirdLevelOfTreeAdapter = new ThirdLevelOfTreeAdapter();
        thirdLevelOfTreeAdapter.setListener(this);
        thirdLevelOfTreeAdapter.setCurrentPlaylist(dataFragment.currentSPlaylist);

        secondLevelOfTreeAdapter = new SecondLevelOfTreeAdapter();
        secondLevelOfTreeAdapter.setListener(this);

        firstLevelOfTreeAdapter = new FirstLevelOfTreeAdapter();
        firstLevelOfTreeAdapter.setListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vk_audio_tree, null);

        etSearch = (EditText) view.findViewById(R.id.et_search_FVAT);
        this.container = (LinearLayout) view.findViewById(R.id.ll_container_FVAT);

        progressWheel = (ProgressWheel) view.findViewById(R.id.pw_waiting_FVAT);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_tree_FVAT);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);

        dataFragment.setSourcesAvailableListener(this);

        switch (currentLevel) {
            case FIRST_LEVEL:
                setFirstLevelAdapter();
                break;
            case SECOND_LEVEL:
                showProgressBar();
                getSourcesForSecondLevel();
                break;
            case THIRD_LEVEL:
                showProgressBar();
                getSourcesForThirdLevel();
                break;
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                secondLevelOfTreeAdapter.filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        AddAlbumDialog dialog = (AddAlbumDialog) getFragmentManager().findFragmentByTag(TAG_DIALOG);
        if (dialog != null) {
            dialog.setListener(this);
        }

        return view;
    }

    public void getSourcesForThirdLevel() {
        if(firstLevelType == POPULAR) {
            dataFragment.getPopularAlbums(0);
        } else {
            dataFragment.getAlbumsList(lastSendingRequestCodeForThirdLevel);
        }
    }

    private void getSourcesForSecondLevel() {
        switch (lastSendingRequestCodeForSecondLevel) {
            case GET_GROUPS:
                dataFragment.getGroups(GET_GROUPS);
                break;
            case GET_FRIENDS:
                dataFragment.getFriends(GET_FRIENDS);
                break;
            case GET_SUBSCRIPTIONS:
                dataFragment.getSubscriptions(GET_SUBSCRIPTIONS);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        dataFragment.setSourcesAvailableListener(null);
        super.onDestroyView();
    }

    private void showProgressBar() {
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.spin();
        container.setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        progressWheel.setInstantProgress(0);
        progressWheel.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    private void setFirstLevelAdapter() {
        etSearch.setVisibility(View.GONE);
        recyclerView.setAdapter(firstLevelOfTreeAdapter);
        hideProgressBar();
    }

    private void setSecondLevelAdapter(ArrayList<VkSource> vkSources) {
        etSearch.setVisibility(View.VISIBLE);
        if(vkSources == null) {
            vkSources = new ArrayList<>();
            Toast.makeText(getActivity(), VkOfflinePlayerApplication.getAppResources()
                    .getString(R.string.error_while_getting_sources_list),Toast.LENGTH_SHORT).show();
        }

        secondLevelOfTreeAdapter.setSources(vkSources);
        recyclerView.setAdapter(secondLevelOfTreeAdapter);
        hideProgressBar();
    }

    private void setThirdLevelAdapter(ArrayList<VKAudioAlbumItem> albums) {
        etSearch.setVisibility(View.GONE);
        if(albums == null) {
            albums = new ArrayList<>();
            Toast.makeText(getActivity(), VkOfflinePlayerApplication.getAppResources()
                    .getString(R.string.error_while_getting_info),Toast.LENGTH_SHORT).show();
        }

        thirdLevelOfTreeAdapter.setAlbumItems(albums);
        recyclerView.setAdapter(thirdLevelOfTreeAdapter);
        hideProgressBar();
    }

    @Override
    public void secondLevelSourcesAvailable(int requestCode, ArrayList<VkSource> vkSources) {
        if(currentLevel != SECOND_LEVEL) return;
        switch (requestCode) {
            case GET_GROUPS:
                if(lastSendingRequestCodeForSecondLevel == GET_GROUPS) {
                    setSecondLevelAdapter(vkSources);
                }
                break;
            case GET_FRIENDS:
                if(lastSendingRequestCodeForSecondLevel == GET_FRIENDS) {
                    setSecondLevelAdapter(vkSources);
                }
                break;
            case GET_SUBSCRIPTIONS:
                if(lastSendingRequestCodeForSecondLevel == GET_SUBSCRIPTIONS) {
                    setSecondLevelAdapter(vkSources);
                }
                break;
        }
    }

    @Override
    public void thirdLevelSourcesAvailable(long requestCode, ArrayList<VKAudioAlbumItem> albums) {
        if(currentLevel != THIRD_LEVEL || lastSendingRequestCodeForThirdLevel != requestCode) return;
        setThirdLevelAdapter(albums);
    }

    @Override
    public boolean onBackClicked() {
        Log.d(tag, "onBackClicked cur level " + currentLevel);
        if(currentLevel == FIRST_LEVEL) return true;

        if(currentLevel == SECOND_LEVEL || firstLevelType == MY_AUDIO || firstLevelType == POPULAR) {
            currentLevel = FIRST_LEVEL;
            setFirstLevelAdapter();
        } else {
            currentLevel = SECOND_LEVEL;
            etSearch.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(secondLevelOfTreeAdapter);
            hideProgressBar();
        }

        return false;
    }

    @Override
    public void onFirstLevelTreeItemClicked(byte position) {
        showProgressBar();
        switch (position) {
            case POPULAR:
                currentLevel = THIRD_LEVEL;
                firstLevelType = POPULAR;
                //переходим сразу на третий уровень
                //в качестве id владельца указываем 0
                currentVKSourceOfSecondLevel = null;
                //кодом запроса будет являться id источника
                lastSendingRequestCodeForThirdLevel = 0;
                dataFragment.getPopularAlbums(0);
                break;
            case MY_AUDIO:
                currentLevel = THIRD_LEVEL;
                firstLevelType = MY_AUDIO;
                //переходим сразу на третий уровень
                //в качестве id владельца указываем 0
                currentVKSourceOfSecondLevel = null;
                //кодом запроса будет являться id источника
                lastSendingRequestCodeForThirdLevel = 0;
                dataFragment.getAlbumsList(0);
                break;
            case FRIENDS:
                currentLevel = SECOND_LEVEL;
                firstLevelType = FRIENDS;
                lastSendingRequestCodeForSecondLevel = GET_FRIENDS;
                dataFragment.getFriends(GET_FRIENDS);
                break;
            case GROUPS:
                currentLevel = SECOND_LEVEL;
                firstLevelType = GROUPS;
                lastSendingRequestCodeForSecondLevel = GET_GROUPS;
                dataFragment.getGroups(GET_GROUPS);
                break;
            case SUBSCRIPTIONS:
                currentLevel = SECOND_LEVEL;
                firstLevelType = SUBSCRIPTIONS;
                lastSendingRequestCodeForSecondLevel = GET_SUBSCRIPTIONS;
                dataFragment.getSubscriptions(GET_SUBSCRIPTIONS);
                break;
        }
    }

    @Override
    public void onSecondLevelTreeItemClicked(VkSource vkSource) {
        showProgressBar();
        currentLevel = THIRD_LEVEL;
        currentVKSourceOfSecondLevel = vkSource;
        //кодом запроса будет являться id источника
        lastSendingRequestCodeForThirdLevel = vkSource.getID();
        dataFragment.getAlbumsList(vkSource.getID());
    }

    String getCurrentVkSourceOfSecondLevelTitle() {
        return currentVKSourceOfSecondLevel.getName();
    }

    @Override
    public void onThirdLevelTreeItemClicked(VKAudioAlbumItem albumItem, boolean isChecked, int position) {
        if(!isChecked) {
            showAddAlbumDialog(albumItem,position);
        } else {
            //удалим текущий альбом из плейлиста
            dataFragment.currentSPlaylist.deleteAlbum(albumItem);
            thirdLevelOfTreeAdapter.deleteAlbumFromCache(albumItem, position);
            dataFragment.albumUnSelectedFromTreeFragment();
        }
    }

    private void showAddAlbumDialog(VKAudioAlbumItem albumItem,int position) {
        String tittle = currentVKSourceOfSecondLevel == null ? null : currentVKSourceOfSecondLevel.getName();
        int albumType = firstLevelType == POPULAR ? SAudioAlbum.POPULAR_ALBUM :
                albumItem.id == VKAudioAlbumItem.WALL_ALBUM ? SAudioAlbum.WALL_ALBUM : SAudioAlbum.SIMPLE_ALBUM;
        AddAlbumDialog albumDialog = AddAlbumDialog.newInstance(tittle,position,albumType,albumItem);
        albumDialog.setListener(this);
        albumDialog.show(getFragmentManager(), TAG_DIALOG);
    }

    @Override
    public void onAlbumAdded(SAudioAlbum sAudioAlbum, int position) {
        dataFragment.currentSPlaylist.addAlbum(sAudioAlbum);
        thirdLevelOfTreeAdapter.addAlbumToCache(sAudioAlbum.vkAudioAlbumItem, position);
        dataFragment.albumSelectedFromTreeFragment();
    }

    public void albumUnSelectedFromSelectedFragment(String uniqAlbumString) {
        thirdLevelOfTreeAdapter.deleteAlbumFromCache(uniqAlbumString);
    }
}
