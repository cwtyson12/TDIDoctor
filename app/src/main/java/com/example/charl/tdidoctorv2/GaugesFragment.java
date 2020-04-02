package com.example.charl.tdidoctorv2;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;


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
    Button detect;
    Bluetooth bluetooth;
    volatile boolean stopCollecting = false;


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
        //detect = (Button) view.findViewById(R.id.detectButton);
        tvRPM = (TextView) view.findViewById(R.id.RPMValue);
        tvBoost = (TextView) view.findViewById(R.id.BoostValue);
        tvSpeed = (TextView) view.findViewById(R.id.SpeedValue);
        tvThrottlePosition = (TextView) view.findViewById(R.id.ThrottlePositionValue);

        createBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth = new Bluetooth(getActivity());
                bluetooth.setupDevice();
                if(bluetooth.foundDevice()) {
                    boolean ableToConnect = bluetooth.connect();
                    if(!ableToConnect){
                        Toast.makeText(getActivity(), "Unable to establish Bluetooth connection", Toast.LENGTH_LONG).show();
                    }
                    else{
                        bluetooth.runEchoOffCommand();
                        updateValuesButton.setEnabled(true);
                        //detect.setEnabled(true);
                    }
                }
            }
        });

        updateValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO use volatile boolean to stop thread
                stopCollecting = false;
                UpdateThread updateThread = new UpdateThread();
                new Thread(updateThread).start();
            }
        });

//        detect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean lspi = bluetooth.detectLSPI();
//                if(lspi){
//                    layout.setBackgroundColor(Color.RED);
//                }
//                else{
//                    layout.setBackgroundColor(Color.GREEN);
//                }
//            }
//        });

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
                            if(!rpm.equals(""))
                                tvRPM.setText(rpm);
                            if(!boost.equals(""))
                                tvBoost.setText(boost);
                            if(!speed.equals(""))
                                tvSpeed.setText(speed);
                            if(!throttlePosition.equals(""))
                            tvThrottlePosition.setText(throttlePosition);

                            boolean lspi = detectLSPINotUpdatingValues();
                            if(lspi){
                                layout.setBackgroundColor(Color.RED);
                            }
                            else{
                                layout.setBackgroundColor(Color.GREEN);
                            }
                        }
                    });
                    numIterations++;



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
