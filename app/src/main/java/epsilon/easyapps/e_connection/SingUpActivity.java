package epsilon.easyapps.e_connection;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SingUpActivity extends AppCompatActivity {
   Toolbar returnToLogin;
   TextInputEditText userName,PhoneNumber,email,password;
   Button create;
    LottieAnimationView animationView;
    Timer timer;

   DatabaseReference users;
   FirebaseAuth auth;
   ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
            returnToLogin =(Toolbar) findViewById(R.id.toolbar_singUp);
            setSupportActionBar(returnToLogin);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please wait.....");
            progressDialog.setCancelable(false);



            users = FirebaseDatabase.getInstance().getReference().child("Users");
            auth = FirebaseAuth.getInstance();

            userName = findViewById(R.id.user_name_create_editText);
            PhoneNumber = findViewById(R.id.PhoneNumber_editText);
            email = findViewById(R.id.email_create_editText);
            password = findViewById(R.id.password_create_editText);
            create = findViewById(R.id.btn_create);
        animationView = findViewById(R.id.check_login);
        timer = new Timer();

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String user_name = userName.getText().toString();
                    String phone_number = PhoneNumber.getText().toString();
                    String email_user = email.getText().toString();
                    final String password_user = password.getText().toString();

                    if (user_name.isEmpty()) {
                        userName.setError("Please enter your UserName");
                        userName.requestFocus();
                    } else if (phone_number.isEmpty()) {
                        PhoneNumber.setError("Please enter your Phone Number");
                        PhoneNumber.requestFocus();
                    } else if (email_user.isEmpty()) {
                        email.setError("Please enter your Email");
                        email.requestFocus();
                    } else if (password_user.isEmpty()) {
                        password.setError("Please enter any Password");
                        password.requestFocus();
                    } else if (user_name.isEmpty() && phone_number.isEmpty() && email_user.isEmpty() && password_user.isEmpty()) {
                        Toast.makeText(SingUpActivity.this, "Fields Are Empty", Toast.LENGTH_SHORT).show();

                    } else if (!user_name.isEmpty() && !phone_number.isEmpty() && !email_user.isEmpty() && !password_user.isEmpty()) {
                        progressDialog.show();
                        final HashMap userMap = new HashMap();
                        userMap.put("user_name", user_name);
                        userMap.put("phone_number", phone_number);
                        userMap.put("email", email_user);
                        userMap.put("password", password_user);

                        auth.createUserWithEmailAndPassword(email_user, password_user).addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = auth.getCurrentUser().getUid();
                                    users.child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @RequiresApi(api = Build.VERSION_CODES.P)
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                checkFingerPrint();
                                            }
                                            else {
                                                Toast.makeText(SingUpActivity.this, "SingUp Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });



                                } else {

                                    Toast.makeText(SingUpActivity.this, "SingUp Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
                });
            }
            public void startMainActivity(){
                Intent startMain = new Intent(SingUpActivity.this, MainActivity.class);
                startActivity(startMain);
                finish();

            }
        @RequiresApi(api = Build.VERSION_CODES.P)
        public void checkFingerPrint(){
                final Executor executor = Executors.newSingleThreadExecutor();
                final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(SingUpActivity.this)
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
                        SingUpActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                animationView.setVisibility(View.VISIBLE);
                                animationView.playAnimation();
                                Toast.makeText(SingUpActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        startMainActivity();

                                    }
                                }, 2000);


                            }
                        });
                    }
                });
            }
    public void onBackPressed() {
      finish();
    }
}