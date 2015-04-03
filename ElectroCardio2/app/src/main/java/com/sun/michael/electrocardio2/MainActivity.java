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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends Activity implements View.OnClickListener{

    static LinearLayout GraphView;
    static GraphView graphView;
    static LineGraphSeries Series;
    Button bluetoothConnect;
    Button bluetoothDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonInit();
    }

    void buttonInit(){
        graphView = (GraphView)findViewById(R.id.heartRateGraph);
        bluetoothConnect = (Button)findViewById(R.id.btConnect);
        bluetoothConnect.setOnClickListener(this);
        bluetoothDisconnect = (Button)findViewById(R.id.btDisconnect);
        bluetoothDisconnect.setOnClickListener(this);
    }

    public void onClick(View v){

        switch(v.getId()){
            case R.id.btConnect:
                startActivity(new Intent("android.intent.action.BT1"));
                break;
            case R.id.btDisconnect:
                Bluetooth.disconnect();
                break;
        }
    }
}
