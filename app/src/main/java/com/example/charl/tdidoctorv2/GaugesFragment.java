package com.example.charl.tdidoctorv2;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class GaugesFragment extends Fragment {
    LinearLayout layout;
    TextView tvRPM;
    TextView tvBoost;
    TextView tvSpeed;
    TextView tvThrottlePosition;
    Button updateValuesButton;
    Button createBluetoothButton;
    Bluetooth bluetooth;
    ProgressBar bluetoothProgressBar;
    volatile boolean stopCollecting = true;
    DatabaseHandler dbh;
    SQLiteDatabase sqLiteDatabase;


    private Handler mainHandler = new Handler();

    public GaugesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gauges, container, false);
        layout = (LinearLayout) view.findViewById(R.id.gaugesLayout);
        updateValuesButton = (Button) view.findViewById(R.id.updateValuesButton);
        createBluetoothButton = (Button) view.findViewById(R.id.createBluetoothButton);
        tvRPM = (TextView) view.findViewById(R.id.RPMValue);
        tvBoost = (TextView) view.findViewById(R.id.BoostValue);
        tvSpeed = (TextView) view.findViewById(R.id.SpeedValue);
        tvThrottlePosition = (TextView) view.findViewById(R.id.ThrottlePositionValue);
        bluetoothProgressBar = (ProgressBar) view.findViewById(R.id.BluetoothProgressBar);

        dbh = new DatabaseHandler(getActivity());
        sqLiteDatabase = dbh.getWritableDatabase();
        dbh.dropTables(sqLiteDatabase);

        createBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth = new Bluetooth(getActivity());
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        mainHandler.post(new Runnable(){
                            @Override
                            public void run() {
                                bluetoothProgressBar.setVisibility(View.VISIBLE);
                                createBluetoothButton.setEnabled(false);
                            }
                        });

                        bluetooth.setupDevice();
                        if(bluetooth.foundDevice()) {
                            boolean ableToConnect = bluetooth.connect();
                            if(!ableToConnect){
                                mainHandler.post(new Runnable(){
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Unable to establish Bluetooth connection", Toast.LENGTH_LONG).show();
                                        bluetoothProgressBar.setVisibility(View.GONE);
                                    }
                                });
                                //createBluetoothButton.setEnabled(true);
                            }
                            else{
                                bluetooth.runEchoOffCommand();
                                mainHandler.post(new Runnable(){
                                    @Override
                                    public void run() {
                                        bluetoothProgressBar.setVisibility(View.GONE);
                                    }
                                });
                                //updateValuesButton.setEnabled(true);
                            }
                            mainHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    createBluetoothButton.setEnabled(!ableToConnect);
                                    updateValuesButton.setEnabled(ableToConnect);
                                }
                            });
                        }
                    }
                };
                thread.start();
            }
        });

        updateValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO use volatile boolean to stop thread
                stopCollecting = !stopCollecting;

                //stopCollecting = false;
                if(!stopCollecting){
                    updateValuesButton.setText("Stop Collecting");
                    UpdateThread updateThread = new UpdateThread();
                    new Thread(updateThread).start();

                    String codes = bluetooth.getTroubleCodes();

                    long currTime = new Date().getTime();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.US);
                    String dateTime = sdf.format(currTime);

                    dbh.insertTroubleCodes(dateTime, codes);
                }
                else{
                    updateValuesButton.setText("Begin Tracking");
                }
            }
        });

        return view;
    }

    boolean detectLSPINotUpdatingValues(){
        String boostValueString = tvBoost.getText().toString();
        String rpmValueString = tvRPM.getText().toString();
        String speedValueString = tvSpeed.getText().toString();
        double boostValue = 0.0;
        int rpmValue = 0;
        double speedValue = 0.0;


        boostValueString = boostValueString.substring(0, boostValueString.indexOf(' '));
        try{
            boostValue = Double.parseDouble(boostValueString);
        } catch(NumberFormatException e){
            Log.e("Gauges Fragment", "Number format exception for boost value conversion to double");
        }

        rpmValueString = rpmValueString.substring(0, rpmValueString.indexOf(' '));
        try{
            rpmValue = Integer.parseInt(rpmValueString);
        } catch(NumberFormatException e){
            Log.e("Gauges Fragment", "Number format exception for RPM value conversion to int");
        }

        speedValueString = speedValueString.substring(0, speedValueString.indexOf(' '));
        try{
            speedValue = Double.parseDouble(speedValueString);
        } catch(NumberFormatException e){
            Log.e("Gauges Fragment", "Number format exception for speed value conversion to double");
        }

        if(speedValue > 45 && boostValue > 5 && rpmValue < 3500)
            return true;

        return false;
    }

    class UpdateThread implements Runnable {
        final int WAIT_TIME = 20;
        final int NUM_ITERATIONS_TO_RUN = 1000;

        @Override
        public void run() {
            int numIterations = 0;
            boolean doLoop = true;
            while(doLoop){
                try {
                    if(numIterations == NUM_ITERATIONS_TO_RUN || stopCollecting)  //want to not run indefinitely for now
                        doLoop = false;
                    Thread.sleep(WAIT_TIME);

                    String rpm = bluetooth.getRPM();
                    String boost = bluetooth.getBoost();
                    String speed = bluetooth.getSpeed();
                    String throttlePosition = bluetooth.getThrottlePosition();


                    //needs to be in handler
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!rpm.equals("")){
                                tvRPM.setText(rpm);
                            }
                            if(!boost.equals(""))
                                tvBoost.setText(boost);
                            if(!speed.equals("")){
                                tvSpeed.setText(speed);
                            }
                            if(!throttlePosition.equals(""))
                            tvThrottlePosition.setText(throttlePosition);

                            boolean lspi = detectLSPINotUpdatingValues();
                            if(lspi){
                                layout.setBackgroundColor(getResources().getColor(R.color.LSPI));
                            }
                            else{
                                tvRPM.setTextColor(getResources().getColor(R.color.noLSPI));
                                tvBoost.setTextColor(getResources().getColor(R.color.noLSPI));
                                tvSpeed .setTextColor(getResources().getColor(R.color.noLSPI));
                            }
                        }
                    });

                    //update in DB here
                    if(!speed.equals("") && !rpm.equals("")){
                        long time = new Date().getTime();

                        String justSpeed = speed.substring(0, speed.indexOf(' '));
                        double speedDouble = Double.parseDouble(justSpeed);

                        String justRPM = rpm.substring(0, rpm.indexOf(' '));
                        Log.d("GaugesFragment", "JUSTRPM: " + justRPM);
                        int rpmInt = Integer.parseInt(justRPM);

                        dbh.insertToDatabase(time, speedDouble, rpmInt);

                    }

                    numIterations++;



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        public void insertData(){

        }
    }
}
