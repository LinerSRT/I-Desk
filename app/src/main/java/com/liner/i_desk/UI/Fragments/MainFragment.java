package com.liner.i_desk.UI.Fragments;

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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainFragment extends Fragment {
    private CircleImageView userPhoto;
    private TextView userName;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        userPhoto = view.findViewById(R.id.userPhoto);
        userName = view.findViewById(R.id.userName);
        Picasso.get().load(MainActivity.getUser().getUserPhotoURL()).into(userPhoto);
        userName.setText(MainActivity.getUser().getUserName());

        return view;
    }

}
