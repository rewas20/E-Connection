package epsilon.easyapps.e_connection;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.EnumMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    Button singUp,login;
    TextInputEditText email_login,paswword;
    TextView forgetPassword;
    LottieAnimationView animationView;
    Timer timer;

    DatabaseReference users;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Check Account");
        progressDialog.setMessage("Please wait.....");
        progressDialog.setCancelable(false);


        timer = new Timer();

        login = findViewById(R.id.btn_login);
        singUp = findViewById(R.id.btn_singup);
        email_login= findViewById(R.id.email_login_editText);
        paswword = findViewById(R.id.password_editText);
        forgetPassword = findViewById(R.id.forget_password);
        animationView = findViewById(R.id.check_login);


        login.setOnClickListener(this);
        singUp.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        users = FirebaseDatabase.getInstance().getReference().child("Users");




    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                String email = email_login.getText().toString();
                String password_user = paswword.getText().toString();
                if(email.isEmpty()){
                    email_login.setError("Please enter your User Name");
                    email_login.requestFocus();
                }else  if(password_user.isEmpty()){
                    paswword.setError("Please enter your password");
                    paswword.requestFocus();
                }else  if(password_user.isEmpty()&&email.isEmpty()){
                    Toast.makeText(this, "Fields Are Empty", Toast.LENGTH_SHORT).show();
                }else  if(!password_user.isEmpty()&&!email.isEmpty()){
                    progressDialog.show();
                    loginToAccount(email,password_user);

                }
                break;
            case R.id.btn_singup:
                startCreateAccount();
            case R.id.forget_password:


        }
    }

    private void startCreateAccount() {
        Intent startCreate =new Intent(AccountActivity.this,SingUpActivity.class);
        startActivity(startCreate);
    }
    private void startMainActivity() {
        Intent startProcesses =new Intent(AccountActivity.this,MainActivity.class);
        startActivity(startProcesses);
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void checkFingerPrint(){
        final Executor executor = Executors.newSingleThreadExecutor();
        final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(AccountActivity.this)
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
                AccountActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                        Toast.makeText(AccountActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                startMainActivity();
                            }
                        },2000);


                    }
                });
            }
        });
    }

    public void onBackPressed() {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
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
                Toast.makeText(AccountActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationView.setVisibility(View.INVISIBLE);
    }


    public void loginToAccount(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    checkFingerPrint();
                }else{
                    Toast.makeText(AccountActivity.this, "Password or User name is incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}