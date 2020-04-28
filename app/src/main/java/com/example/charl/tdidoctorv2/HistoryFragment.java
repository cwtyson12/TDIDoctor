package com.example.charl.tdidoctorv2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private GraphView speedGraph;
    private GraphView rpmGraph;

    private Dialog troubleCodesDialog;

    private Button getSpeedValuesButton;
    private Button getTroubleCodesButton;
    DatabaseHandler dbh;
    SQLiteDatabase sqLiteDatabase;
    LineGraphSeries<DataPoint> speedDataSeries = new LineGraphSeries<DataPoint>(new DataPoint[0]);
    LineGraphSeries<DataPoint> rpmDataSeries = new LineGraphSeries<DataPoint>(new DataPoint[0]);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        speedGraph = (GraphView) view.findViewById(R.id.SpeedGraphView);
        rpmGraph = (GraphView) view.findViewById(R.id.RPMGraphView);
        getSpeedValuesButton = (Button) view.findViewById(R.id.GetValuesButton);
        getTroubleCodesButton = (Button) view.findViewById(R.id.GetTroubleCodesButton);

        troubleCodesDialog = new Dialog(getActivity());


        dbh = new DatabaseHandler(getActivity());
        sqLiteDatabase = dbh.getWritableDatabase();

        speedGraph.addSeries(speedDataSeries);
        speedGraph.getGridLabelRenderer().setNumHorizontalLabels(3);
        speedGraph.getGridLabelRenderer().setPadding(80);
        speedGraph.getViewport().setScalable(true);
        speedGraph.getViewport().setScrollable(true);
        speedGraph.getViewport().setScalableY(true);
        speedGraph.getGridLabelRenderer().setHorizontalLabelsAngle(135);

        rpmGraph.addSeries(rpmDataSeries);
        rpmGraph.getGridLabelRenderer().setNumHorizontalLabels(3);
        rpmGraph.getGridLabelRenderer().setPadding(80);
        rpmGraph.getViewport().setScalable(true);
        rpmGraph.getViewport().setScrollable(true);
        rpmGraph.getViewport().setScalableY(true);
        rpmGraph.getGridLabelRenderer().setHorizontalLabelsAngle(135);

        getSpeedValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedDataSeries = new LineGraphSeries<DataPoint>(getSpeedData());
                speedGraph.removeAllSeries();
                speedGraph.addSeries(speedDataSeries);
                speedGraph.onDataChanged(true,false);

                rpmDataSeries = new LineGraphSeries<DataPoint>(getRPMData());
                rpmGraph.removeAllSeries();
                rpmGraph.addSeries(rpmDataSeries);
                rpmGraph.onDataChanged(true,false);

                Log.d("HistoryFragment", "GOT BOTH DATA SERIES");


                speedGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(isValueX){
                            return simpleDateFormat.format(new Date((long) value));
                        }
                        else{
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });

                rpmGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(isValueX){
                            return simpleDateFormat.format(new Date((long) value));
                        }
                        else{
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });
            }
        });

        getTroubleCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troubleCodesDialog.setContentView(R.layout.trouble_codes_popup_window);
                Button closeButton;
                closeButton = (Button) troubleCodesDialog.findViewById(R.id.closeTroubleCodes);
                TextView codesTV;
                codesTV = (TextView) troubleCodesDialog.findViewById(R.id.troubleCodesTV);
                Log.d("HistoryFragment", "codesTV == null: " + (codesTV == null));

                String[] columns = {"time", "codes"};
                @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("troubleCodesTable", columns, null, null, null, null, null);
                cursor.moveToNext();
                String formattedTime = cursor.getString(0);
                String codesText = cursor.getString(1);


                codesTV.setText("Codes for " + formattedTime + ":\n\n" + (codesText.equals("[]") ? "None" : codesText
                ));
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        troubleCodesDialog.dismiss();
                    }
                });
                troubleCodesDialog.show();
            }
        });

        return view;
    }

    private DataPoint[] getRPMData() {
        String[] columns = {"time", "RPM"};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("RPMTable", columns, null, null, null, null, null);
        DataPoint[] dataPoints = new DataPoint[cursor.getCount()];
        for(int i = 0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            dataPoints[i] = new DataPoint(cursor.getLong(0), cursor.getDouble(1));
        }

        return dataPoints;
    }

    private DataPoint[] getSpeedData(){
        String[] columns = {"time", "speed"};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("SpeedTable", columns, null, null, null, null, null);
        DataPoint[] dataPoints = new DataPoint[cursor.getCount()];
        for(int i = 0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            dataPoints[i] = new DataPoint(cursor.getLong(0), cursor.getDouble(1));
        }

        return dataPoints;
    }


}
