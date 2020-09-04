package epsilon.easyapps.e_connection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hanks.passcodeview.PasscodeView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private PasscodeView passcodeView;
    LottieAnimationView unlock;
    MediaPlayer click;
    TextView Incorrect;
    Button button0,button1,button2,button3,button4,button5,button6,button7,button8,button9;
    ImageButton back_space,check_code;
    ImageView circleEmpty1,circleEmpty2,circleEmpty3,circleEmpty4;
    int ENTER_CODE_PLACE =0;
    String NUMBER_CODE="";
    ProgressDialog verifyCode;
    Timer timer;

    FirebaseAuth auth;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        verifyCode = new ProgressDialog(MainActivity.this);
        verifyCode.setTitle("Verifying");
        verifyCode.setMessage("Please wait....");
        timer = new Timer();

        //define all buttons
        button0 = findViewById(R.id.btn_number0);
        button1 = findViewById(R.id.btn_number1);
        button2 = findViewById(R.id.btn_number2);
        button3 = findViewById(R.id.btn_number3);
        button4 = findViewById(R.id.btn_number4);
        button5 = findViewById(R.id.btn_number5);
        button6 = findViewById(R.id.btn_number6);
        button7 = findViewById(R.id.btn_number7);
        button8 = findViewById(R.id.btn_number8);
        button9 = findViewById(R.id.btn_number9);
        check_code = findViewById(R.id.check_code);
        back_space = findViewById(R.id.back_space);
        circleEmpty1 = findViewById(R.id.circle_empty1);
        circleEmpty2 = findViewById(R.id.circle_empty2);
        circleEmpty3 = findViewById(R.id.circle_empty3);
        circleEmpty4 = findViewById(R.id.circle_empty4);
        Incorrect =findViewById(R.id.error);
        unlock = findViewById(R.id.unlock);

        //add to ClickListener action for all buttons
        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        check_code.setOnClickListener(this);
        back_space.setOnClickListener(this);
        click = MediaPlayer.create(this,R.raw.bubble);
        auth = FirebaseAuth.getInstance();

    }
    // add action for all buttons
    @Override
    public void onClick(View v) {
        click.start();
       switch (v.getId()) {
           case R.id.btn_number0:
               pushNumber(button0.getText().toString());
               break;
           case R.id.btn_number1:
               pushNumber(button1.getText().toString());
               break;
           case R.id.btn_number2:
               pushNumber(button2.getText().toString());
               break;
           case R.id.btn_number3:
               pushNumber(button3.getText().toString());
               break;
           case R.id.btn_number4:
               pushNumber(button4.getText().toString());
               break;
           case R.id.btn_number5:
               pushNumber(button5.getText().toString());
               break;
           case R.id.btn_number6:
               pushNumber(button6.getText().toString());
               break;
           case R.id.btn_number7:
               pushNumber(button7.getText().toString());
               break;
           case R.id.btn_number8:
               pushNumber(button8.getText().toString());
               break;
           case R.id.btn_number9:
               pushNumber(button9.getText().toString());
               break;
           case R.id.back_space:
               removeOne();
               break;
           case R.id.check_code:
               checkIsCorrect();
               break;

       }
    }
    //save the number is entered
    public void pushNumber(String number) {
        if (ENTER_CODE_PLACE < 4) {
            ENTER_CODE_PLACE++;
            NUMBER_CODE+=number;
            if (ENTER_CODE_PLACE == 1) {
                circleEmpty1.setImageResource(R.drawable.circle_select);
            } else if (ENTER_CODE_PLACE == 2) {
                circleEmpty2.setImageResource(R.drawable.circle_select);
            }
            else if (ENTER_CODE_PLACE == 3) {
                circleEmpty3.setImageResource(R.drawable.circle_select);
            } else if (ENTER_CODE_PLACE == 4) {
                circleEmpty4.setImageResource(R.drawable.circle_select);
            }
        }

    }

    //remove last one number
    public void removeOne() {
        if (ENTER_CODE_PLACE >0) {
            ENTER_CODE_PLACE--;
            if (ENTER_CODE_PLACE == 0) {
                circleEmpty1.setImageResource(R.drawable.circle_check);
                NUMBER_CODE = "";
            } else if (ENTER_CODE_PLACE == 1) {
                circleEmpty2.setImageResource(R.drawable.circle_check);
                NUMBER_CODE = NUMBER_CODE.substring(0,1);
            } else if (ENTER_CODE_PLACE == 2) {
                circleEmpty3.setImageResource(R.drawable.circle_check);
                NUMBER_CODE = NUMBER_CODE.substring(0,2);
            } else if (ENTER_CODE_PLACE == 3) {
                circleEmpty4.setImageResource(R.drawable.circle_check);
                NUMBER_CODE = NUMBER_CODE.substring(0,3);
            }
        }
    }

    //check is correct code or not
    public void checkIsCorrect(){
        verifyCode.show();
        if(Integer.valueOf(NUMBER_CODE)==1289){
            verifyCode.dismiss();
            unlock.playAnimation();
            Incorrect.setTextColor(Integer.valueOf(Color.WHITE));
            Incorrect.setText("Correct");

            timer.schedule(new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void run() {
                    isConnected();
                }
            },1400);



        }else {
            Incorrect.setTextColor(Integer.valueOf(Color.DKGRAY));
            Incorrect.setText("Try again please");
            circleEmpty1.setImageResource(R.drawable.circle_check);
            circleEmpty2.setImageResource(R.drawable.circle_check);
            circleEmpty3.setImageResource(R.drawable.circle_check);
            circleEmpty4.setImageResource(R.drawable.circle_check);
            ENTER_CODE_PLACE=0;
            NUMBER_CODE = "";
            verifyCode.dismiss();
        }
    }
    public void onBackPressed() {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit.....!!");
        builder.setIcon(R.drawable.exit);
        builder.setMessage("Are you sure you want to exit ?");
        builder.setCancelable(false);
        builder.setNeutralButton("log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                onStart();
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
                Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void startAccountActivity(){
        Intent startAccount=new Intent(MainActivity.this,AccountActivity.class);
        startActivity(startAccount);
        finish();
    }
    public void startBarCodeActivity(){
        Intent barCode = new Intent(MainActivity.this,BarCode.class);
        startActivity(barCode);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null){
            startAccountActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void checkFingerPrint(){
        final Executor executor = Executors.newSingleThreadExecutor();
        final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(MainActivity.this)
                .setTitle("Fingerprint Authentication")
                .setSubtitle("for security bank")
                .setDescription("can enter to your account in bank")
                .setNegativeButton("Cancel", executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).build();
        biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MainActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                startBarCodeActivity();
                            }
                        },2000);


                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected==false) {
            checkFingerPrint();
        }else {
            startBarCodeActivity();
        }

    }
}