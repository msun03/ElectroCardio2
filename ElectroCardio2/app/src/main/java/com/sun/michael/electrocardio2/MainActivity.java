package com.sun.michael.electrocardio2;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends Activity implements View.OnClickListener{

    GraphView graphView;
    LineGraphSeries Series;

    Button bluetoothConnect;
    Button bluetoothDisconnect;
    ToggleButton toggleStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonInit();
        initializeGraph();
    }

    void buttonInit(){
        bluetoothConnect = (Button)findViewById(R.id.btConnect);
        bluetoothConnect.setOnClickListener(this);
        bluetoothDisconnect = (Button)findViewById(R.id.btDisconnect);
        bluetoothDisconnect.setOnClickListener(this);
        toggleStream = (ToggleButton)findViewById(R.id.streamToggle);
    }

    void initializeGraph(){

        graphView = (GraphView)findViewById(R.id.hrGraph);

        /*LineGraphSeries<DataPoint> exampleSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
        });*/

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(5.0);
        //graphView.addSeries(exampleSeries);
    }

    public void onClick(View v){

        switch(v.getId()){
            case R.id.btConnect:
                startActivity(new Intent("android.intent.action.BT1"));
                break;
            case R.id.btDisconnect:
                Bluetooth.disconnect();
                break;
            case R.id.streamToggle:
                if (toggleStream.isChecked()){
                    if(Bluetooth.connectedThread != null)
                        Bluetooth.connectedThread.write("E");
                } else {
                    if (Bluetooth.connectedThread != null)
                        Bluetooth.connectedThread.write("Q");
                }
        }
    }
}
