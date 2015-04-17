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

    static boolean Lock;
    static boolean AutoScrollX;
    static boolean Stream;

    Button bXminus;
    Button bXplus;

    ToggleButton tbLock;
    ToggleButton tbScroll;
    ToggleButton toggleStream;

    static LinearLayout GraphView;
    static GraphView graphView;
    static GraphViewSeries Series;

    private static double graph2LastXValue = 0;
    private static int Xview=10;
    Button bluetoothConnect, bluetoothDisconnect;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case Bluetooth.SUCCESS_CONNECT:
                    Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket) msg.obj);
                    Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
                    String s = "successfully connected";
                    Bluetooth.connectedThread.start();
                    break;

                case Bluetooth.MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, 5);                 // create string from bytes array

                    Log.d("strIncom", strIncom);
                    if (strIncom.indexOf('.')==2 && strIncom.indexOf('s')==0){
                        strIncom = strIncom.replace("s", "");
                        if (isFloatNumber(strIncom)){
                            Series.appendData(new GraphViewData(graph2LastXValue,Double.parseDouble(strIncom)),AutoScrollX);
                            graphView.redrawAll();

                            //X-axis control
                            if (graph2LastXValue >= Xview && Lock == true){
                                Series.resetData(new GraphViewData[]{});
                                graph2LastXValue = 0;
                            } else
                                graph2LastXValue += 0.1;

                            if(Lock == true)
                                graphView.setViewPort(0, Xview);
                            else
                                graphView.setViewPort(graph2LastXValue-Xview, Xview);

                            //refresh
                            GraphView.removeView(graphView);
                            GraphView.addView(graphView);
                            graphView.invalidate();
                        }
                    }
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

        GraphView = (LinearLayout)findViewById(R.id.hrGraph);
        GraphView.setBackgroundColor(Color.BLACK);
        Series = new GraphViewSeries("Signal", new GraphViewStyle(Color.GREEN, 2), new GraphViewData[] {new GraphViewData(0, 0)});
        graphView = new LineGraphView(this, "Heart Rate");

        graphView.setViewPort(0, Xview);
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);
        graphView.setManualYAxis(true);
        graphView.setManualYAxisBounds(5, 0);
        graphView.addSeries(Series);
        GraphView.addView(graphView);
    }

    void initializeButtons(){
        bluetoothConnect = (Button)findViewById(R.id.btConnect);
        bluetoothConnect.setOnClickListener(this);

        bluetoothDisconnect = (Button)findViewById(R.id.btDisconnect);
        bluetoothDisconnect.setOnClickListener(this);

        bXminus = (Button)findViewById(R.id.bXminus);
        bXminus.setOnClickListener(this);

        bXplus = (Button)findViewById(R.id.bXplus);
        bXplus.setOnClickListener(this);

        tbLock = (ToggleButton)findViewById(R.id.tbLock);
        tbLock.setOnClickListener(this);

        tbScroll = (ToggleButton)findViewById(R.id.tbScroll);
        tbScroll.setOnClickListener(this);

        toggleStream = (ToggleButton)findViewById(R.id.streamToggle);
        toggleStream.setOnClickListener(this);

        Lock = true;
        AutoScrollX = true;
        Stream = true;
    }

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
                if (Xview>1) Xview--;
                break;
            case R.id.bXplus:
                if (Xview<30) Xview++;
                break;
            case R.id.tbLock:
                if (tbLock.isChecked()){
                    Lock = true;
                }else{
                    Lock = false;
                }
                break;
            case R.id.tbScroll:
                if (tbScroll.isChecked()){
                    AutoScrollX = true;
                }else{
                    AutoScrollX = false;
                }
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
