package epsilon.easyapps.e_connection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ProcessesActivity extends AppCompatActivity implements View.OnClickListener {

    LottieAnimationView deposit, withdrawal;
    TextInputLayout numberNeed;
    TextInputEditText numberNeed_editText;
    TextView doProcessText;
    Button endProcess, doProcess;
    RelativeLayout debit, credit;
    Dialog dialog;
    BluetoothDevice device=null;
    FirebaseAuth auth;

    ProgressDialog progressDialog;
    Timer timer;
    BluetoothAdapter myBluetooth;
    BluetoothSocket socket = null;

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);
        auth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_chose_credit);
        dialog.setCancelable(false);
        dialog.show();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("doing process");
        progressDialog.setMessage("Please wait.....");

        timer = new Timer();
        Intent intent = getIntent();
        if (intent != null){
            device =intent.getParcelableExtra("device");
        }

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        deposit =findViewById(R.id.depositProcess);
        withdrawal = findViewById(R.id.withdrawalProcess);
        numberNeed = findViewById(R.id.numberNeed);
        numberNeed_editText = findViewById(R.id.numberNeed_editText);
        doProcessText = findViewById(R.id.textView_enterNumber);
        doProcess = findViewById(R.id.btn_doProcess);
        endProcess = findViewById(R.id.btn_endProcess);


        deposit.setOnClickListener(this);
        withdrawal.setOnClickListener(this);
        doProcess.setOnClickListener(this);
        endProcess.setOnClickListener(this);


        if(device!=null){
            try {
                bluetooth_connect_device();
            } catch (IOException e) {
                startDoProcess();

            }
        }




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.depositProcess:
                startDoProcess();
                break;
            case R.id.withdrawalProcess:
                startDoProcess();
                break;
            case R.id.btn_doProcess:
                sendNumber();
                break;
            case R.id.btn_endProcess:
                startEndProcess();
                break;


        }

    }

    private void startEndProcess() {
    if(device!=null) {
        numberNeed.setVisibility(View.INVISIBLE);
        doProcessText.setVisibility(View.INVISIBLE);
        doProcess.setVisibility(View.INVISIBLE);
        endProcess.setVisibility(View.INVISIBLE);
    }
    }

    private void startDoProcess() {
       /**/
       if(device!=null&&socket.isConnected()){
        numberNeed.setVisibility(View.VISIBLE);
        numberNeed_editText.setText("");
        doProcessText.setVisibility(View.VISIBLE);
        doProcess.setVisibility(View.VISIBLE);
        endProcess.setVisibility(View.VISIBLE);
    }else {
           Intent doProcess = new Intent(ProcessesActivity.this,DoProccessActivity.class);
           startActivity(doProcess);
       }

    }
    public void selectCard(View v){

        dialog.dismiss();
    }

    public void startAccountActivity(){
        Intent startAccount=new Intent(ProcessesActivity.this,AccountActivity.class);
        startActivity(startAccount);
        finish();
    }
    public void startBarCode(){
        Intent startCode=new Intent(ProcessesActivity.this,BarCode.class);
        startActivity(startCode);
        finish();
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit.....!!");
        builder.setIcon(R.drawable.exit);
        builder.setMessage("Are you sure you want to exit ?");
        builder.setCancelable(false);
        builder.setNeutralButton("log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startAccountActivity();

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProcessesActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendNumber() {


        if(!myBluetooth.isEnabled()||device.getBondState()!=BluetoothDevice.BOND_BONDED||!socket.isConnected()) {
            startDoProcess();
            Toast.makeText(ProcessesActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
        }
        else if(myBluetooth.isEnabled()&&device.getBondState()==BluetoothDevice.BOND_BONDED) {
            try {
                progressDialog.show();
                String number=numberNeed_editText.getText().toString();
                send(Integer.valueOf(number));
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        numberNeed_editText.setText("");

                    }
                },5000);

            }
            catch (Exception e){

            }

        }

    }

    private void bluetooth_connect_device() throws IOException {

        int counter = 0;
        do {

            try {

                    BluetoothDevice dispositive = myBluetooth.getRemoteDevice(device.getAddress());
                    socket = dispositive.createRfcommSocketToServiceRecord(mUUID);
                    socket.connect();
                    Toast.makeText(this, "Connected with :" + device.getName(), Toast.LENGTH_SHORT).show();



            } catch (Exception e) {

                startDoProcess();
                Toast.makeText(getApplicationContext(), "can't connect\n device not has any receiver", Toast.LENGTH_LONG).show();

            }
            counter++;
        }while (!socket.isConnected()&&counter<3);
    }
    private void send(int bytes) throws IOException {

        try {

            if(socket!=null) {
                socket.getOutputStream().write(bytes);
            }
        }
        catch (Exception e){
            socket.close();
            startDoProcess();
            Toast.makeText(getApplicationContext(), "can't send", Toast.LENGTH_SHORT).show();

        }


    }


}