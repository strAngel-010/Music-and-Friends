package com.example.musicandfriends;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.musicandfriends.MainActivity.APP_PREFERENCES;
import static com.example.musicandfriends.MainActivity.EMAIL;
import static com.example.musicandfriends.MainActivity.PASS;

public class PageFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String[] musicStyles = {"Фолк", "Кантри", "Латиноамериканская", "Блюз", "R&B", "Джаз", "Шансон", "Электро", "Рок", "Хип-хоп", "Регги", "Поп"};
    int ID;
    int pageNumber;
    UserService service;
    String args = "";
    static User curUser;
    User[] userList = null;
    View view = null;
    RecyclerView friends;
    ListView listView;
    PageFragment curPageFragment;
    MultipartBody.Part mbp;
    ProgressDialog pd;
    SearchAdapterItem user;
    EditText searchEditText;

    static PageFragment newInstance(int page, UserService service, int ID){
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.service = service;
        pageFragment.setArguments(arguments);
        pageFragment.ID = ID;
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        curPageFragment = this;
        pd = new ProgressDialog(getContext());
        pd.setMessage("Загрузка...");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        switch (pageNumber){
            case 0:
                {
                    view = inflater.inflate(R.layout.main_page, null);
                    friends = view.findViewById(R.id.mainPageRecyclerView);
                    LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    friends.setLayoutManager(manager);
                    args = "main_page";
                    pd.show();
                    new MyAsyncTask(this).execute(String.valueOf(ID));

                    args = "get_avatar";
                    pd.show();
                    ImageView avatar = view.findViewById(R.id.mainPageAvatar);
                    avatar.setImageResource(R.drawable.camera_200);
                    new MyAsyncTask(this, avatar).execute(String.valueOf(ID));

                    Button changeMusicPreferences = view.findViewById(R.id.changeMusicPreferences);
                    Button exit = view.findViewById(R.id.mainPageExit);
                    Button update = view.findViewById(R.id.mainPageUpdate);
                    update.setOnClickListener(this);

                    avatar.setClipToOutline(true);
                    avatar.setOnClickListener(this);
                    exit.setOnClickListener(this);
                    changeMusicPreferences.setOnClickListener(this);
                    break;
                }

                case 1:
                {
                    view = inflater.inflate(R.layout.search_page, null);
                    args = "search_page";
                    pd.show();
                    new MyAsyncTask(this).execute(String.valueOf(ID));

                    searchEditText = view.findViewById(R.id.searchEditText);
                    Button searchButton = view.findViewById(R.id.searchButton);
                    Button filterButton = view.findViewById(R.id.filterButton);
                    listView = view.findViewById(R.id.searchPageListView);
                    listView.setOnItemClickListener(this);
                    searchButton.setOnClickListener(this);
                    filterButton.setOnClickListener(this);

                    break;
                }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.changeMusicPreferences){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Выбор жанров");
            boolean[] curMusicPreferences = curUser.getMusicPreferences();
            alertDialogBuilder.setMultiChoiceItems(musicStyles, curMusicPreferences, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    curMusicPreferences[which] = isChecked;
                }
            });
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    args = "set_music_prefs";
                    curUser.setMusicPreferences(curMusicPreferences);
                    pd.show();
                    new MyAsyncTask(curPageFragment).execute("");
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (v.getId() == R.id.mainPageAvatar){
            final String[] options = {"Загрузить фото", "Удалить фото"};
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Действия")
                    .setItems(options, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent,"Select File"), 1);
                                    break;
                                }
                                case 1:
                                {
                                    ImageView avatar = view.findViewById(R.id.mainPageAvatar);
                                    avatar.setImageResource(R.drawable.camera_200);
                                    break;
                                }
                            }
                        }
                    });
            alertDialogBuilder.create().show();
        }

        if (v.getId() == R.id.searchButton){

        }

        if (v.getId() == R.id.mainPageExit){
            SharedPreferences sp = ((MainActivity)getActivity()).getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(EMAIL);
            editor.remove(PASS);
            editor.commit();
            ((MainActivity)getActivity()).finish();
            ((MainActivity)getActivity()).moveTaskToBack(true);
        }

        if (v.getId() == R.id.mainPageUpdate){
            ((MainActivity)getActivity()).pager.setAdapter(((MainActivity)getActivity()).pagerAdapter);
            String text = String.valueOf(searchEditText.getText());
            if (text != null){
                
            }
        }
    }

    public ArrayList<SearchAdapterItem> makeUser(){
        ArrayList<SearchAdapterItem> arr = new ArrayList<>();
        args = "get_avatar";
        for (int i = 0; i < userList.length; ++i){
            user = new SearchAdapterItem();
            user.ID = userList[i].getID();
            user.profileName = userList[i].getName();
            user.musicPreferences = userList[i].getTextMusicPreferences();
            user.curPageFragment = this;
            Log.d("user", user.ID+" "+user.profileName);
            arr.add(user);
        }
        return arr;
    }

    public ArrayList<SearchAdapterItem> makeFriendsList(){
        ArrayList<SearchAdapterItem> arr = new ArrayList<>();
        args = "get_avatar";
        if (curUser == null){
            Log.d("user", "null");
        } else {
            for (int i = 0; i < curUser.friendsIDs.length; ++i){
                user = new SearchAdapterItem();
                user.ID = curUser.getFriendsIDs()[i];
                user.profileName = curUser.getFriendsNames()[i];
                user.curPageFragment = this;
                arr.add(user);
            }
        }
        return arr;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null){
            Uri chosenImageUri = data.getData();
            ImageView imageView = view.findViewById(R.id.mainPageAvatar);
            imageView.setImageURI(chosenImageUri);

            File file = new File(getContext().getCacheDir(), "avatar.jpg");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap rawbitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int x = 0,
                    y = 0,
                    square = 0;
            if (rawbitmap.getWidth() < rawbitmap.getHeight()){
                x = 0;
                square = rawbitmap.getWidth();
                y = rawbitmap.getHeight()/2-square/2;
            } else {
                y = 0;
                square = rawbitmap.getHeight();
                x = rawbitmap.getWidth()/2-square/2;
            }

            Bitmap bitmap = Bitmap.createBitmap(rawbitmap, x, y, square, square);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            byte[] bitmapdata = bos.toByteArray();

            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestBody rb = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            mbp = MultipartBody.Part.createFormData("file", file.getName(), rb);
            args = "set_avatar";
            new MyAsyncTask(this).execute(String.valueOf(ID));
            PagerAdapter pa = ((MainActivity)getActivity()).pagerAdapter;
            ((MainActivity)getActivity()).pager.setAdapter(pa);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("myID", ID);
        intent.putExtra("ID", userList[position].getID());
        startActivity(intent);
    }
}
