package com.example.charl.tdidoctorv2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import br.ufrn.imd.obd.commands.ObdCommandGroup;
import br.ufrn.imd.obd.commands.engine.RPMCommand;
import br.ufrn.imd.obd.commands.engine.SpeedCommand;
import br.ufrn.imd.obd.commands.engine.ThrottlePositionCommand;
import br.ufrn.imd.obd.commands.pressure.BarometricPressureCommand;
import br.ufrn.imd.obd.commands.protocol.EchoOffCommand;
import br.ufrn.imd.obd.commands.protocol.LineFeedOffCommand;
import br.ufrn.imd.obd.commands.protocol.SelectProtocolCommand;
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand;
import br.ufrn.imd.obd.commands.temperature.AmbientAirTemperatureCommand;
import br.ufrn.imd.obd.enums.ObdProtocols;

public class Bluetooth {

    private Context context;
    private final String TAG = "Bluetooth";
    private final int TIMEOUT = 500;
    protected BluetoothAdapter m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice m_OBDDevice = null;
    private BluetoothSocket m_Socket = null;
    private String uuid;
    private String macAddress;
    private static final UUID UUID_USE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Bluetooth(Context cntxt) {
        context = cntxt;
    }

    public boolean setupDevice() {
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices;

        if (m_bluetoothAdapter == null || !m_bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "BLUETOOTH ADAPTER NOT FOUND OR IS DISABLED", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Toast.makeText(context, "BLUETOOTH ADAPTER FOUND", Toast.LENGTH_SHORT).show();

        pairedDevices = m_bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                String hardwareAddress = device.getAddress();
                if (name.contains("OBD")) {
                    m_OBDDevice = device;
                    uuid = device.getUuids()[0].toString();
                    Toast.makeText(context, "UUID: " + uuid, Toast.LENGTH_LONG).show();
                    macAddress = hardwareAddress;
                    return true;

                }
            }
        }
        //TODO find out if this is necessary/good protocol
        //m_bluetoothAdapter.cancelDiscovery();

        return false;
    }

    public boolean foundDevice(){
        return m_OBDDevice != null;
    }

    public void connect(){
        if(m_OBDDevice == null){
            Log.e(TAG," connect: device not set");
            return;
        }

        if (isConnected()) {
            if (m_Socket.getRemoteDevice().getAddress().equals(m_OBDDevice.getAddress())) {
                Log.d(TAG, "connect: Already connected, no need to change socket...");
                return;
            } else {
                Log.d(TAG, "connect: Connected to another device. Disconnecting...");
                disconnect();
            }
        }

        try{
            m_Socket = m_OBDDevice.createRfcommSocketToServiceRecord(UUID_USE);
            m_Socket.connect();
        } catch (IOException e) {
            Log.e(TAG, " connect: unable to connect to socket");
        }
    }

    public boolean isConnected(){
        return m_Socket != null && m_Socket.isConnected();
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                m_Socket.close();
                Log.d(TAG, "disconnect: Socket successfully disconnected");
            } catch (IOException e) {
                Log.e(TAG, "disconnect: Fail to close Bluetooth socket connection", e);
            }
        }
    }

    public ObdCommandGroup getObdCommands(){
        ObdCommandGroup obdCommands = new ObdCommandGroup();
        obdCommands.add(new EchoOffCommand());
        obdCommands.add(new LineFeedOffCommand());
        obdCommands.add(new TimeoutCommand(TIMEOUT));
        obdCommands.add(new SelectProtocolCommand(ObdProtocols.AUTO));
        obdCommands.add(new AmbientAirTemperatureCommand());
        obdCommands.add(new RPMCommand());
        obdCommands.add(new BarometricPressureCommand());
        obdCommands.add(new SpeedCommand());
        obdCommands.add(new ThrottlePositionCommand());
        ThrottlePositionCommand f = new ThrottlePositionCommand();
        return obdCommands;
    }

    public String getRPM(){
        RPMCommand rpm = new RPMCommand();
        try {
            rpm.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            String formattedResult = rpm.getFormattedResult();
            String resultUnit = rpm.getResultUnit();
            return formattedResult;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getThrottlePosition(){
        ThrottlePositionCommand throttlePosition = new ThrottlePositionCommand();
        try {
            throttlePosition.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            float percentage = throttlePosition.getPercentage();
            String x = "" + percentage;
            return x;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean readyToUpdate(){
        Log.d(TAG, "In ready to update");
        return (m_Socket != null && m_Socket.isConnected());
    }

    public String updateValues(Context context){
        if(readyToUpdate()){
            ObdCommandGroup obdCommands = getObdCommands();
            Toast.makeText(context ,"In updateValues", Toast.LENGTH_SHORT).show();
            while(m_Socket != null && m_Socket.isConnected()){
                try {
                    obdCommands.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
                    String formattedResult = obdCommands.getFormattedResult();
                    return formattedResult;
                } catch (IOException e) {
                    Log.e(TAG, "IOException on updateValues()", e);
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException on updateValues()", e);
                }
            }
        }
        return "ERROR";
    }
}
