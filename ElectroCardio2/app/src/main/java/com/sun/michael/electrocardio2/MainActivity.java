package com.sun.michael.electrocardio2;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

    // Create variables
    static boolean Lock;
    static boolean AutoScrollX;
    static boolean Stream;

    Button xMinus;
    Button xPlus;

    ToggleButton lockToggle;
    ToggleButton scrollToggle;
    ToggleButton streamToggle;

    static LinearLayout GraphView;
    static GraphView graphView;
    static GraphViewSeries Series;

    private static double graphLastXValue = 0;
    private static int xView = 10;

    Button bluetoothConnect, bluetoothDisconnect;

    /** Handler for managing the threads. */
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case Bluetooth.SUCCESS_CONNECT:
                    Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(),"Connected!",Toast.LENGTH_SHORT).show();
                    //String s = "successfully connected";
                    Bluetooth.connectedThread.start();
                    break;

                case Bluetooth.MESSAGE_READ:

                    byte[] readBuf = (byte[])msg.obj;
                    String strIncom = new String(readBuf,0,5);                 //Create string from bytes array

                    Log.d("strIncom", strIncom);
                    if (strIncom.indexOf('.') == 2 && strIncom.indexOf('s') == 0){
                        strIncom = strIncom.replace("s","");
                        if (isFloatNumber(strIncom)){
                            Series.appendData(new GraphViewData(graphLastXValue,
                                    Double.parseDouble(strIncom)),AutoScrollX);

                            //X-axis control
                            if (graphLastXValue >= xView && Lock){
                                Series.resetData(new GraphViewData[]{});
                                graphLastXValue = 0;
                            } else
                                graphLastXValue += 0.1;

                            if(Lock)
                                graphView.setViewPort(0,xView);
                            else
                                graphView.setViewPort(graphLastXValue-xView,xView);

                            //Refresh
                            GraphView.removeView(graphView);
                            GraphView.addView(graphView);
                            graphView.redrawAll();
                            //graphView.invalidate();
                        }
                    }
                    break;
            }
        }

        public boolean isFloatNumber(String num){
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
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initializeGraph();
        initializeButtons();
    }

    /** Stop streaming if Back button pressed.*/
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (Bluetooth.connectedThread != null) {
            Bluetooth.connectedThread.write("Q");
        }
        super.onBackPressed();
    }

    void initializeGraph(){

        Bluetooth.getHandler(myHandler);

        GraphView = (LinearLayout)findViewById(R.id.hrGraph);
        GraphView.setBackgroundColor(Color.BLACK);
        Series = new GraphViewSeries("Signal",new GraphViewStyle(Color.GREEN,2),
                new GraphViewData[]{new GraphViewData(0,0)});
        graphView = new LineGraphView(this,"Heart Rate");

        graphView.setViewPort(0,xView);
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);
        graphView.setManualYAxis(true);
        graphView.setManualYAxisBounds(5,0);
        graphView.addSeries(Series);
        GraphView.addView(graphView);
    }

    void initializeButtons(){
        bluetoothConnect = (Button)findViewById(R.id.btConnect);
        bluetoothConnect.setOnClickListener(this);

        bluetoothDisconnect = (Button)findViewById(R.id.btDisconnect);
        bluetoothDisconnect.setOnClickListener(this);

        xMinus = (Button)findViewById(R.id.bXminus);
        xMinus.setOnClickListener(this);

        xPlus = (Button)findViewById(R.id.bXplus);
        xPlus.setOnClickListener(this);

        lockToggle = (ToggleButton)findViewById(R.id.tbLock);
        lockToggle.setOnClickListener(this);

        scrollToggle = (ToggleButton)findViewById(R.id.tbScroll);
        scrollToggle.setOnClickListener(this);

        streamToggle = (ToggleButton)findViewById(R.id.streamToggle);
        streamToggle.setOnClickListener(this);

        Lock = true;
        AutoScrollX = true;
        Stream = true;
    }

    /** OnClickListener method for the buttons.*/
    @Override
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
        case R.id.bXminus:
            if (xView > 1) xView--;
            break;
        case R.id.bXplus:
            if (xView < 30) xView++;
            break;
        case R.id.tbLock:
            if (lockToggle.isChecked()){
                Lock = true;
            }else{
                Lock = false;
            }
            break;
        case R.id.tbScroll:
            if (scrollToggle.isChecked()){
                AutoScrollX = true;
            }else{
                AutoScrollX = false;
            }
            break;
        case R.id.streamToggle:
            if (streamToggle.isChecked()){
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
