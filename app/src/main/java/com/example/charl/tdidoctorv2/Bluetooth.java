package com.example.charl.tdidoctorv2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import br.ufrn.imd.obd.commands.ObdCommandGroup;
import br.ufrn.imd.obd.commands.control.TroubleCodesCommand;
import br.ufrn.imd.obd.commands.engine.RPMCommand;
import br.ufrn.imd.obd.commands.engine.SpeedCommand;
import br.ufrn.imd.obd.commands.engine.ThrottlePositionCommand;
import br.ufrn.imd.obd.commands.pressure.BarometricPressureCommand;
import br.ufrn.imd.obd.commands.pressure.IntakeManifoldPressureCommand;
import br.ufrn.imd.obd.commands.protocol.EchoOffCommand;
import br.ufrn.imd.obd.commands.protocol.LineFeedOffCommand;
import br.ufrn.imd.obd.commands.protocol.SelectProtocolCommand;
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand;
import br.ufrn.imd.obd.commands.temperature.AmbientAirTemperatureCommand;
import br.ufrn.imd.obd.enums.ObdProtocols;
import br.ufrn.imd.obd.exceptions.UnknownErrorException;


/**
 *  Used to control all Bluetooth connection and data retrieval code.
 */
public class Bluetooth {

    private Context context;
    private final String TAG = "Bluetooth";
    private BluetoothDevice m_OBDDevice = null;
    private BluetoothSocket m_Socket = null;
    private static final UUID UUID_USE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Bluetooth(Context cntxt) {
        context = cntxt;
    }

    /**
     * Initializes Bluetooth information in the application and checks if any
     * valid OBD-II devices are currently connected to the phone.
     */
    public void setupDevice() {
        BluetoothAdapter m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices;

        if (m_bluetoothAdapter == null || !m_bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "BLUETOOTH ADAPTER NOT FOUND OR IS DISABLED", Toast.LENGTH_SHORT).show();
            return;
        }


