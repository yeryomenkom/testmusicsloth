package com.yeryomenkom.testmusicsloth.choose_sources;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import ua.yeryomenkom.musicsloth.OnBackClickListener;
import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.choose_playlists.PlaylistsActivity;
import ua.yeryomenkom.musicsloth.sync.SPlaylist;

/**
 * Created by Misha on 06.05.2015.
 */
public class AddNewOrEditPlaylistActivity extends AppCompatActivity implements SavePlaylistDialog.SavePlaylistDialogListener,
        OnBackClickListener {
    private String tag = getClass().getSimpleName();
    private static final String TAG_DATA_FRAGMENT = "data";
    private static final String TAG_SAVE_PLAYLIST_DIALOG = "spd";

    private ViewPager pager;
    private MaterialTabHost tabHost;
    private Button btnSave;

    SourcesDataFragment dataFragment;
    private OnBackClickListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sources);

        initializeVkSdk();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        btnSave = (Button) findViewById(R.id.btn_save_ACS);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pager.getCurrentItem() == 1) {
                    SavePlaylistDialog savePlaylistDialog = new SavePlaylistDialog();
                    savePlaylistDialog.setCurrentPlaylistName(dataFragment.currentSPlaylist.name);
                    savePlaylistDialog.show(getFragmentManager(), TAG_SAVE_PLAYLIST_DIALOG);
                } else {
                    pager.setCurrentItem(1,true);
                }
            }
        });

        boolean needMoveToSelected = false;
        dataFragment = (SourcesDataFragment) getFragmentManager().findFragmentByTag(TAG_DATA_FRAGMENT);
        if(dataFragment == null) {
            Log.d(tag,"Creating new data Fragment");
            dataFragment = new SourcesDataFragment();
            dataFragment.vkAudioAlbumsTreeFragment = new VkAudioAlbumsTreeFragment();
            dataFragment.selectedVkAudioAlbumsFragment = new SelectedVkAudioAlbumsFragment();
            dataFragment.vkAudioAlbumsTreeFragment.dataFragment = dataFragment;
            dataFragment.selectedVkAudioAlbumsFragment.dataFragment = dataFragment;
            SPlaylist sPlaylist = (SPlaylist) getIntent().getSerializableExtra(PlaylistsActivity.EXTRA_SPLAYLIST);
            needMoveToSelected = sPlaylist != null;
            dataFragment.setCurrentSPlaylist(sPlaylist);
            getFragmentManager().beginTransaction().add(dataFragment,TAG_DATA_FRAGMENT).commit();
        }

        onBackPressedListener = dataFragment.vkAudioAlbumsTreeFragment;

        VKUIHelper.onCreate(this);

        initializeToolBar();
        initializeViewPagerAndTabHost();

        if(needMoveToSelected) {
            pager.setCurrentItem(1);
        }
    }


    private void initializeToolBar(){
        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.Sources_selection));
        //toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initializeViewPagerAndTabHost() {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager );
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
                if (position == 0) {
                    btnSave.setText(R.string.Next);
                    onBackPressedListener = dataFragment.vkAudioAlbumsTreeFragment;
                } else {
                    btnSave.setText(R.string.Save);
                    onBackPressedListener = AddNewOrEditPlaylistActivity.this;
                }
            }
        });

        tabHost = (MaterialTabHost) findViewById(R.id.tabHost);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab().setText(pagerAdapter.getPageTitle(i)).setTabListener(new MaterialTabListener() {
                        @Override
                        public void onTabSelected(MaterialTab materialTab) {
                            pager.setCurrentItem(materialTab.getPosition());
                        }

                        @Override
                        public void onTabReselected(MaterialTab materialTab) {

                        }

                        @Override
                        public void onTabUnselected(MaterialTab materialTab) {

                        }
                    }));
        }

    }


    private void initializeVkSdk() {
        VKSdk.initialize(new VKSdkListener() {
            @Override
            public void onCaptchaError(VKError captchaError) {

            }

            @Override
            public void onTokenExpired(VKAccessToken expiredToken) {

            }

            @Override
            public void onAccessDenied(VKError authorizationError) {

            }
        }, "4697900", VKAccessToken.tokenFromSharedPreferences(this, VKAccessToken.ACCESS_TOKEN));
    }

    @Override
    public void savePlaylist(String name) {
        SPlaylist currentPlaylist = dataFragment.currentSPlaylist;
        currentPlaylist.name = name;

        Intent intent = new Intent();
        intent.putExtra(PlaylistsActivity.EXTRA_SPLAYLIST,currentPlaylist);
        setResult(PlaylistsActivity.RESULT_CODE_OK,intent);
        super.finish();
    }

    @Override
    public boolean onBackClicked() {
        pager.setCurrentItem(0,true);
        return false;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            if(num == 0) return dataFragment.vkAudioAlbumsTreeFragment;
            else return dataFragment.selectedVkAudioAlbumsFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0) return getString(R.string.sources_select_tab0_name);
            else return getString(R.string.sources_select_tab1_name);
        }

    }

    @Override
    public void onBackPressed() {
        if(onBackPressedListener == null || onBackPressedListener.onBackClicked())
            super.onBackPressed();
    }

    @Override
    public void finish() {
        setResult(PlaylistsActivity.RESULT_CODE_UNKNOWN);
        super.finish();
    }

}
