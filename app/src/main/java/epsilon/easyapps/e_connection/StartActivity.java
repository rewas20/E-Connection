package epsilon.easyapps.e_connection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent startApp = new Intent(StartActivity.this,MainActivity.class);
                startActivity(startApp);
                finish();
            }
        },5000);
    }

    @Override
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
                Toast.makeText(StartActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
    });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}