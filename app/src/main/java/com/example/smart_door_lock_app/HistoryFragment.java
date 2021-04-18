package com.example.smart_door_lock_app;


import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    DbHelper dbHelper;
    View historyFragmentView;
    ListView listView;
    CountDownTimer historyTimer = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new DbHelper(getContext()); //Initialise the database

        historyFragmentView = inflater.inflate(R.layout.fragment_history,
                container, false); //Inflate the fragment

        loadListView(); //Initial load when this fragment is initialized

        autoUpdateHistory();

        return historyFragmentView;
    }

    void loadListView() {
        listView = historyFragmentView.findViewById(R.id.listViewLv);

        ArrayList<HistoryEntry> arrayList = new ArrayList<>(); //Stores all the entries that will be in history

        //Populate arrayList
        List<ContentValues> userDataList = dbHelper.getHistory();
        int image = 0;

        while (userDataList.size() > 0) {
            ContentValues cv = userDataList.remove(userDataList.size() - 1); //Get last element
            String name = (String) cv.get("NAME");
            String action = (String) cv.get("ACTION");
            String time = (String) cv.get("TIME");
            Log.d("action", action);
            if (action.equals("lock")) {
                image = R.drawable.locked;
            } else if (action.equals("unlock")) {
                image = R.drawable.unlocked;
            }

            arrayList.add(new HistoryEntry(image, name, time));
        }

        EntryAdapter entryAdapter = new EntryAdapter(getContext(), R.layout.list_row, arrayList);
        listView.setAdapter(entryAdapter);
    }

    void autoUpdateHistory(){ //Updates the ListView when a new lock/unlock message comes in
        if (MainActivity.newHistoryData){ //If there is new data that is not put into the list
            loadListView();
            MainActivity.newHistoryData = false;
        }
        historyTimerStart();
    }

    void historyTimerStart() { //Used to periodically update the listView
        if( historyTimer == null ) {
            historyTimer = new CountDownTimer(1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    autoUpdateHistory();
                }
            };
        }
        historyTimer.start();
    }
}