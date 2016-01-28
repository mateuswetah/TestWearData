package com.example.mateus.testweardata;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.ActionPage;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Sensors
    private SensorManager managerAccel, managerGyro;
    private Sensor sensorAccel, sensorGyro;
    private SensorEventListener AccelListener, GyroListener;
    // Visual Components
    private TextView ValueAccelX, ValueAccelY, ValueAccelZ;
    private TextView ValueGyroX, ValueGyroY, ValueGyroZ;
    private ToggleButton ButtonRecord;
    //private ImageButton ButtonSpeak;
    // JSON parsing
    JSONObject jsonObject;
    JSONArray jsonAllAccel, jsonAllGyro;
    int recordNumber = 0;
    // Related to Sending the Message
    private static final String WEARABLE_MAIN = "WearableMain";
    private Node mNode;
    private GoogleApiClient mGoogleApiClient;
    private static final String WEAR_PATH = "/from-wear";
//    // Timer
//    Timer timer = new Timer();
//    long current = 0;
//    boolean checkTimer = false;
//    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        // Initialize mGoogleApiClient
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(AppIndex.API).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        // Associate the XML items for displaying the values
        ValueAccelX = (TextView) findViewById(R.id.ValueAccelX);
        ValueAccelY = (TextView) findViewById(R.id.ValueAccelY);
        ValueAccelZ = (TextView) findViewById(R.id.ValueAccelZ);

        ValueGyroX = (TextView) findViewById(R.id.ValueGyroX);
        ValueGyroY = (TextView) findViewById(R.id.ValueGyroY);
        ValueGyroZ = (TextView) findViewById(R.id.ValueGyroZ);

        ButtonRecord = (ToggleButton) findViewById(R.id.ButtonRecord);
        //ButtonSpeak = (ImageButton) findViewById(R.id.ButtonSpeak);

        // Set the sensors
        managerAccel = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = managerAccel.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        managerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorGyro = managerGyro.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //Initialize the JSON object
        jsonObject = new JSONObject();
        jsonAllAccel = new JSONArray();
        jsonAllGyro = new JSONArray();
//
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
////                checkTimer = false;
////
////                //ButtonRecord.setChecked(false);
////                //ButtonRecord.callOnClick();
////
////                try {
////
////                    jsonObject.put("Accel", jsonAllAccel);
////                    jsonObject.put("Gyro", jsonAllGyro);
////
////                    //writeJSONtoFile(jsonObject);
////                    sendMessage(jsonObject.toString(2));
////
////                    jsonObject = new JSONObject();
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//        };

        AccelListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                ValueAccelX.setText(String.valueOf(event.values[0]));
                ValueAccelY.setText(String.valueOf(event.values[1]));
                ValueAccelZ.setText(String.valueOf(event.values[2]));

                if (ButtonRecord.isChecked()) {
                    JSONArray jsonAccel = new JSONArray();
                    jsonAccel.put(String.valueOf(event.values[0]));
                    jsonAccel.put(String.valueOf(event.values[1]));
                    jsonAccel.put(String.valueOf(event.values[2]));

                    jsonAllAccel.put(jsonAccel);

//                    ClientUDP.sendMessage("  Xa=" + event.values[0] +
//                            "; Ya=" + event.values[1] +
//                            "; Za=" + event.values[2] +
//                            "\n");

                    //checkTimer = true;


                }


//                current = System.currentTimeMillis();
//
//                if (checkTimer) {
//                    if (current - timer >= 5000) {
//
//                        ButtonRecord.callOnClick();
//                        ButtonRecord.setChecked(false);
//                        checkTimer = false;
//                    }
//                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        GyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                ValueGyroX.setText(String.valueOf(event.values[0]));
                ValueGyroY.setText(String.valueOf(event.values[1]));
                ValueGyroZ.setText(String.valueOf(event.values[2]));


                if (ButtonRecord.isChecked()) {
                    JSONArray jsonGyro = new JSONArray();

                    jsonGyro.put(String.valueOf(event.values[0]));
                    jsonGyro.put(String.valueOf(event.values[1]));
                    jsonGyro.put(String.valueOf(event.values[2]));

                    jsonAllGyro.put(jsonGyro);

//                    ClientUDP.sendMessage(  "  Xg=" + event.values[0] +
//                            "; Yg=" + event.values[1] +
//                            "; Zg=" + event.values[2] +
//                            "\n");
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

//        // Set the buttons
//        ButtonSpeak.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                promptSpeechInput();
//            }
//        });

        ButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ButtonRecord.isChecked()) {

//                    checkTimer = true;

                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Your process to do

                                Log.e("MSG", "I'm Here!!!!");
                                try {

                                    jsonObject.put("Accel", jsonAllAccel);
                                    jsonObject.put("Gyro", jsonAllGyro);

                                    //writeJSONtoFile(jsonObject);
                                    sendMessage(jsonObject.toString(2));

                                    jsonObject = new JSONObject();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ButtonRecord.setChecked(false);

                            }
                        }, 2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                } else {
//                    //timer.schedule(timerTask, 3000, 1);
//                }

            }
        });

    }

    private void sendMessage(String message) {


        if (mNode != null && mGoogleApiClient != null) {

            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(),
                    WEAR_PATH, message.getBytes())
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.d(WEARABLE_MAIN, "Failed message:" + sendMessageResult.getStatus().getStatusCode());
                            } else {
                                Log.d(WEARABLE_MAIN, "Message succeeded");
                            }
                        }
                    });
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (managerAccel != null) {
            managerAccel.unregisterListener((SensorEventListener) this);
        }
        if (managerGyro != null) {
            managerGyro.unregisterListener((SensorEventListener) this);
        }

    }

    private void updateDisplay() {
//        if (isAmbient()) {
//            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
//            mTextView.setTextColor(getResources().getColor(android.R.color.white));
//            mClockView.setVisibility(View.VISIBLE);
//
//            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
//        } else {
//            mContainerView.setBackground(null);
//            mTextView.setTextColor(getResources().getColor(android.R.color.black));
//            mClockView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onConnected(Bundle bundle) {



        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node : nodes.getNodes()) {
                            if (node != null && node.isNearby()) {
                                mNode = node;
                                Log.d(WEARABLE_MAIN, "Connected to " + mNode.getDisplayName());
                            }
                        }
                        if (mNode == null) {
                            Log.d(WEARABLE_MAIN, "Not connected");
                        }
                    }
                });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("MSG", " " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.example.mateus.testweardata/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.example.mateus.testweardata/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        managerAccel.registerListener(AccelListener, sensorAccel, SensorManager.SENSOR_DELAY_UI);
        managerGyro.registerListener(GyroListener, sensorGyro, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        managerAccel.unregisterListener(AccelListener);
        managerGyro.unregisterListener(GyroListener);
    }

}


