package com.example.nferon.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import communication.NetworkAccess;
import communication.Schedule;

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private String[] roomsArray;
    private int[] tempArray;
    private Context context;
    private Schedule schedule;



    public CustomAdapter(String[] list, int[] list2, Context context) {
        this.roomsArray = list;
        this.tempArray = list2;
        this.context = context;
        this.schedule = NetworkAccess.getSchedule();
    }

    @Override
    public int getCount() {
        return roomsArray.length;
    }

    @Override
    public Object getItem(int pos) {
        return roomsArray[pos];
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item_temp, null);
        }

        //Handle TextView1
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(roomsArray[position]);

        //Handle decrease
        Button decreaseButton = (Button)view.findViewById(R.id.decrease_button);

        decreaseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NetworkAccess.changeTemperature(1,position+1,tempArray[position]-1);
                tempArray[position]--;
                notifyDataSetChanged();
            }
        });

        //Handle TextView2
        TextView listItemText2 = (TextView)view.findViewById(R.id.list_item_string2);
        listItemText2.setText(tempArray[position]+"°");

        //Handle incease
        Button increaseButton = (Button)view.findViewById(R.id.increase_button);
        increaseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NetworkAccess.changeTemperature(1,position+1,tempArray[position]+1);
                tempArray[position]++;
                notifyDataSetChanged();
            }
        });

        return view;
    }
}