package com.example.nferon.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.util.Log;

import communication.NetworkAccess;
import communication.Schedule;
import communication.TemperatureChange;
import android.widget.AdapterView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.view.View.OnClickListener;

public class ScheduleActivity extends AppCompatActivity {
    static final int HOURS_IN_DAY = 24;
    Schedule schedule;
    String[] list;
    String[] listRooms;
    private PopupWindow pwTemperatures;
    private PopupWindow pwRooms;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        schedule = NetworkAccess.getSchedule();
        list = new String[HOURS_IN_DAY];
        listRooms = schedule.getRooms();
        initList(schedule);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        populateListView();
    }

    private void initList(Schedule schedule){
        TemperatureChange[] tempList = schedule.getChanges();
        for (TemperatureChange change : tempList){Log.d("CHANGE", "ScheduleActivity:57 -> room: "+change.getRoom()+"  temperature: "+change.getTemperature() + "  time: "+change.getTime());}
        int actualIndex = 0;
        int tempListsize = tempList.length;
        String listItem = "";
        for (int actualHour = 0; actualHour<HOURS_IN_DAY; actualHour++){ // for each hour
            listItem += Integer.toString(actualHour) + "h:\n";
            for (int j = actualIndex; j<=actualIndex && j<tempListsize; j++){
                if (actualHour*60 <= tempList[j].getTime() && tempList[j].getTime() < (actualHour+1)*60){
                    //listItem += tempList[j].getRoom() + "  " + Integer.toString((int)tempList[j].getTemperature())+"\n";
                    listItem += tempList[j].getRoom() + "  " + ((int)tempList[j].getTemperature()==0 ? "OFF" : (int)tempList[j].getTemperature() + "°") +"\n";
                    actualIndex++;
                }
            }
            list[actualHour] = listItem;
            listItem = "";
        }
    }

    private void populateListView(){
        adapter = new ArrayAdapter<String>(this, R.layout.listview_item_schedule, list);
        ListView lv = (ListView) findViewById(R.id.schedule_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                initiatePopupRoom(findViewById(R.id.schedule_list), position);
            }
        });
    }


    private void initiatePopupRoom(View v, final int position) {
        try {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_room, null);
            pwRooms = new PopupWindow(layout, 500, 500, true);
            pwRooms.showAtLocation(v, Gravity.CENTER, 0, 0);
            populatePopupListView(layout, position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populatePopupListView(View layout, final int timeFork){
        ArrayAdapter<String> roomsAdapter = new ArrayAdapter<String>(this, R.layout.listview_item_schedule, listRooms);
        ListView lv = (ListView) layout.findViewById(R.id.popup_room_list);
        lv.setAdapter(roomsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                initiatePopupTemperature(findViewById(R.id.schedule_list), position, timeFork);
                pwRooms.dismiss();
            }
        });
    }

    private void initiatePopupTemperature(View v, final int room, final int timeFork) {
        try {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_temperature, null);
            pwTemperatures = new PopupWindow(layout, 400, 250, true);
            pwTemperatures.showAtLocation(v, Gravity.CENTER, 0, 0);

            // get text input value
            final EditText txtInput = (EditText) layout.findViewById(R.id.popup_editText);

            // cancel button
            Button cancelButton = (Button) layout.findViewById(R.id.popup_cancel);
            cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View popupView) {
                    pwTemperatures.dismiss();
                }
            });

            // add button
            Button addButton = (Button) layout.findViewById(R.id.popup_add);
            addButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final double newTemp = Integer.valueOf(txtInput.getText().toString());
                        Log.d("TEST","timeFork: "+((timeFork+4)%24)*60+"   position: "+room+"   newTemp: "+newTemp);
                        NetworkAccess.planSchedule(((timeFork+4)%24)*60,1,room+1,newTemp);
                        updateData();
                        adapter.notifyDataSetChanged();
                        pwTemperatures.dismiss();
                    } catch (Exception e) {/*ask to add a temperature*/}
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateData(){
        schedule = NetworkAccess.getSchedule();
        initList(schedule);
    }
}
