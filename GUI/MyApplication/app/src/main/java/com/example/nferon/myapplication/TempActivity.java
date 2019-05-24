package com.example.nferon.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.util.Log;

import communication.NetworkAccess;
import communication.Schedule;

public class TempActivity extends AppCompatActivity {
    Schedule schedule;
    String[] roomsArray;
    int[] tempArray;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        schedule = NetworkAccess.getSchedule();
        roomsArray = schedule.getRooms();
        tempArray = schedule.getTemps();
        adapter = new CustomAdapter(roomsArray, tempArray, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);



        // populate listView
        ListView listView = (ListView) findViewById(R.id.temp_list);
        listView.setAdapter(adapter);
    }
}
