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
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (Bluetooth.connectedThread != null) {
            Bluetooth.connectedThread.write("Q");}//Stop streaming
        super.onBackPressed();
    }

    static boolean Lock;//whether lock the x-axis to 0-5
    static boolean AutoScrollX;//auto scroll to the last x value
    static boolean Stream;//Start or stop streaming

    static LinearLayout GraphView;
    static GraphView graphView;
    static GraphViewSeries Series;

    Button bluetoothConnect;
    Button bluetoothDisconnect;
    ToggleButton toggleStream;

    private static double graph2LastXValue = 0;
    private static int Xview=10;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what){
                case Bluetooth.SUCCESS_CONNECT:
                    Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
                    String s = "successfully connected";
                    Bluetooth.connectedThread.start();
                    break;
            }
        }

        public boolean isFloatNumber(String num){
            //Log.d("checkfloatNum", num);
            try{
                Double.parseDouble(num);
            } catch(NumberFormatException nfe) {
                return false;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initializeGraph();
        initializeButtons();
    }

    void initializeGraph(){

        Bluetooth.gethandler(mHandler);

        //init graphview
        GraphView = (LinearLayout) findViewById(R.id.hrGraph);
        GraphView.setBackgroundColor(Color.BLACK);
        // init example series data-------------------
        Series = new GraphViewSeries("Signal",
                new GraphViewStyle(Color.GREEN, 2),//color and thickness of the line
                new GraphViewData[] {new GraphViewData(0, 0)});
        graphView = new LineGraphView(
                this // context
                , "Graph" // heading
        );

        graphView.setViewPort(0, Xview);
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);
        graphView.setManualYAxis(true);
        graphView.setManualYAxisBounds(5, 0);
        graphView.addSeries(Series); // data
        GraphView.addView(graphView);
    }

    void initializeButtons(){
        bluetoothConnect = (Button)findViewById(R.id.btConnect);
        bluetoothConnect.setOnClickListener(this);
        bluetoothDisconnect = (Button)findViewById(R.id.btDisconnect);
        bluetoothDisconnect.setOnClickListener(this);
        toggleStream = (ToggleButton)findViewById(R.id.streamToggle);
        toggleStream.setOnClickListener(this);
    }

    public void onClick(View v){

        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.btConnect:
                startActivity(new Intent("android.intent.action.BT1"));
                break;
            case R.id.btDisconnect:
                Bluetooth.disconnect();
                Toast.makeText(getApplicationContext(), "Disconnected!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.streamToggle:
                if (toggleStream.isChecked()){
                    if(Bluetooth.connectedThread != null)
                        Bluetooth.connectedThread.write("E");
                } else {
                    if (Bluetooth.connectedThread != null)
                        Bluetooth.connectedThread.write("Q");
                }
                break;
        }
    }
}
