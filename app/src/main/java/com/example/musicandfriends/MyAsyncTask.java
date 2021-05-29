  package com.example.musicandfriends;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.filedownloader.services.FileDownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.musicandfriends.MainActivity.TAG;

class MyAsyncTask extends AsyncTask<String, String, String> {
    private String args;
    private View view;
    private UserService service;
    private PageFragment pageFragment;
    private ImageView avatar;
    private ProfileActivity profileActivity;
    public MyAsyncTask(PageFragment pageFragment){
        this.args = pageFragment.args;
        this.service = pageFragment.service;
        this.view = pageFragment.view;
        this.pageFragment = pageFragment;
    }
    public MyAsyncTask(PageFragment pageFragment, ImageView avatar){
        this.args = pageFragment.args;
        this.service = pageFragment.service;
        this.view = pageFragment.view;
        this.pageFragment = pageFragment;
        this.avatar = avatar;
    }
    public MyAsyncTask(ProfileActivity profileActivity, ImageView avatar){
        this.profileActivity = profileActivity;
        this.service = profileActivity.service;
        this.args = profileActivity.args;
        this.avatar = avatar;
    }
    public MyAsyncTask(ProfileActivity profileActivity){
        this.profileActivity = profileActivity;
        this.service = profileActivity.service;
        this.args = profileActivity.args;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            if (args.equals("main_page")){
                Call<User> call = service.getMainPageContent(Integer.parseInt(params[0]));
                Response<User> response = call.execute();
                pageFragment.curUser = response.body();
                TextView profileName = view.findViewById(R.id.mainPageProfileName);
                profileName.setText(pageFragment.curUser.getName());
                TextView musicPreferences = view.findViewById(R.id.mainPageMusicPreferences);
                musicPreferences.setText(pageFragment.curUser.getTextMusicPreferences());
                TextView notifications = view.findViewById(R.id.mainPageNotifications);
                notifications.setText(String.valueOf(pageFragment.curUser.getNotifications()));
                TextView city = view.findViewById(R.id.mainPageCity);
                city.setText(pageFragment.curUser.getCity());
            }

            if (args.equals("profile_page")){
                Call<User> call = service.getMainPageContent(Integer.parseInt(params[0]));
                Response<User> response = call.execute();
                User user = response.body();
                profileActivity.curUser = user;
                TextView profileName = profileActivity.findViewById(R.id.profileName);
                profileName.setText(user.getName());
                TextView musicPreferences = profileActivity.findViewById(R.id.profileMusicPreferences);
                musicPreferences.setText(user.getTextMusicPreferences());
            }

            if (args.equals("get_avatar")){
                try{
                    Call<ResponseBody> call = service.downloadAvatar(Integer.parseInt(params[0]));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                File f = writeResponseBodyToDisk(response.body(), Integer.parseInt(params[0]));
                                try {
                                    f.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                avatar.setImageURI(Uri.fromFile(f));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("error", "main_page_avatar_at");
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (args.equals("friends_req_add")){
                if (Integer.parseInt(params[1]) != Integer.parseInt(params[0])){
                    Call<Boolean> call = service.friendsReqAdd(Integer.parseInt(params[1]), Integer.parseInt(params[0]));
                    Response<Boolean> response = call.execute();
                    boolean res = response.body();
                }
            }

            if (args.equals("friends_del")){
                Call<Boolean> call = service.friendsDel(Integer.parseInt(params[1]), Integer.parseInt(params[0]));
                Response<Boolean> response = call.execute();
                boolean res = response.body();
            }

            if (args.equals("get_status")){
                Call<Integer> call = service.getStatus(Integer.parseInt(params[1]), Integer.parseInt(params[0]));
                Response<Integer> response = call.execute();
                int status = response.body();
                profileActivity.setButtons(status);
            }

            if (args.equals("search_page")){
                Call<User[]> call = service.getSearchPageContent(Integer.parseInt(params[0]), 5);
                Response<User[]> response = call.execute();
                User[] userList = response.body();
                pageFragment.userList = userList;
            }
            if (args.equals("set_music_prefs")){
                service.setMusicPrefs(pageFragment.ID, pageFragment.curUser.getMusicPreferences()).execute();
            }
            if (args.equals("set_avatar")){
                service.setAvatar(pageFragment.mbp, Integer.parseInt(params[0])).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (args.equals("set_music_prefs")){
            TextView textView = pageFragment.view.findViewById(R.id.mainPageMusicPreferences);
            textView.setText(pageFragment.curUser.getTextMusicPreferences());
        }
        if (args.equals("main_page")){
            pageFragment.friends.setAdapter(new RecyclerAdapter(pageFragment.makeFriendsList()));
        }
        if (args.equals("profile_page")){
            profileActivity.friends.setAdapter(new RecyclerAdapter(profileActivity.makeFriendsList()));
        }
        if (args.equals("search_page")){
            pageFragment.listView.setAdapter(new SearchAdapter(view.getContext(), 0, pageFragment.makeUser()));
        }
        if (args.equals("friends_del")){
            try{
                args = "get_status";
                profileActivity.pd.show();
                new MyAsyncTask(profileActivity).execute(String.valueOf(profileActivity.ID), String.valueOf(profileActivity.myID));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            pageFragment.pd.cancel();
        } catch (Exception e){
            profileActivity.pd.cancel();
        }
    }

    private File writeResponseBodyToDisk(ResponseBody body, int ID) {
        File avatar = null;
        try {
            if (pageFragment != null) {
                avatar = new File(pageFragment.getContext().getCacheDir(), ID+"avatar.png");
            } else {
                avatar = new File(profileActivity.getCacheDir(), ID+"avatar.png");
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            byte[] fileReader = new byte[4096];
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(avatar);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
            }
            outputStream.flush();
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e){
            Log.d("error", "writeResponse");
            e.printStackTrace();
        }
        return avatar;
    }
}
