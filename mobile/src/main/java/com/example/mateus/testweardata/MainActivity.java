package com.example.mateus.testweardata;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity{

    public TextView mTextView;
    public static String jsonMessage = "";
    public TextView recordName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyStoragePermissions(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saving...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                try {
                    writeJSONtoFile(jsonMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mTextView = (TextView) findViewById(R.id.mTextView);
        mTextView.setText(jsonMessage);

        recordName = (TextView) findViewById(R.id.recordNameInput);
        recordName.setText("mov1_1.5s_");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void writeJSONtoFile(String jsonObject) throws IOException {

        FileWriter file;
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +  "/" + recordName.getText() + ".json");
        try {
            file = new FileWriter(path);
            file.write(jsonObject);
            file.flush();
            file.close();
            Toast.makeText(MainActivity.this, "File saved to " + path.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static class ListenerServiceFromWear extends WearableListenerService {

        private static final String WEARPATH = "/from-wear";
        // UDP related
        private UDP_Client aClientUDP;
        private int SERVER_PORT = 25000;

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            super.onMessageReceived(messageEvent);
            Log.d("HEY", "I'm Here!");

//            // Instantiate a new UDP Client
            aClientUDP = new UDP_Client();
            aClientUDP.setServer("192.168.0.107", SERVER_PORT);

            if (messageEvent.getPath().equals(WEARPATH)) {
                jsonMessage = new String(messageEvent.getData());

                aClientUDP.sendMessage(jsonMessage);

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("message", jsonMessage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        }

        @Override
        public void onPeerConnected(Node peer) {
            super.onPeerConnected(peer);

            String id = peer.getId();
            String name = peer.getDisplayName();

            Log.d("MOBILE", "Connected peer name & ID: " + name + "|" + id);
        }

    }

}
