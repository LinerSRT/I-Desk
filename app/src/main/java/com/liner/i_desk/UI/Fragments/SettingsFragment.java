package com.liner.i_desk.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liner.i_desk.R;
import com.liner.i_desk.UI.MainActivity;
import com.liner.i_desk.UI.SplashActivity;
import com.liner.i_desk.Utils.Views.SimpleBottomSheetDialog;

public class SettingsFragment extends Fragment {
    private TextView signOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        signOut= view.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleBottomSheetDialog confirmDialog = new SimpleBottomSheetDialog(getActivity());
                confirmDialog.setDialogTitle("Выход");
                confirmDialog.setDialogText("Вы действительно хотите выйти из своего аккаунта?");
                confirmDialog.setDialogDoneBtnText("Выйти");
                confirmDialog.setDialogCancelBtnText("Отмена");
                confirmDialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirmDialog.dismiss(true);
                    }
                });
                confirmDialog.setDoneClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.getFirebaseAuth().signOut();
                        getContext().startActivity(new Intent(getContext(), SplashActivity.class));
                        getActivity().finish();
                    }
                });
                confirmDialog.create();
            }
        });
        return view;
    }

}
