package com.example.musicandfriends;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;

import static android.provider.CalendarContract.CalendarCache.URI;

public class MyFragment extends Fragment {
    TextView textView;
    ImageView avatar;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_page, container, false);
        avatar = view.findViewById(R.id.mainPageAvatar);
        avatar.setImageResource(R.drawable.camera_200);
        avatar.setClipToOutline(true);
        return view;
    }
}
