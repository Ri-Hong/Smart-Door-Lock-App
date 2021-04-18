package com.example.smart_door_lock_app;

import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    DbHelper dbHelper;
    CountDownTimer idTimer = null;
    View settingFragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingFragmentView = inflater.inflate(R.layout.fragment_settings,
                container, false);
        dbHelper = new DbHelper(getContext());


        Log.d("updateId", String.valueOf(dbHelper.getSize()));


        if (dbHelper.getSize() == 0){ //New user
            generateId();
        }
        else{ //Existing user, load name, pin and id from database
            TextView idTv = (TextView) settingFragmentView.findViewById(R.id.idTv);
            TextInputEditText nameTi = (TextInputEditText) settingFragmentView.findViewById(R.id.nameTi);
            TextInputEditText pinTi = (TextInputEditText) settingFragmentView.findViewById(R.id.pinTi);
            TextView entryPinTv = (TextView) settingFragmentView.findViewById(R.id.entryPinTv);

            List<ContentValues> userDataList = dbHelper.getData();
            ContentValues cv = userDataList.remove(0);
            String userName = (String) cv.get( "USER");
            String pin = Integer.toString((Integer) cv.get( "PIN"));
            String id = Integer.toString((Integer) cv.get( "ID"));

            idTv.setText("Id: " + id);
            nameTi.setText(userName);
            pinTi.setText(pin);
            entryPinTv.setText(String.valueOf(id) + String.valueOf(pin));
        }


        Button saveBt = (Button) settingFragmentView.findViewById(R.id.saveBt);
        saveBt.setOnClickListener(this);
        return settingFragmentView;
    }


    @Override
    public void onClick(View v) {
        dbHelper = new DbHelper(getContext());

        switch (v.getId()) {
            case R.id.saveBt:
                TextInputEditText nameTi = (TextInputEditText) getView().findViewById(R.id.nameTi);
                TextInputEditText pinTi = (TextInputEditText) getView().findViewById(R.id.pinTi);
                TextView entryPinTv = (TextView) getView().findViewById(R.id.entryPinTv);

                String name = nameTi.getText().toString(); //read the digits from the id input field
                int pin = Integer.parseInt(String.valueOf(pinTi.getText())); //read the pin from the pin input field

                List<ContentValues> userDataList = dbHelper.getData();
                ContentValues cv = userDataList.remove(0);
                int id = (int) cv.get( "ID");

                Log.d("dbdebug", name);
                Log.d("dbdebug", String.valueOf(id));
                Log.d("dbdebug", String.valueOf(pin));

                dbHelper.deleteTable(); //Clear the existing id in the db
                dbHelper.addOne(name, id, pin);

                String stringToPublish = "update credentials " + name + "," + String.valueOf(id) + "," + String.valueOf(pin) + ",";
                HomeFragment.getInstance().mqttPublish(stringToPublish);

                entryPinTv.setText(String.valueOf(id) + String.valueOf(pin));
                break;
        }
    }

    public void generateId() {
        Log.d("updateId", Integer.toString(MainActivity.id));

        if (MainActivity.idStatus == MainActivity.ID_STATUS_INVALID){
            HomeFragment.getInstance().mqttPublish("generate id");
            MainActivity.idStatus = MainActivity.ID_STATUS_VERIFYING;
            idTimerStart();
        }
        else if (MainActivity.idStatus == MainActivity.ID_STATUS_VERIFYING){
            idTimerStart();
        }
        else if (MainActivity.idStatus == MainActivity.ID_STATUS_VALID){

            TextView idTv = (TextView) settingFragmentView.findViewById(R.id.idTv);
            TextInputEditText nameTi = (TextInputEditText) settingFragmentView.findViewById(R.id.nameTi);
            TextInputEditText pinTi = (TextInputEditText) settingFragmentView.findViewById(R.id.pinTi);

            idTv.setText("Id: " + MainActivity.id);
            nameTi.setText("Null User");
            pinTi.setText("0000");

            dbHelper.addOne("Null User", MainActivity.id, 0000);
        }

    }


    void idTimerStart() {
        if( idTimer == null ) {
            idTimer = new CountDownTimer(1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    generateId();
                }
            };
        }
        idTimer.start();
    }
}

