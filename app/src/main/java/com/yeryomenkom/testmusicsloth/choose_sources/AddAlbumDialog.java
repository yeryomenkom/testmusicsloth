package com.yeryomenkom.testmusicsloth.choose_sources;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.VkOfflinePlayerApplication;
import ua.yeryomenkom.musicsloth.sync.SAudioAlbum;
import ua.yeryomenkom.musicsloth.vk_essences.VKAudioAlbumItem;

/**
 * Created by Misha on 05.06.2015.
 */
public class AddAlbumDialog extends DialogFragment implements View.OnClickListener {
    private static final int MAX_COUNT_ALL_AUDIO_ALBUM = 2000, MAX_COUNT_WALL_ALBUM = 100, MAX_COUNT_POPULAR_ALBUM = 100;
    private RadioGroup radioGroup;
    private EditText editText;

    private AddAlbumDialogListener listener;

    private boolean isAllSongs;

    public void setListener(AddAlbumDialogListener listener) {
        this.listener = listener;
    }

    public static AddAlbumDialog newInstance(String tittle, int itemPosition, int albumType, VKAudioAlbumItem audioAlbumItem) {
        Bundle args = new Bundle();
        args.putSerializable("al", audioAlbumItem);
        args.putString("t", tittle);
        args.putInt("i", itemPosition);
        args.putInt("a", albumType);

        AddAlbumDialog dialog = new AddAlbumDialog();
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getResources().getString(R.string.dialog_adding_audio_album));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_adding_vk_album, null);

        view.findViewById(R.id.btn_cancel_DAVA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_ok_DAVA).setOnClickListener(this);

        radioGroup = (RadioGroup) view.findViewById(R.id.rg_cont_DAVA);

        editText = (EditText) view.findViewById(R.id.et_n_songs_DAVA);

        switch (getArguments().getInt("a")) {
            case SAudioAlbum.SIMPLE_ALBUM:
                onCreateViewForSimpleAlbum();
                break;
            case SAudioAlbum.WALL_ALBUM:
                onCreateViewForWallAlbum(view);
                break;
            case SAudioAlbum.POPULAR_ALBUM:
                onCreateViewForPopularAlbum(view);
                break;
        }

        if(((VKAudioAlbumItem) getArguments().getSerializable("al")).id == 0) {
            onCreateViewForAllAudioAlbum();
        }


        return view;
    }

    private void onCreateViewForPopularAlbum(View view) {
        editText.setEnabled(true);
        isAllSongs = false;
        radioGroup.setVisibility(View.GONE);
    }

    private void onCreateViewForSimpleAlbum() {
        isAllSongs = true;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_all_songs_DAVA:
                        isAllSongs = true;
                        editText.setEnabled(false);
                        break;
                    case R.id.rb_last_n_songs_DAVA:
                        isAllSongs = false;
                        editText.setEnabled(true);
                        break;
                }
            }
        });
    }

    private void onCreateViewForAllAudioAlbum() {
        editText.setEnabled(true);
        isAllSongs = false;
        radioGroup.setVisibility(View.GONE);
    }

    private void onCreateViewForWallAlbum(View view) {
        editText.setEnabled(true);
        isAllSongs = false;
        radioGroup.setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tv_msg_DAVA)).setText(R.string.dialog_add_album_msg_wall);
        ((TextView) view.findViewById(R.id.tv_msg2_DAVA)).setText(R.string.posts);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onClick(View v) {
        String txt = editText.getText().toString();
        if (TextUtils.isEmpty(txt) && !isAllSongs) {
            Toast.makeText(getActivity(), getResources().getString(R.string.dialog_adding_audio_album_err_msg),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int count;
        if (isAllSongs) count = SAudioAlbum.COUNT_ALL;
        else {
            try {
                count = Integer.parseInt(txt);
                if (count <= 0) throw new Exception("count меньше ноля или ноль");
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getActivity(), getResources().getString(R.string.dialog_adding_audio_album_err_msg),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Bundle args = getArguments();
        VKAudioAlbumItem vkAudioAlbumItem = (VKAudioAlbumItem) args.getSerializable("al");

        if(args.getInt("a") == SAudioAlbum.WALL_ALBUM && count > MAX_COUNT_WALL_ALBUM) {
            Toast.makeText(getActivity(),VkOfflinePlayerApplication.getAppResources().getString(R.string.max_available_count)+MAX_COUNT_WALL_ALBUM, Toast.LENGTH_SHORT).show();
            return;
        } else if(vkAudioAlbumItem.id == 0 && count > MAX_COUNT_ALL_AUDIO_ALBUM) {
            Toast.makeText(getActivity(),VkOfflinePlayerApplication.getAppResources().getString(R.string.max_available_count)+MAX_COUNT_ALL_AUDIO_ALBUM, Toast.LENGTH_SHORT).show();
            return;
        } else if(args.getInt("a") == SAudioAlbum.POPULAR_ALBUM && count > MAX_COUNT_POPULAR_ALBUM) {
            Toast.makeText(getActivity(),VkOfflinePlayerApplication.getAppResources().getString(R.string.max_available_count)+MAX_COUNT_POPULAR_ALBUM, Toast.LENGTH_SHORT).show();
            return;
        } else if(args.getInt("a") == SAudioAlbum.SIMPLE_ALBUM && count > MAX_COUNT_ALL_AUDIO_ALBUM) {
            Toast.makeText(getActivity(), VkOfflinePlayerApplication.getAppResources().getString(R.string.max_available_count)+MAX_COUNT_ALL_AUDIO_ALBUM, Toast.LENGTH_SHORT).show();
            return;
        }


        SAudioAlbum sAudioAlbum = new SAudioAlbum();
        sAudioAlbum.vkAudioAlbumItem = vkAudioAlbumItem;
        sAudioAlbum.type = args.getInt("a");

        sAudioAlbum.ownerName = args.getString("t");
        sAudioAlbum.count = count;

        listener.onAlbumAdded(sAudioAlbum, args.getInt("i"));
        dismiss();
    }

    interface AddAlbumDialogListener {
        void onAlbumAdded(SAudioAlbum sAudioAlbum, int position);
    }
}
