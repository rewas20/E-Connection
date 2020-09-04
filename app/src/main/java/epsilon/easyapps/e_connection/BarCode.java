package epsilon.easyapps.e_connection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;

public class BarCode extends AppCompatActivity implements View.OnClickListener {
    Button skip;
    ImageButton scanQR;
    public static TextView verfiyCode;

    Set<BluetoothDevice> devices;
    BluetoothAdapter myAdapter;
    BluetoothDevice mBluetoothDevice = null;


  //  static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        skip = findViewById(R.id.skip_scan);
        scanQR = findViewById(R.id.btn_scan);
        verfiyCode = findViewById(R.id.verify_bluetooth);

        myAdapter = BluetoothAdapter.getDefaultAdapter();

        skip.setOnClickListener(this);
        scanQR.setOnClickListener(this);


    }

    public void startProcess() {
        Intent processActivity = new Intent(BarCode.this, ProcessesActivity.class);
        processActivity.putExtra("device",mBluetoothDevice);
        startActivity(processActivity);
        finish();
    }

    public void startScanActivity() {
        Intent scanCodeActivity = new Intent(BarCode.this, ScanCodeActivity.class);
        startActivity(scanCodeActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip_scan:
                startProcess();
                break;
            case R.id.btn_scan:
                //startScanActivity();
                break;

        }

    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit.....!!");
        builder.setIcon(R.drawable.exit);
        builder.setMessage("Are you sure you want to exit ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BarCode.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void enableBluetooth() {

        if(myAdapter==null){
            startProcess();
        }
        else if (myAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is enable", Toast.LENGTH_SHORT).show();
             devices = myAdapter.getBondedDevices();

            if(devices.size() >0){
                for (BluetoothDevice device : devices){

                        if(device.getName().contains("HC")){
                            mBluetoothDevice = device;
                            verfiyCode.setText("connect with "+device.getName());

                        }


                }
                if(mBluetoothDevice==null){
                    Toast.makeText(this, "no connection", Toast.LENGTH_SHORT).show();
                    startProcess();
                }
            }



        }
        else if(!myAdapter.isEnabled()) {
            startProcess();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        enableBluetooth();

    }



}