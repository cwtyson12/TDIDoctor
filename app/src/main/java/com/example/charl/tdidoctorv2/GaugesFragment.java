package com.example.charl.tdidoctorv2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class GaugesFragment extends Fragment {
    TextView tvRPM;
    Button updateValuesButton;
    Button connectBluetoothButton;
    Button createBluetoothButton;
    Bluetooth bluetooth;
    public GaugesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gauges, container, false);
        updateValuesButton = (Button) view.findViewById(R.id.updateValuesButton);
        connectBluetoothButton = (Button) view.findViewById(R.id.connectBluetoothButton);
        createBluetoothButton = (Button) view.findViewById(R.id.createBluetoothButton);
        tvRPM = (TextView) view.findViewById(R.id.RPMValue);

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
                //TODO add rest of values here (BarometricPressureCommand, SpeedCommand, ThrottlePositionCommand)
                String rpm = bluetooth.getRPM();
                tvRPM.setText(rpm);
                //bluetooth.updateValues(getActivity());

                //String pos = bluetooth.getThrottlePosition();
                // Toast.makeText(getActivity(), "Throttle Position: " + pos, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

}
