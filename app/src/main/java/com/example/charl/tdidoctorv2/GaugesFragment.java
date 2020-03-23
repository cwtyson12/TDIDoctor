package com.example.charl.tdidoctorv2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

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
    Button connectBluetoothButton;
    Button createBluetoothButton;
    Button detect;
    Bluetooth bluetooth;
    public GaugesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gauges, container, false);
        layout = (LinearLayout) view.findViewById(R.id.gaugesLayout);
        updateValuesButton = (Button) view.findViewById(R.id.updateValuesButton);
        connectBluetoothButton = (Button) view.findViewById(R.id.connectBluetoothButton);
        createBluetoothButton = (Button) view.findViewById(R.id.createBluetoothButton);
        detect = (Button) view.findViewById(R.id.detectButton);
        tvRPM = (TextView) view.findViewById(R.id.RPMValue);
        tvBoost = (TextView) view.findViewById(R.id.BoostValue);
        tvSpeed = (TextView) view.findViewById(R.id.SpeedValue);
        tvThrottlePosition = (TextView) view.findViewById(R.id.ThrottlePositionValue);

        createBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth = new Bluetooth(getActivity());
                bluetooth.setupDevice();
            }
        });

        connectBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetooth.foundDevice()) {
                    bluetooth.connect();
                }
            }
        });
        updateValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rpm = bluetooth.getRPM();
                String boost = bluetooth.getBoost();
                String speed = bluetooth.getSpeed();
                String throttlePosition = bluetooth.getThrottlePosition();

                tvRPM.setText(rpm);
                tvBoost.setText(boost);
                tvSpeed.setText(speed);
                tvThrottlePosition.setText(throttlePosition);

                //bluetooth.updateValues(getActivity());

                //String pos = bluetooth.getThrottlePosition();
                // Toast.makeText(getActivity(), "Throttle Position: " + pos, Toast.LENGTH_LONG).show();
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean lspi = bluetooth.detectLSPI();
                if(lspi){
                    layout.setBackgroundColor(Color.RED);
                }
                else{
                    layout.setBackgroundColor(Color.GREEN);
                }
            }
        });

        return view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        TabLayout tabLayout = view.findViewById(R.id.tablayout);
//        ViewPager2 viewPager2 = view.findViewById(R.id.viewpager);
//        new TabLayoutMediator(tabLayout, viewPager2,
//                (tab, position) -> tab.setText("OBJECT " + (position + 1))
//        ).attach();
//    }
}
