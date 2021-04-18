package com.example.smart_door_lock_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.CountDownTimer;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import androidx.annotation.Nullable;


public class HomeFragment extends Fragment{
    MqttAndroidClient mqttAndroidClient;
    String mqttAndroidClientId = "";

    CountDownTimer clockTimer = null;

    private int debugN = 0;
    String topic = ""; //FILL IN YOUR OWN MQTT TOPIC. Should match the one you set for the ESP01 module

    boolean mLocked;
    private static HomeFragment instance = null;
    DbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);
        dbHelper = new DbHelper(getContext());

        super.onCreate(savedInstanceState);
        instance = this;

        ImageButton lockButton = (ImageButton) view.findViewById(R.id.lockButton); //Init the lock button widgit
        lockButton.setOnClickListener(v -> {
            if (!mLocked){
                v.setBackgroundResource(R.drawable.unlocked);
                mqttPublish("Locked");
                mLocked = true;
            }
            else{
                v.setBackgroundResource(R.drawable.locked);
                mqttPublish("Unlocked");
                mLocked = false;
            }
        });
        return view;

    }

    public static HomeFragment getInstance() {
        return instance;
    }

    public void garageDoorStart() {
        setTrace( "garageDoorStart()");
        clockTimerStart();
        mqttStart();
    } // powerpakStart()

    public void mqttStart() {
        setTrace( "mqttStart()");
        mqttSetup();
        mqttConnect();
    }

    //------------ MQTT setup
    public void mqttSetup() {
        setTrace( "mqttSetup()");
        //-------- setup the MQTT connection
        String mqttUri = "tcp://broker.hivemq.com:1883";
        mqttAndroidClientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(this.getActivity().getApplicationContext(), mqttUri, mqttAndroidClientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                setTrace( "connectionLost()");
                // restart the mqtt connection after some delay
                // handleMqttConnectionLost();

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                setTrace( "messageArrived()");

                showToast("rcvd!");
                String msgPayload = new String(message.getPayload());
                setTrace( msgPayload );
                if (msgPayload.startsWith("unique id")){
                    Log.d("my db", "ID RECIEVED");
                }
                handleMqttMsg(msgPayload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                setTrace( "deliveryComplete()");
            }
        });

    } // mqttSetup()

    public void mqttConnect() {
        setTrace( "mqttConnect()");
        Log.d("my MQTT", "mqttConnect()");
        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setTrace( "onSucess()");
                    setSubscription();
                    mqttPublish("query state"); //Ask the door for it's state when the app starts up

                } // onSuccess()

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    setTrace( "onFailure()");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }  // mqttConnect()

    // ----------- MQTT ---
    // Msg to publish should be a string of Json format
    public void mqttPublish(String msgStr) {
        setTrace( "mqttPublish(): " + msgStr);
        // String message = "{'MsgType':'ping'}";
        try {
            mqttAndroidClient.publish(topic, msgStr.getBytes(), 0, false);
            showToast( "Published Message");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription() {
        setTrace( "setSubscription()");
        try {

            mqttAndroidClient.subscribe(topic, 0);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void conn(View v) {
        setTrace( "8()");
        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    showToast("connected!!");
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    showToast( "connection failed!!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    //--------------------


    private void handleMqttMsg(String msgStr) {
        setTrace( "handleMqttMsg() ");
        Log.d("123 updateId 7", String.valueOf(MainActivity.id));



        Log.d("123 updateId 1", "handleMQTTMSG msgStr");
        Log.d("123 updateId 2", msgStr);
        Log.d("123 updateId 3", String.valueOf(msgStr.startsWith("unique id")));



        Log.d("123 updateId 4", msgStr);
        if (msgStr.equals("Unlocked")){
            ImageButton lockButton = getView().findViewById(R.id.lockButton);
            lockButton.setBackgroundResource(R.drawable.unlocked);
            mLocked = false;
        }
        else if(msgStr.equals("Locked")){
            ImageButton lockButton = getView().findViewById(R.id.lockButton);
            lockButton.setBackgroundResource(R.drawable.locked);
            mLocked = true;
        }
        else if(msgStr.startsWith("unique id")){
            int newId = Integer.parseInt(msgStr.substring(10));
            MainActivity.id = newId;
            MainActivity.idStatus = MainActivity.ID_STATUS_VALID;

            Log.d("123 updateId 5", "ID UPDATED:---");
            Log.d("123 updateId 6", String.valueOf(newId));
            Log.d("123 updateId 7", String.valueOf(MainActivity.id));
        }
        else if(msgStr.startsWith("Door locked by")) {
            String action = "lock";
            String user = msgStr.substring(15);
            dbHelper.addOneHistory(user, action);
            MainActivity.newHistoryData = true;
        }
        else if(msgStr.startsWith("Door unlocked by")) {
            String action = "unlock";
            String user = msgStr.substring(17);
            dbHelper.addOneHistory(user, action);
            MainActivity.newHistoryData = true;
        }
        else{
            Log.d("myTag", msgStr); // change tag------------
        }

    }  // handleMqttMsg()


    void clockTimerStart() {

        if( clockTimer == null ) {
            clockTimer = new CountDownTimer(1000, 1000) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd E hh:mm:ss a");

                    clockTimerStart();
                }
            };
        }
        clockTimer.start();
    }


    private void setTrace( String msg){
        debugN++;
        Log.d( "mgPowerPak", String.valueOf(debugN)+": "+ msg);
        //tvMsg.append( String.valueOf(debugN)+": "+ msg+" ");
    }

    @Override
    public void onStart() {
        super.onStart();
        garageDoorStart();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        garageDoorStart();
        ImageButton lockButton = (ImageButton) getView().findViewById(R.id.lockButton);

        if (mLocked){
            lockButton.setBackgroundResource(R.drawable.locked);
        }
        else{
            lockButton.setBackgroundResource(R.drawable.unlocked);
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showToast(String msg) {
//        Toast.makeText(getApplicationContext(),
//                Integer.toString(msgCount++) + ": MainActivity: " + msg,
//                Toast.LENGTH_SHORT).show();
        //TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
        //tvMsg.setText(msg);
    }


}

