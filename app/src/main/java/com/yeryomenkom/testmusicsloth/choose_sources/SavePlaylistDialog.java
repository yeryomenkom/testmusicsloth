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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ua.yeryomenkom.musicsloth.R;

/**
 * Created by Misha on 07.06.2015.
 */
public class SavePlaylistDialog extends DialogFragment {
    private EditText editText;
    private String currentPlaylistName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.setTitle(getResources().getString(R.string.dialog_saving_playlist_tittle));
        return dialog;
    }

    public void setCurrentPlaylistName(String currentPlaylistName) {
        this.currentPlaylistName = currentPlaylistName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_save_playlist,null);

        editText = (EditText) view.findViewById(R.id.et_playlist_name_DSP);
        if(currentPlaylistName != null) {
            editText.setText(currentPlaylistName);
        }

        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel_DSP);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button btnSave = (Button) view.findViewById(R.id.btn_save_DSP);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plName = editText.getText().toString();
                if (TextUtils.isEmpty(plName)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.dialog_saving_playlist_toast_err_msg), Toast.LENGTH_SHORT).show();
                } else {
                    ((SavePlaylistDialogListener) getActivity()).savePlaylist(plName);
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    interface SavePlaylistDialogListener {
        void savePlaylist(String albumName);
    }
}