        pairedDevices = m_bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                String hardwareAddress = device.getAddress();
                if (name.contains("OBD")) {
                    m_OBDDevice = device;
                    String uuid = device.getUuids()[0].toString();
                    return;
                }
            }
        }

    }

    /**
     * Determines whether or not a Bluetooth OBD-II device is connected.
     * @return true if a device is connected, false otherwise
     */
    public boolean foundDevice(){
        return m_OBDDevice != null;
    }

    /**
     * Conneccts the application to the OBD-II adapter.
     * @return true if connection established and valid, false otherwise
     */
    public boolean connect(){
        if(m_OBDDevice == null){
            Log.e(TAG," connect: device not set");
            return false;
        }

        if (isConnected()) {
            if (m_Socket.getRemoteDevice().getAddress().equals(m_OBDDevice.getAddress())) {
                Log.d(TAG, "connect: Already connected, no need to change socket...");
                return true;
            } else {
                Log.d(TAG, "connect: Connected to another device. Disconnecting...");
                disconnect();
            }
        }

        try{
            m_Socket = m_OBDDevice.createRfcommSocketToServiceRecord(UUID_USE);
            m_Socket.connect();
            return true;
        } catch (IOException e) {
            Log.e(TAG, " connect: unable to connect to socket");
            return false;
        }
    }

    /**
     * Determines if an OBD-II device is currently connected.
     * @return true if connected, false otherwise
     */
    private boolean isConnected(){
        return m_Socket != null && m_Socket.isConnected();
    }

    /**
     * Disconnects Bluetooth connection to OBD-II adapter if a connection exists.
     */
    private void disconnect() {
        if (isConnected()) {
            try {
                m_Socket.close();
                Log.d(TAG, "disconnect: Socket successfully disconnected");
            } catch (IOException e) {
                Log.e(TAG, "disconnect: Fail to close Bluetooth socket connection", e);
            }
        }
    }

    /**
     * Returns ObdCommandGroup with all relevant OBD commands
     * @return ObdCommandGroup with necessary OBD commands for application
     */
    public ObdCommandGroup getObdCommands(){
        ObdCommandGroup obdCommands = new ObdCommandGroup();
        obdCommands.add(new EchoOffCommand());
        obdCommands.add(new LineFeedOffCommand());
        int TIMEOUT = 500;
        obdCommands.add(new TimeoutCommand(TIMEOUT));
        obdCommands.add(new SelectProtocolCommand(ObdProtocols.AUTO));
        obdCommands.add(new AmbientAirTemperatureCommand());
        obdCommands.add(new RPMCommand());
        obdCommands.add(new BarometricPressureCommand());
        obdCommands.add(new SpeedCommand());
        obdCommands.add(new ThrottlePositionCommand());
        obdCommands.add(new IntakeManifoldPressureCommand());

        obdCommands.add(new TroubleCodesCommand());

        return obdCommands;
    }

    /**
     * Uses Bluetooth to retrieve current vehicle engine RPM
     * @return engine RPM as formatted string of "[1234] RPM"
     */
    public String getRPM(){
        RPMCommand rpm = new RPMCommand();
        try {
            rpm.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            int rpmValue = rpm.getRPM();
            return rpmValue + " RPM";
        } catch (IOException | InterruptedException | UnknownErrorException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Uses Bluetooth to retrieve current throttle position percentage
     * @return Throttle position as a formatted string
     */
    public String getThrottlePosition(){
        ThrottlePositionCommand throttlePosition = new ThrottlePositionCommand();
        try {
            throttlePosition.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            return throttlePosition.getFormattedResult();
        } catch (IOException | UnknownErrorException | InterruptedException e) {
            e.printStackTrace();
        }

        return "";
    }

    //Boost = (Intake manifold absolute pressure) - (absolute barometric pressure)
    /**
     * Uses formula of (Boost = (Intake manifold absolute pressure) - (absolute barometric pressure))
     * and Bluetooth commands to retrieve current data
     * @return String of formatted boost pressure in PSI
     */
    public String getBoost(){

        BarometricPressureCommand barometricPressure = new BarometricPressureCommand();
        IntakeManifoldPressureCommand intakeManifoldPressure = new IntakeManifoldPressureCommand();

        try {
            intakeManifoldPressure.setImperialUnits(true);
            barometricPressure.setImperialUnits(true);

            intakeManifoldPressure.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            barometricPressure.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            float barometricImperial = barometricPressure.getImperialUnit();
            float intakeImperial = intakeManifoldPressure.getImperialUnit();
            String barometricResult = barometricPressure.getCalculatedResult();
            String intakePressure = intakeManifoldPressure.getCalculatedResult();
            double result;


            try{
                double barometricDouble = Double.parseDouble(barometricResult);
                double intakeDouble = Double.parseDouble(intakePressure);
                result = intakeDouble - barometricDouble;
                if(result < 0.00)
                    result = 0.00;
                return String.format(Locale.US, "%.2f PSI", result);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } catch (IOException | UnknownErrorException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Gets current vehicle speed in RPM via Bluetooth
     * @return Vehicle speed in MPH
     */
    public String getSpeed(){
        SpeedCommand speed = new SpeedCommand();
        try {
            speed.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
            return String.format(Locale.US,"%.2f MPH", speed.getImperialSpeed());
        } catch (IOException | UnknownErrorException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Disables echoing of data from OBD-II adapter
     */
    public void runEchoOffCommand() {
        EchoOffCommand echoOff = new EchoOffCommand();
        try {
            echoOff.run(m_Socket.getInputStream(), m_Socket.getOutputStream());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns OBD-II trouble codes/PIDs using Bluetooth command
     * @return String formatted with OBD-II trouble codes
     */
    public String getTroubleCodes(){
        TroubleCodesCommand troubles = new TroubleCodesCommand();

        try{
            troubles.run(m_Socket.getInputStream(), m_Socket.getOutputStream());

            return troubles.getResult();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
