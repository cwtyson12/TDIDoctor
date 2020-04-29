package com.example.charl.tdidoctorv2;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A {@link Fragment} subclass used to control the Gauges tab of the application.
 */
public class GaugesFragment extends Fragment {
    private LinearLayout layout;
    private TextView tvRPM;
    private TextView tvBoost;
    private TextView tvSpeed;
    private TextView tvThrottlePosition;
    private Button updateValuesButton;
    private Button createBluetoothButton;
    private Bluetooth bluetooth;
    private ProgressBar bluetoothProgressBar;
    private volatile boolean stopCollecting = true;
    private DatabaseHandler dbh;
    private SQLiteDatabase sqLiteDatabase;


    private Handler mainHandler = new Handler();

    public GaugesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gauges, container, false);
        layout = view.findViewById(R.id.gaugesLayout);
        updateValuesButton = view.findViewById(R.id.updateValuesButton);
        createBluetoothButton = view.findViewById(R.id.createBluetoothButton);
        tvRPM = view.findViewById(R.id.RPMValue);
        tvBoost = view.findViewById(R.id.BoostValue);
        tvSpeed = view.findViewById(R.id.SpeedValue);
        tvThrottlePosition = view.findViewById(R.id.ThrottlePositionValue);
        bluetoothProgressBar = view.findViewById(R.id.BluetoothProgressBar);

        dbh = new DatabaseHandler(getActivity());
        sqLiteDatabase = dbh.getWritableDatabase();
        dbh.dropTables(sqLiteDatabase);

        createBluetoothButton.setOnClickListener(v -> {
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
                        }
                        else{
                            bluetooth.runEchoOffCommand();
                            mainHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    bluetoothProgressBar.setVisibility(View.GONE);
                                }
                            });
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
        });

        updateValuesButton.setOnClickListener(v -> {
            stopCollecting = !stopCollecting;

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
        });

        return view;
    }

    private boolean detectLSPINotUpdatingValues(){
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

        return speedValue > 45 && boostValue > 5 && rpmValue < 3500;
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
                    if(numIterations == NUM_ITERATIONS_TO_RUN || stopCollecting)
                        doLoop = false;
                    Thread.sleep(WAIT_TIME);

                    String rpm = bluetooth.getRPM();
                    String boost = bluetooth.getBoost();
                    String speed = bluetooth.getSpeed();
                    String throttlePosition = bluetooth.getThrottlePosition();

                    mainHandler.post(() -> {
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
                            layout.setBackgroundColor(Color.WHITE);
                            tvRPM.setTextColor(getResources().getColor(R.color.noLSPI));
                            tvBoost.setTextColor(getResources().getColor(R.color.noLSPI));
                            tvSpeed .setTextColor(getResources().getColor(R.color.noLSPI));
                        }
                    });

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
    }
}
