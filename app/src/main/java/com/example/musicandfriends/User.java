package com.example.musicandfriends;

import android.net.Uri;

import okhttp3.Response;

import static com.example.musicandfriends.PageFragment.musicStyles;

public class User {
    public String name;
    public int ID;
    public boolean[] musicPreferences;
    public String city;
    public Integer[] friendsIDs;
    public String[] friendsNames;
    public int notifications;

    public void setName(String name) { this.name = name; }
    public void setID(int ID) { this.ID = ID; }
    public void setMusicPreferences(boolean[] musicPreferences) { this.musicPreferences = musicPreferences; }
    public void setCity(String city) { this.city = city; }
    public void setFriendsIDs(Integer[] friendsIDs) {this.friendsIDs = friendsIDs; }
    public void setFriendsNames(String[] friendsNames) { this.friendsNames = friendsNames; }
    public void setNotifications(int notifications) { this.notifications = notifications; }

    public String getName() { return name; }
    public int getID() { return ID; }
    public boolean[] getMusicPreferences() { return musicPreferences; }
    public String getTextMusicPreferences(){
        String curMusicPreferences = "";
        for (int i = 0; i < musicPreferences.length; ++i){
            if (musicPreferences[i]){
                curMusicPreferences += musicStyles[i].toLowerCase()+", ";
            }
        }
        if (!curMusicPreferences.equals("")){
            curMusicPreferences = String.valueOf((curMusicPreferences.charAt(0))).toUpperCase() + curMusicPreferences.substring(1, curMusicPreferences.length()-2);
        }
        return curMusicPreferences;
    }
    public String getCity() { return city; }
    public Integer[] getFriendsIDs() { return friendsIDs; }
    public String[] getFriendsNames() { return friendsNames; }
    public int getNotifications() { return notifications; }
}
