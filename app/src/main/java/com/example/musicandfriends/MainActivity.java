package com.example.musicandfriends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private String[] pages = {"Мой профиль", "Поиск"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ItemAdapter itemAdapter = new ItemAdapter(this, make_items(pages));
        mDrawerList.setAdapter(itemAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                MyFragment fragment = new MyFragment();
                transaction.replace(R.id.fragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                 */
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                MyFragment fragment = new MyFragment();
                transaction.add(R.id.content_frame, fragment);
                transaction.commit();
            }
        });
    }

    ListItem[] make_items(String[] text_arr){
        ListItem[] listItems = new ListItem[2];
        for (int i = 0; i < text_arr.length; ++i){
            ListItem listItem = new ListItem(text_arr[i]);
            listItems[i] = listItem;
        }
        return listItems;
    }
}